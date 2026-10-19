import { defineConfig, devices } from '@playwright/test'
import { BASE_URL, VIEWPORT } from './e2e/helpers.js'

// End-to-end tests (npm run test:e2e) against the whole app started with `docker compose up` at the repository root.
export default defineConfig({
  testDir: './e2e',
  // The scenarios share the demo data: one at a time.
  workers: 1,
  timeout: 90_000,
  expect: { timeout: 10_000 },
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    ...devices['Desktop Chrome'],
    baseURL: BASE_URL,
    viewport: VIEWPORT,
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
  },
})
