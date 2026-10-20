import { expect, test } from '@playwright/test'
import {
  ADMIN, ADMIN_PASSWORD, API, BASE_URL, KARIM, VIEWPORT,
  api, card, expectCall, likeButton, login, signIn, waitForCode,
} from './helpers.js'

const STAMP = Date.now().toString()
const EMAIL = `smoke-${STAMP}@voixdesagesse.local`
const PASSWORD = 'Smoke123!'
const NEW_PASSWORD = 'Smoke456!'
const WISDOM = `Sagesse de test ${STAMP} : la patience porte ses fruits.`
const STORY = `Histoire de test ${STAMP}`
const PNG = Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==', 'base64')

test.describe.serial('Parcours principaux', () => {
  let page
  let karim
  let token
  let emailsReceived
  const jsErrors = []

  test.beforeAll(async ({ browser }) => {
    karim = await login(KARIM)
    page = await browser.newPage({ baseURL: BASE_URL, viewport: VIEWPORT })
    page.on('pageerror', (error) => jsErrors.push(error.message))
    page.on('dialog', (dialog) => dialog.accept())
  })

  test.afterAll(async () => {
    if (token) await api('DELETE', '/users/me', token)
    await page?.close()
  })

  test('inscription avec code, puis connexion', async () => {
    await page.goto('/auth/register')
    for (const [placeholder, value] of [['Prénom', 'Smoke'], ['Nom de famille', 'Test'], ['Email', EMAIL],
      ['Mot de passe', PASSWORD], ['Confirmez le mot de passe', PASSWORD]]) {
      await page.getByPlaceholder(placeholder, { exact: true }).fill(value)
    }
    await expectCall(page, 'POST', '/auth/register', () => page.getByRole('button', { name: "S'inscrire" }).click(), 202)
    const { code, count } = await waitForCode(EMAIL)
    emailsReceived = count
    await page.getByLabel('Code de vérification').first().click()
    await expectCall(page, 'POST', '/auth/register/verify', () => page.keyboard.type(code), 201)
    await page.waitForURL('**/auth/login')
    await signIn(page, EMAIL, PASSWORD)
    await expect(page).toHaveURL(/\/espace$/)
    token = await page.evaluate(() => localStorage.getItem('token'))
  })

  test('publier une sagesse', async () => {
    await page.goto('/espace/publier/formulaire?type=wisdom')
    await page.getByPlaceholder('Entrez votre citation ou proverbe...').fill(WISDOM)
    await page.locator('select').first().selectOption('philosophie')
    await page.getByPlaceholder('motivation, amour, travail...').fill('smoke, test')
    await expectCall(page, 'POST', '/articles/sagesse', () => page.locator('button[type="submit"]').click(), 201)
  })

  test('publier une histoire', async () => {
    await page.goto('/espace/publier/formulaire?type=story')
    await page.getByPlaceholder('Donnez un titre à votre histoire...').fill(STORY)
    await page.locator('select').first().selectOption('inspirante')
    await page.getByPlaceholder('motivation, amour, travail...').fill('smoke')
    await page.getByPlaceholder('Partagez votre expérience').fill(
      'Une histoire assez longue pour passer la validation du serveur, qui demande cinquante caractères.')
    await expectCall(page, 'POST', '/articles/histoire', () => page.locator('button[type="submit"]').click(), 201)
  })

  test("fil : j'aime puis je n'aime plus", async () => {
    await page.goto('/espace/fil')
    const like = likeButton(card(page, WISDOM))
    await expectCall(page, 'POST', '/like', () => like.click())
    await page.waitForTimeout(500)
    await expectCall(page, 'DELETE', '/like', () => like.click())
  })

  test('fil : commenter puis supprimer son commentaire', async () => {
    const mine = card(page, WISDOM)
    await mine.locator('button:has(svg.lucide-message-circle)').click()
    const box = mine.getByPlaceholder('Ajouter un commentaire...')
    await box.fill('Commentaire smoke')
    await expectCall(page, 'POST', '/comments/article/', () => box.press('Enter'))
    const own = mine.locator('div.flex.gap-3').filter({ hasText: 'Commentaire smoke' }).first()
    await expectCall(page, 'DELETE', '/comments/', () => own.locator('[title="Supprimer le commentaire"]').click())
  })

  test('fil : suivre puis ne plus suivre', async () => {
    const karimCards = page.locator('div.shadow-lg').filter({ hasText: 'Karim' })
    const toFollow = karimCards.filter({ has: page.getByRole('button', { name: 'Suivre' }) }).first()
    await expectCall(page, 'POST', '/follow', () => toFollow.getByRole('button', { name: 'Suivre' }).click())
    await expectCall(page, 'DELETE', '/follow', () => karimCards.getByRole('button', { name: 'Suivi' }).first().click())
  })

  test('recherche', async () => {
    await page.getByPlaceholder('Rechercher dans les articles...').fill(STAMP)
    await expectCall(page, 'GET', '/articles/search', () => page.getByRole('button', { name: 'Rechercher' }).click())
    await expect(page.getByText('2 résultats trouvés')).toBeVisible()
    await expect(card(page, WISDOM)).toBeVisible()
  })

  test('favoris : ajouter puis retirer', async () => {
    const { myPosts } = await api('GET', '/articles/my-posts', token)
    const wisdomId = myPosts.find((post) => post.article.content === WISDOM).article.id
    await api('POST', `/users/me/saved/${wisdomId}`, token)
    await page.goto('/espace/favoris')
    await expectCall(page, 'DELETE', '/users/me/saved/', () => card(page, WISDOM).locator('[title="Retirer des favoris"]').click())
    await expect(card(page, WISDOM)).toHaveCount(0)
  })

  test('photo de profil', async () => {
    const response = await page.request.post(`${API}/users/me/picture`, {
      headers: { Authorization: `Bearer ${token}` },
      multipart: { profilePicture: { name: 'a.png', mimeType: 'image/png', buffer: PNG } },
    })
    expect(response.status()).toBe(200)
    expect((await page.request.get(API + (await response.json()).profilePictureUrl)).status()).toBe(200)
  })

  test('mes publications : supprimer un article', async () => {
    await page.goto('/espace/mes-publications')
    await expectCall(page, 'DELETE', '/articles/', () => card(page, STORY).locator('[title="Supprimer l\'article"]').click())
    await expect(card(page, STORY)).toHaveCount(0)
  })

  test('erreur : une seule notification, avec le message du serveur', async () => {
    const temp = await api('POST', '/articles/sagesse', karim, { content: `Article éphémère ${STAMP}.`, category: 'philosophie', tags: ['test'] })
    await page.goto('/espace/fil')
    const target = card(page, `Article éphémère ${STAMP}.`)
    await expect(target).toBeVisible()
    await api('DELETE', `/articles/${temp.id}`, karim)
    await expectCall(page, 'POST', '/like', () => likeButton(target).click(), 404)
    await expect(page.locator('.mantine-Notification-root')).toHaveCount(1)
    await expect(page.locator('.mantine-Notification-root')).toContainText('Article introuvable.')
  })

  test('mot de passe oublié : code, nouveau mot de passe, connexion', async () => {
    await api('POST', '/auth/password/otp', null, { email: EMAIL })
    const { code } = await waitForCode(EMAIL, emailsReceived)
    const { resetToken } = await api('POST', '/auth/password/otp/verify', null, { email: EMAIL, otp: code })
    await api('POST', '/auth/password/reset', null, { email: EMAIL, resetToken, newPassword: NEW_PASSWORD })
    token = await login(EMAIL, NEW_PASSWORD)
  })

  test('documentation Swagger', async () => {
    await page.goto(`${API}/swagger-ui.html`)
    await expect(page.getByText('Voix De Sagesse API').first()).toBeVisible()
  })

  test('tableau de bord admin et signalements', async ({ browser }) => {
    const adminPage = await browser.newPage({ baseURL: BASE_URL, viewport: VIEWPORT })
    await signIn(adminPage, ADMIN, ADMIN_PASSWORD)
    await adminPage.waitForURL('**/admin/dashboard')
    await expect(adminPage.getByText('Utilisateurs Total')).toBeVisible({ timeout: 60_000 })
    await expect(adminPage.getByText('vs mois dernier')).toHaveCount(0)
    await expectCall(adminPage, 'GET', '/admin/signals', () => adminPage.getByRole('button', { name: /Signalements/ }).first().click())
    await adminPage.close()
  })

  test('aucune erreur JavaScript', () => {
    expect(jsErrors).toEqual([])
  })
})
