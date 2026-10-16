# VoixDeSagesse - Backend

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

**A modern Spring Boot REST API for sharing inspirational content and wisdom**

[Features](#features) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started) • [API Documentation](#api-documentation) • [Frontend](#frontend)

</div>

---

## 📖 Overview

VoixDeSagesse (Voice of Wisdom) is a full-stack web platform that enables users to create, share, and discover inspirational content. This repository contains the backend API built with Spring Boot, providing a robust, secure, and scalable REST API with JWT authentication, social features, and collaborative moderation.

## ✨ Features

### 🔐 Authentication & Security
- **JWT-based authentication** with token expiration management
- **OTP verification system** via email for password recovery
- **BCrypt password encryption** for secure credential storage
- **Role-based access control** (User, Admin)
- **CORS configuration** for secure cross-origin requests

### 📝 Content Management
- **Two article types**: Wisdom (quotes/citations) and Story (narratives)
- **Categorization system** with multiple themes (Personal Development, Spirituality, etc.)
- **Tag-based organization** for enhanced discoverability
- **Advanced search functionality** with filters
- **Article CRUD operations** with validation

### 👥 Social Features
- **User following system** with follower/following management
- **Like/Unlike articles** with real-time counter updates
- **Commenting system** with soft delete support
- **Article bookmarking** for personalized collections
- **User profiles** with statistics and activity tracking

### 🛡️ Moderation & Administration
- **Collaborative reporting system** for inappropriate content
- **Admin dashboard** with comprehensive statistics
- **Signal workflow** (Pending, Approved, Rejected states)
- **Content moderation tools** with audit trail
- **Automated cleanup** of expired OTP codes

### 🚀 Technical Features
- **Multi-layered architecture** with clear separation of concerns
- **Optimized MongoDB queries** with compound indexes
- **File upload support** for profile pictures
- **Centralized error handling** with internationalized messages
- **Comprehensive validation** with Jakarta Validation
- **Unit & integration tests** with JUnit and Mockito
- **Docker support** for containerized deployment

## 🛠️ Tech Stack

### Core Framework
- **Spring Boot 3.4.4** - Main application framework
- **Java 17** - Programming language with LTS support
- **Maven** - Dependency management and build tool

### Database & Persistence
- **MongoDB 7.0** - NoSQL database for flexible data storage
- **Spring Data MongoDB** - Data access layer abstraction

### Security
- **Spring Security 6.4** - Authentication and authorization
- **JWT (jjwt 0.11.5)** - Stateless token-based authentication
- **BCrypt** - Password hashing algorithm

### Email & Communication
- **Spring Boot Mail** - SMTP integration for email notifications
- **OTP System** - One-time password generation and verification

### Development Tools
- **Lombok** - Reduces boilerplate code
- **Spring Boot DevTools** - Hot reload during development
- **SLF4J** - Logging facade
- **Jakarta Validation** - Data validation framework

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests
- **Spring Test** - Integration testing support

### DevOps
- **Docker** - Containerization platform
- **Spring Boot Actuator** - Application monitoring and metrics

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Maven 3.8+** for dependency management
- **MongoDB 7.0+** (local installation or MongoDB Atlas)
- **Docker** (optional, for containerized deployment)
- **Git** for version control

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/voix-de-sagesse-backend.git
cd voix-de-sagesse-backend
```

### 2. Configure MongoDB

**Option A: Local MongoDB**
```bash
# Install MongoDB locally and start the service
mongod --dbpath /path/to/data/directory
```

**Option B: MongoDB Atlas**
- Create a free cluster on [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
- Get your connection string

### 3. Configure Application Properties

Edit `src/main/resources/application.properties`:
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/voixdesagesse
spring.data.mongodb.auto-index-creation=true

# JWT Configuration
jwt.secret=your-super-secret-key-at-least-32-characters-long
jwt.expiration=86400000

# Email Configuration (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# File Upload Configuration
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
file.upload-dir=uploads/profile-pictures
```

> **Note**: For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833) instead of your regular password.

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application

**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using JAR**
```bash
java -jar target/VoixDeSagesse-0.0.1-SNAPSHOT.jar
```

**Option C: Using Docker**
```bash
# Build Docker image
docker build -t voix-de-sagesse-backend .

# Run container
docker run -p 8080:8080 voix-de-sagesse-backend
```

