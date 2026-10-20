import { defineConfig, devices } from '@playwright/test'
import { BASE_URL, VIEWPORT } from './e2e/helpers.js'

export default defineConfig({
  testDir: './e2e',
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
