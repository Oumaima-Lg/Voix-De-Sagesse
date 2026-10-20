import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { AxiosError } from 'axios'
import axiosInstance from './axiosInstance'
import { errorNotification } from '../utils/notifications'

vi.mock('../utils/notifications')

const failWith = (status) => ({
  adapter: async (config) => {
    throw new AxiosError('failed', 'ERR_BAD_RESPONSE', config, null,
      { status, data: { errorMessage: 'Article introuvable.' }, headers: {}, config })
  },
})

describe('axiosInstance errors', () => {
  const realLocation = window.location

  beforeEach(() => {
    Object.defineProperty(window, 'location', { configurable: true, value: { href: '', pathname: '/espace/fil' } })
  })
  afterEach(() => {
    Object.defineProperty(window, 'location', { configurable: true, value: realLocation })
  })

  it('leaves ordinary errors to the page, so they are shown only once', async () => {
    for (const status of [400, 404, 409, 500]) {
      await expect(axiosInstance.get('/articles/1', failWith(status))).rejects.toMatchObject({ response: { status } })
    }
    expect(errorNotification).not.toHaveBeenCalled()
    expect(window.location.href).toBe('')
  })

  it('ends an expired session and sends the user to the login page', async () => {
    localStorage.setItem('token', 'expired')
    localStorage.setItem('user', '{"id":1}')

    await expect(axiosInstance.get('/articles/posts', failWith(401))).rejects.toBeDefined()

    expect(localStorage.getItem('token')).toBeNull()
    expect(errorNotification).toHaveBeenCalledWith('Session expirée', 'Session expirée, veuillez vous reconnecter')
    expect(window.location.href).toMatch(/^\/auth\/login\?message=/)
  })

  it('sends non-admins away from the admin area', async () => {
    await expect(axiosInstance.get('/admin/users', failWith(403))).rejects.toBeDefined()
    expect(window.location.href).toBe('/espace')
  })
})
