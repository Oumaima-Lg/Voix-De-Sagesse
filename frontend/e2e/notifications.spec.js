import { expect, test } from '@playwright/test'
import { ADMIN, ADMIN_PASSWORD, BASE_URL, DEMO, KARIM, VIEWPORT, api, login, signIn, userId } from './helpers.js'

test.describe.serial('Notifications', () => {
  let page
  let demo, karim, karimId, demoId, articleId, before, comment

  test.beforeAll(async ({ browser }) => {
    let admin
    ;[demo, karim, admin] = await Promise.all([login(DEMO), login(KARIM), login(ADMIN, ADMIN_PASSWORD)])
    ;[demoId, karimId] = [userId(demo), userId(karim)]
    const post = (await api('GET', '/articles/posts', karim)).find((p) => p.user.id === demoId)
    articleId = post.article.id
    before = { ...post.interaction }

    if (before.liked) await api('DELETE', `/articles/${articleId}/like`, karim)
    if (before.saved) await api('DELETE', `/users/me/saved/${articleId}`, karim)
    if (before.following) await api('DELETE', `/users/${demoId}/follow`, karim)
    await api('POST', '/notifications/read-all', demo)

    await api('POST', `/articles/${articleId}/like`, karim)
    ;({ comment } = await api('POST', `/comments/article/${articleId}`, karim, { content: 'Très belle pensée, merci de la partager.' }))
    await api('POST', `/users/me/saved/${articleId}`, karim)
    await api('POST', `/users/${demoId}/follow`, karim)
    const removed = await api('POST', '/articles/sagesse', demo, { content: 'Un article qui sera retiré par la modération pour le test.', category: 'philosophie', tags: ['test'] })
    const signal = await api('POST', '/signals', karim, { articleId: removed.id, reason: 'Spam', description: '' })
    await api('PUT', `/admin/signals/${signal.id}/process?status=APPROVED`, admin)

    page = await browser.newPage({ baseURL: BASE_URL, viewport: VIEWPORT })
    await signIn(page, DEMO)
  })

  test.afterAll(async () => {
    await page?.close()
    if (!before) return
    const quietly = (call) => call.catch(() => {})
    if (comment) await quietly(api('DELETE', `/comments/${comment.id}`, karim))
    await quietly(api('DELETE', `/articles/${articleId}/like`, karim))
    await quietly(api('DELETE', `/users/me/saved/${articleId}`, karim))
    await quietly(api('DELETE', `/users/${demoId}/follow`, karim))
    if (before.liked) await api('POST', `/articles/${articleId}/like`, karim)
    if (before.saved) await api('POST', `/users/me/saved/${articleId}`, karim)
    if (before.following) await api('POST', `/users/${demoId}/follow`, karim)
  })

  test('la cloche annonce 5 nouvelles notifications', async () => {
    await expect(page.getByRole('button', { name: 'Notifications : 5 nouvelles' })).toBeVisible()
  })

  test('la liste montre les 5 notifications, puis le compteur disparaît', async () => {
    const [response] = await Promise.all([
      page.waitForResponse((r) => r.url().endsWith('/notifications/read-all')),
      page.getByRole('button', { name: /^Notifications/ }).first().click(),
    ])
    expect(response.status()).toBe(204)
    for (const text of ['vous suit désormais', 'à ses favoris', 'a commenté', 'a aimé votre article', 'supprimé par la modération']) {
      await expect(page.getByText(text).first()).toBeVisible()
    }
    await expect(page.getByRole('button', { name: 'Notifications', exact: true }).first()).toBeVisible()
  })

  test("un clic sur l'abonnement ouvre le profil de Karim", async () => {
    await page.getByText('vous suit désormais').first().click()
    await page.waitForURL(`**/espace/auteur/${karimId}`)
  })

  test('la cloche est aussi sur le fil, les favoris et mes publications', async () => {
    for (const path of ['/espace/fil', '/espace/favoris', '/espace/mes-publications']) {
      await page.goto(path)
      await expect(page.getByRole('button', { name: /^Notifications/ })).toHaveCount(1)
    }
  })

  test("sur téléphone (375 px), l'en-tête et la liste tiennent dans l'écran", async ({ browser }) => {
    const phone = await browser.newPage({ baseURL: BASE_URL, viewport: { width: 375, height: 740 } })
    await signIn(phone, DEMO)
    await phone.getByRole('button', { name: /^Notifications/ }).first().click()
    const dropdown = phone.locator('.mantine-Popover-dropdown')
    await expect(dropdown).toBeVisible()
    expect(await phone.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(375)
    const box = await dropdown.boundingBox()
    expect(box.x).toBeGreaterThanOrEqual(0)
    expect(box.x + box.width).toBeLessThanOrEqual(375)
    await phone.close()
  })
})