The API will be available at `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | User login | No |
| POST | `/auth/sendOtp/{email}` | Send OTP for password reset | No |
| GET | `/auth/verifyOtp/{email}/{otp}` | Verify OTP code | No |
| POST | `/auth/changePass` | Change password with OTP | No |

### User Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/users/profile/{userId}` | Get user profile | Yes |
| PUT | `/users/updateProfile` | Update user profile | Yes |
| POST | `/users/upload-profile-picture` | Upload profile picture | Yes |
| POST | `/users/follow/{targetUserId}` | Follow a user | Yes |
| DELETE | `/users/unfollow/{targetUserId}` | Unfollow a user | Yes |
| GET | `/users/following` | Get following list | Yes |
| GET | `/users/my-followers` | Get followers list | Yes |
| POST | `/users/save-article/{articleId}` | Save article to favorites | Yes |
| DELETE | `/users/unsave-article/{articleId}` | Remove from favorites | Yes |
| DELETE | `/users/delete-account` | Delete user account | Yes |

### Article Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/articles/createSagesseArticla` | Create wisdom article | Yes |
| POST | `/articles/createHistoireArticla` | Create story article | Yes |
| GET | `/articles` | Get all articles | Yes |
| GET | `/articles/posts/{currentUserId}` | Get personalized feed | Yes |
| GET | `/articles/persoArticle/{userId}` | Get user's articles | Yes |
| GET | `/articles/saved/{userId}` | Get saved articles | Yes |
| POST | `/articles/{id}/like` | Like an article | Yes |
| POST | `/articles/{id}/unlike` | Unlike an article | Yes |
| DELETE | `/articles/delete/{articleId}/{userId}` | Delete article | Yes |
| GET | `/articles/search/{currentUserId}` | Search articles | Yes |

### Comment Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/comments/add/{articleId}` | Add comment to article | Yes |
| GET | `/comments/article/{articleId}` | Get article comments | Yes |
| DELETE | `/comments/delete/{commentId}` | Delete comment | Yes |
| GET | `/comments/count/{articleId}` | Get comment count | Yes |

### Signal/Report Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/signals/report` | Report an article | Yes |
| GET | `/signals/pending` | Get pending reports | Admin |
| PUT | `/signals/process/{signalId}` | Process a report | Admin |
| GET | `/signals/by-reporter/{reporterId}` | Get user's reports | Yes |

### Admin Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/admin/dashboard` | Get dashboard statistics | Admin |
| GET | `/admin/users` | Get all users | Admin |
| GET | `/admin/signals` | Get all signals | Admin |
| PUT | `/admin/signals/{signalId}/process` | Process signal | Admin |

### Request/Response Examples

**Register User**
```json
POST /auth/register
{
  "nom": "Doe",
  "prenom": "John",
  "email": "john.doe@example.com",
  "motDePass": "SecurePass123!",
  "accountType": "USER"
}
```

**Create Wisdom Article**
```json
POST /articles/createSagesseArticla
Headers: Authorization: Bearer {jwt-token}
{
  "type": "SAGESSE",
  "content": "The only way to do great work is to love what you do.",
  "source": "Steve Jobs",
  "categorie": "INSPIRATION",
  "tags": ["motivation", "work", "passion"]
}
```

**Login Response**
```json
{
  "userId": "1",
  "nom": "Doe",
  "prenom": "John",
  "email": "john.doe@example.com",
  "accountType": "USER",
  "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 🏗️ Project Structure
```
src/
├── main/
│   ├── java/com/project/VoixDeSagesse/
│   │   ├── config/                 # Configuration classes
│   │   │   ├── AppConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/             # REST Controllers
│   │   │   ├── AdminController.java
│   │   │   ├── ArticleController.java
│   │   │   ├── AuthController.java
│   │   │   ├── CommentController.java
│   │   │   ├── SignalController.java
│   │   │   └── UserController.java
│   │   ├── dto/                    # Data Transfer Objects
│   │   │   ├── UserDTO.java
│   │   │   ├── ArticleDTO.java
│   │   │   └── ...
│   │   ├── entity/                 # MongoDB Entities
│   │   │   ├── User.java
│   │   │   ├── Article.java
│   │   │   ├── Comment.java
│   │   │   ├── Signal.java
│   │   │   ├── Otp.java
│   │   │   └── Sequence.java
│   │   ├── exception/              # Exception Handling
│   │   │   ├── ArticlaException.java
│   │   │   └── ExceptionControllerAdvice.java
│   │   ├── repository/             # Spring Data Repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── ArticleRepository.java
│   │   │   └── ...
│   │   ├── security/               # Security Components
│   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtHelper.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   └── service/                # Business Logic Services
│   │       ├── AdminService.java
│   │       ├── ArticleService.java
│   │       ├── UserService.java
│   │       ├── EmailService.java
│   │       └── ...
│   └── resources/
│       ├── application.properties  # Application configuration
│       └── ValidationMessages.properties  # Validation messages
└── test/                           # Test classes
    └── java/com/project/VoixDeSagesse/
