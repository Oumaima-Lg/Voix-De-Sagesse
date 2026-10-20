import { expect, test } from '@playwright/test'
import {
  ADMIN, ADMIN_PASSWORD, BASE_URL, DEMO, KARIM,
  api, createAccount, emailHtml, emailsTo, login, signIn,
} from './helpers.js'

const STAMP = Date.now().toString()
const CONTENT = `Contenu douteux à modérer ${STAMP}, signalé par deux lecteurs du site.`
const REPORTER = `rapporteur-${STAMP}@voixdesagesse.local`

test.describe.serial('Modération des signalements', () => {
  let page
  let demo, karim, admin, reporter
  let reported, kept, karimEmails, pendingBefore
  const dialogs = []

  const reportsFor = async (articleId) =>
    (await api('GET', '/admin/signals', admin)).filter((s) => s.articleId === articleId).map((s) => s.status)

  const pendingBadge = async () => {
    const last = (await page.getByRole('button', { name: /Signalements/ }).first().innerText()).trim().split(/\s+/).pop()
    return /^\d+$/.test(last) ? Number(last) : 0
  }

  const openReports = async () => {
    await expect(page.getByText('Utilisateurs Total')).toBeVisible({ timeout: 60_000 })
    pendingBefore = await pendingBadge()
    await page.getByRole('button', { name: /Signalements/ }).first().click()
  }

  test.beforeAll(async ({ browser }) => {
    ;[demo, karim, admin] = await Promise.all([login(DEMO), login(KARIM), login(ADMIN, ADMIN_PASSWORD)])
    reporter = await createAccount(REPORTER, 'Test1234!', 'Rapporteur')
    reported = await api('POST', '/articles/sagesse', karim, { content: CONTENT, category: 'philosophie', tags: ['test'] })
    await api('POST', '/signals', demo, { articleId: reported.id, reason: 'Spam', description: 'Publicité déguisée' })
    await api('POST', '/signals', reporter, { articleId: reported.id, reason: 'Contenu inapproprié', description: '' })
    karimEmails = (await emailsTo(KARIM)).length

    page = await browser.newPage({ baseURL: BASE_URL, viewport: { width: 1366, height: 900 } })
    page.on('dialog', (dialog) => {
      dialogs.push(dialog.message())
      dialog.accept()
    })
    await signIn(page, ADMIN, ADMIN_PASSWORD)
    await page.waitForURL('**/admin/dashboard')
    await openReports()
  })

  test.afterAll(async () => {
    if (kept) await api('DELETE', `/articles/${kept.id}`, karim)
    if (reporter) await api('DELETE', '/users/me', reporter)
    await page?.close()
  })

  test('la liste montre le début du texte (sagesse sans titre)', async () => {
    await expect(page.locator('tbody tr').filter({ hasText: `Contenu douteux à modérer ${STAMP}` })).toHaveCount(2)
  })

  test("supprimer l'article, avec confirmation", async () => {
    await page.locator('tbody tr').filter({ hasText: 'Rapporteur' }).first().getByRole('button', { name: 'Voir le détail' }).click()
    await expect(page.getByText(CONTENT).first()).toBeVisible()
    const [response] = await Promise.all([
      page.waitForResponse((r) => r.url().includes('/process')),
      page.locator('div.fixed').getByRole('button', { name: "Supprimer l'article" }).click(),
    ])
    expect(response.status()).toBe(200)
    expect(dialogs.at(-1)).toContain('Supprimer cet article ?')
  })

  test('article retiré, les 2 signalements traités, badge à jour', async () => {
    const feed = await api('GET', '/articles/posts', karim)
    expect(feed.some((post) => post.article.id === reported.id)).toBe(false)
    expect(await reportsFor(reported.id)).toEqual(['APPROVED', 'APPROVED'])
    await expect.poll(pendingBadge).toBe(pendingBefore - 2)
  })

  test("email à l'auteur, avec le début du texte", async () => {
    const received = await emailsTo(KARIM)
    expect(received).toHaveLength(karimEmails + 1)
    const named = (await emailHtml(received[0].ID)).match(/display:inline-block;border-radius:5px;'>([^<]*)</)[1]
    expect(named).toContain(STAMP)
  })

  test("garder l'article, sans confirmation", async () => {
    kept = await api('POST', '/articles/sagesse', karim, { content: `Article à garder ${STAMP}, rien de grave.`, category: 'philosophie', tags: ['test'] })
    await api('POST', '/signals', demo, { articleId: kept.id, reason: 'Spam', description: '' })
    await page.reload()
    await openReports()
    const dialogsBefore = dialogs.length
    const row = page.locator('tbody tr').filter({ hasText: `Article à garder ${STAMP}` })
    const [response] = await Promise.all([
      page.waitForResponse((r) => r.url().includes('/process')),
      row.getByRole('button', { name: "Garder l'article" }).click(),
    ])
    expect(response.status()).toBe(200)
    expect(dialogs).toHaveLength(dialogsBefore)
    expect((await api('GET', '/articles/posts', karim)).some((post) => post.article.id === kept.id)).toBe(true)
    expect(await reportsFor(kept.id)).toEqual(['REJECTED'])
  })

  test("l'auteur supprime son article : le signalement est clos", async () => {
    const article = await api('POST', '/articles/sagesse', karim, { content: `Article retiré par son auteur ${STAMP}.`, category: 'philosophie', tags: ['test'] })
    await api('POST', '/signals', demo, { articleId: article.id, reason: 'Spam', description: '' })
    await api('DELETE', `/articles/${article.id}`, karim)
    expect(await reportsFor(article.id)).toEqual(['CLOSED'])
  })
})
