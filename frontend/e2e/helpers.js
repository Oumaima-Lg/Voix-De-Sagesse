import { expect } from '@playwright/test'

export const BASE_URL = process.env.BASE_URL ?? 'http://localhost:5173'
export const API = process.env.API_URL ?? 'http://localhost:8080'
export const MAILPIT = process.env.MAILPIT_URL ?? 'http://localhost:8025'
export const VIEWPORT = { width: 1366, height: 768 }

// Demo accounts created by the backend on an empty database (see .env.example)
export const DEMO = 'demo@voixdesagesse.local'
export const KARIM = 'karim@voixdesagesse.local'
export const ADMIN = 'admin@voixdesagesse.local'
export const DEMO_PASSWORD = process.env.DEMO_USER_PASSWORD || 'Demo1234!'
export const ADMIN_PASSWORD = process.env.DEMO_ADMIN_PASSWORD || 'Admin1234!'

// Calls the API directly, to prepare data and check results. Throws on an error status.
export async function api(method, path, token, body) {
  const response = await fetch(API + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(token && { Authorization: `Bearer ${token}` }) },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  const text = await response.text()
  if (!response.ok) throw new Error(`${method} ${path} -> ${response.status} ${text.slice(0, 150)}`)
  try {
    return JSON.parse(text)
  } catch {
    return text // the like routes answer with plain text
  }
}

export const login = async (email, password = DEMO_PASSWORD) =>
  (await api('POST', '/auth/login', null, { email, password })).jwt

export const userId = (token) => JSON.parse(Buffer.from(token.split('.')[1], 'base64url').toString()).id

export async function emailsTo(email) {
  const query = encodeURIComponent(`to:"${email}"`)
  return (await (await fetch(`${MAILPIT}/api/v1/search?query=${query}`)).json()).messages
}

export async function emailHtml(id) {
  return (await (await fetch(`${MAILPIT}/api/v1/message/${id}`)).json()).HTML
}

// Waits for a new email to `email` in Mailpit and returns the 6-digit code it contains.
export async function waitForCode(email, alreadyReceived = 0) {
  let messages = []
  await expect.poll(async () => (messages = await emailsTo(email)).length, { timeout: 15_000 }).toBeGreaterThan(alreadyReceived)
  const html = await emailHtml(messages[0].ID)
  return { code: html.match(/letter-spacing:4px;'>(\d{6})</)[1], count: messages.length }
}

// Creates an account through the normal sign-up (code read in Mailpit) and returns its token.
export async function createAccount(email, password, prenom = 'Temp') {
  await api('POST', '/auth/register', null, { nom: 'Test', prenom, email, motdepasse: password })
  const { code } = await waitForCode(email)
  await api('POST', '/auth/register/verify', null, { email, otp: code })
  return login(email, password)
}

export async function signIn(page, email, password = DEMO_PASSWORD) {
  await page.goto('/auth/login')
  await page.getByPlaceholder('Email').fill(email)
  await page.getByPlaceholder('Mot de passe').fill(password)
  await page.getByRole('button', { name: 'CONNEXION' }).click()
  await page.waitForURL((url) => !url.pathname.startsWith('/auth/login'))
}

// Runs `action` and checks the status of the API call it triggers.
export async function expectCall(page, method, fragment, action, status = 200) {
  const [response] = await Promise.all([
    page.waitForResponse((r) => r.request().method() === method && r.url().includes(fragment)),
    action(),
  ])
  expect(response.status(), `${method} ${fragment}`).toBe(status)
  return response
}

// The post card containing `text`.
export const card = (page, text) => page.locator('div.shadow-lg').filter({ hasText: text }).first()

// The like button of a post card.
export const likeButton = (postCard) => postCard.locator('.pt-4.border-t > div > button').first()