```

## 🧪 Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceImplTest
```

### Generate Test Coverage Report
```bash
mvn clean test jacoco:report
```

Test coverage reports will be available in `target/site/jacoco/index.html`

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t voix-de-sagesse-backend:latest .
```

### Run with Docker Compose

Create a `docker-compose.yml`:
```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:7.0
    container_name: voixdesagesse-mongodb
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: voixdesagesse

  backend:
    image: voix-de-sagesse-backend:latest
    container_name: voixdesagesse-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/voixdesagesse
      JWT_SECRET: your-secret-key-here
      SPRING_MAIL_USERNAME: your-email@gmail.com
      SPRING_MAIL_PASSWORD: your-app-password
    depends_on:
      - mongodb

volumes:
  mongodb_data:
```

Run with:
```bash
docker-compose up -d
```

## 🔒 Security Best Practices

This application implements several security measures:

- ✅ **JWT Authentication** with token expiration
- ✅ **Password Encryption** using BCrypt
- ✅ **Input Validation** with Jakarta Validation
- ✅ **CSRF Protection** (disabled for stateless API)
- ✅ **CORS Configuration** for secure cross-origin requests
- ✅ **Role-Based Access Control** (RBAC)
- ✅ **Secure Password Reset** with OTP verification
- ✅ **File Upload Validation** (size, type restrictions)
- ✅ **Centralized Exception Handling**
- ✅ **SQL Injection Prevention** (MongoDB parameterized queries)

## 🤝 Frontend Integration

This backend API is designed to work seamlessly with the VoixDeSagesse frontend application.

**Frontend Repository**: [VoixDeSagesse Frontend](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Frontend)

The frontend is built with:
- React 19.0
- Redux Toolkit for state management
- Tailwind CSS for styling
- Axios for API communication

## 📝 Environment Variables

Create a `.env` file or set environment variables:
```properties
# Database
MONGODB_URI=mongodb://localhost:27017/voixdesagesse

# JWT
JWT_SECRET=your-super-secret-key-minimum-32-characters
JWT_EXPIRATION=86400000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# File Upload
UPLOAD_DIR=uploads/profile-pictures
MAX_FILE_SIZE=5MB
```

## 🐛 Troubleshooting

### Common Issues

**MongoDB Connection Issues**
```
Solution: Ensure MongoDB is running and the connection string is correct
- Check MongoDB service status
- Verify credentials and network connectivity
- Check firewall settings
```

**JWT Token Expiration**
```
Solution: Tokens expire after 24 hours by default
- Implement token refresh mechanism
- Adjust jwt.expiration in application.properties
```

**CORS Errors**
```
Solution: Configure CORS in SecurityConfig
- Add frontend origin to allowed origins
- Verify HTTP methods are allowed
```

**Email Sending Failures**
```
Solution: Check SMTP configuration
- Use App Password for Gmail
- Enable "Less secure app access" (not recommended)
- Check firewall for port 587
```

## 📊 Performance Optimization

- **MongoDB Indexing**: Unique index on user email, compound indexes on queries
- **Lazy Loading**: User relationships loaded on demand
- **DTO Pattern**: Reduce data transfer overhead
- **Pagination**: Implemented for large result sets
- **Caching**: Consider Redis for session management (future enhancement)

## 📄 License

This project is part of an academic end-of-year project at **Hassan I University - Faculty of Sciences and Techniques of Settat**, Department of Computer Engineering.

**Academic Year**: 2024-2025  
**Author**: Oumaima LAGHJIBI

## 👥 Contact & Support

For questions, issues, or contributions:

- **Email**: laghjibioumaima2003@gmail.com
- **GitHub Issues**: [Report an issue](https://github.com/yourusername/voix-de-sagesse-backend/issues)
- **Frontend Repository**: [Voix-De-Sagesse-Frontend](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Frontend)

---

<div align="center">

**Built with ❤️ using Spring Boot and modern Java technologies**

⭐ Star this repository if you find it helpful!

</div>
