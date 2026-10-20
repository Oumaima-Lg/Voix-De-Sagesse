# Voix De Sagesse · frontend

React application of [Voix De Sagesse](../README.md), built with React 19, Vite 6, Redux Toolkit, Mantine 8 and Tailwind CSS 4.

## Run it

You need Node.js 20 or newer, and the API running on http://localhost:8080. From the repository root, `docker compose up -d backend` starts the API with its database. Then, in `frontend/`:

```bash
npm ci
npm run dev
```

The app opens on http://localhost:5173. To use another API, set `VITE_API_URL` in a `.env` file (see `.env.example`).

## Scripts

| Command | What it does |
|---|---|
| `npm run dev` | Development server with hot reload |
| `npm run build` | Production build in `dist/` |
| `npm run preview` | Serves the production build locally |
| `npm run lint` | ESLint |
| `npm test` | Unit tests (Vitest); `npm run test:watch` reruns them on each change |
| `npm run test:e2e` | End-to-end tests (Playwright) against the whole app started with `docker compose up` |
| `npm run test:e2e:report` | Opens the HTML report of the last end-to-end run |

Before the first end-to-end run, install the browser with `npx playwright install chromium`. Without Node, `e2e/run-in-docker.sh` runs them in Playwright's Docker image.

## Tests

- **Unit tests** (`src/**/*.test.js(x)`, Vitest and Testing Library): form validation, route guards, login, sign-up with the email code, post actions, notification bell, admin report review, API error handling, waking the API.
- **End-to-end tests** (`e2e/`, Playwright): a real browser goes through the main flows (sign-up, publishing, reactions, search, favorites, profile picture, password reset, protected demo accounts, admin), the moderation of reported articles, and notifications. They use the demo accounts and clean up after themselves.

## Code organization

`src/`:

| Folder | Contents |
|---|---|
| `pages` | Pages: landing (`homePage`), login and sign-up, user area (`espacePage`), admin |
| `components` | Shared components: post card, notification bell, and UI pieces |
| `api` | Calls to the API (Axios), with session and error handling |
| `hooks` | Shared logic, such as the actions on posts and waking the API |
| `store` | Redux state: current user and token |
| `routes` | Routes, loaded on demand, and access guards |
| `utils` | Form validation, notifications, dates, local storage |
| `animations`, `blocks` | Framer Motion variants and animated text |
| `test` | Test setup |

## Docker image

The `Dockerfile` builds the app with Node, then serves it with nginx as a non-root user. Unknown paths fall back to `index.html` for client-side routing. The API URL is set at build time with the `VITE_API_URL` build argument.
