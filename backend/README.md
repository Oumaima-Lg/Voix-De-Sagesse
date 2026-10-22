# Voix De Sagesse · backend

REST API of [Voix De Sagesse](../README.md), built with Spring Boot 4.1, Java 17 and MongoDB.

## Run it

The easiest way is to start the whole app with Docker Compose (see the [main README](../README.md)). To run only the API on your machine, you need Java 17 and MongoDB. From the repository root, `docker compose up -d mongo mailpit` starts MongoDB and the local mail catcher. Then, in `backend/`:

```bash
export JWT_SECRET=$(openssl rand -base64 48)
export DEMO_DATA=true DEMO_ADMIN_PASSWORD=Admin1234!   # optional: demo accounts and articles
./mvnw spring-boot:run
```

The API listens on http://localhost:8080, with its documentation at http://localhost:8080/swagger-ui.html.

## Configuration

Every setting that changes between environments comes from an environment variable.

| Variable | Default | Purpose |
|---|---|---|
| `JWT_SECRET` | none, required | Signs the tokens; at least 32 bytes, or the API refuses to start |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Token lifetime |
| `MONGODB_URI` | `mongodb://localhost:27017/voixdesagesse` | Database |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Frontends allowed to call the API, comma-separated |
| `MAIL_HOST`, `MAIL_PORT` | `localhost`, `1025` | SMTP server (Mailpit by default) |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | empty | SMTP credentials |
| `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS` | `false`, `false` | SMTP options |
| `MAIL_FROM` | `no-reply@voixdesagesse.local` | Sender address |
| `PICTURE_STORAGE` | `local` | Where profile pictures go: `local` (a folder) or `cloudinary` |
| `UPLOAD_DIR` | `uploads/profile-pictures` | Folder used by the `local` storage |
| `CLOUDINARY_URL` | empty | `cloudinary://<api_key>:<api_secret>@<cloud_name>`, required by the `cloudinary` storage |
| `EMAIL_CODES_PER_HOUR` | `5` | Sign-up and password codes one IP address can request per hour (Docker Compose raises it to 100 for local tests) |
| `PICTURE_UPLOADS_PER_HOUR` | `10` | Profile picture changes per user per hour |
| `CLIENT_IP_HEADER` | empty | Header holding the visitor's IP when a trusted proxy sets it (`True-Client-IP` on Render); empty uses the connection address |
| `PORT` | `8080` | HTTP port |
| `DEMO_DATA` | `false` | Create demo accounts and articles when the database is empty |
| `DEMO_USER_PASSWORD` | `Demo1234!` | Password of the shared demo accounts, which cannot change their password or profile, be deleted, or delete the demo articles |
| `DEMO_ADMIN_PASSWORD` | empty | Password of `admin@voixdesagesse.local`; no admin is created when empty |

## Tests

```bash
./mvnw test
```

Unit tests use Mockito. Integration tests call the API through MockMvc or a real HTTP server, against a MongoDB database named `voixdesagesse_test` on `localhost:27017` (`MONGODB_TEST_URI` to use another one). They cover authorization (acting as another user, admin routes), sign-up and password codes, error responses, moderation, notifications, profile pictures, the demo data and its protected accounts.

## Code organization

`src/main/java/com/voixdesagesse/`:

| Package | Contents |
|---|---|
| `controller` | REST endpoints, documented with springdoc-openapi |
| `service` | Business logic: users, articles, comments, reports, notifications |
| `repository` | Spring Data MongoDB repositories, including atomic updates |
| `entity`, `dto` | Stored documents and API payloads |
| `security` | JWT filter and helper, user details, login attempt limiter |
| `config` | Security rules, CORS, OpenAPI |
| `exception` | Business errors and the global handler that formats every error the same way |
| `mail` | Email templates and sending |
| `storage` | Profile pictures behind one interface: a local folder served by the API, or Cloudinary in production |
| `demo` | Demo data loaded on an empty database |

Error messages shown to users are in `src/main/resources/messages.properties`.

## Docker image

The `Dockerfile` builds the jar with Maven, then runs it on a Java 17 runtime image as a non-root user. To start faster on a small server, the image includes a class data sharing archive, created by a training run during the build, and uses the C1 compiler and the serial garbage collector. `/actuator/health` is public, for health checks.

With the `local` storage, pictures are written to `/app/uploads`, owned by the app user; Docker Compose keeps them in a volume. On Render the disk is not kept between restarts, so production uses `PICTURE_STORAGE=cloudinary`.
