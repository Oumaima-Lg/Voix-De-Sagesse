import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { act, renderHook } from '@testing-library/react'
import { notifications } from '@mantine/notifications'
import { API_URL } from '../config'
import { errorNotification } from '../utils/notifications'
import { MAX_ATTEMPTS, RETRY_EVERY_MS, SLOW_AFTER_MS, useServerWakeUp } from './useServerWakeUp'

vi.mock('@mantine/notifications', () => ({ notifications: { show: vi.fn(), hide: vi.fn() } }))
vi.mock('../utils/notifications')

const waking = expect.objectContaining({ title: 'Le serveur démarre…', loading: true })

describe('useServerWakeUp', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.useRealTimers()
    vi.unstubAllGlobals()
  })

  it('stays silent when the server answers quickly', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true }))
    renderHook(() => useServerWakeUp())
    await act(() => vi.advanceTimersByTimeAsync(SLOW_AFTER_MS * 2))

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/actuator/health`)
    expect(notifications.show).not.toHaveBeenCalled()
  })

  it('tells the visitor the server is starting, then hides the message', async () => {
    let answer
    vi.stubGlobal('fetch', vi.fn(() => new Promise((resolve) => { answer = resolve })))
    renderHook(() => useServerWakeUp())
    await act(() => vi.advanceTimersByTimeAsync(SLOW_AFTER_MS))
    expect(notifications.show).toHaveBeenCalledWith(waking)

    await act(async () => answer({ ok: true }))
    expect(notifications.hide).toHaveBeenCalledWith('server-wake-up')
    expect(errorNotification).not.toHaveBeenCalled()
  })

  it('keeps trying while the server is not ready', async () => {
    vi.stubGlobal('fetch', vi.fn()
      .mockRejectedValueOnce(new TypeError('Failed to fetch'))
      .mockResolvedValueOnce({ ok: false })
      .mockResolvedValue({ ok: true }))
    renderHook(() => useServerWakeUp())
    await act(() => vi.advanceTimersByTimeAsync(RETRY_EVERY_MS * 3))

    expect(fetch).toHaveBeenCalledTimes(3)
    expect(notifications.hide).toHaveBeenCalledWith('server-wake-up')
    expect(errorNotification).not.toHaveBeenCalled()
  })

  it('gives up with an error when the server never answers', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))
    renderHook(() => useServerWakeUp())
    await act(() => vi.advanceTimersByTimeAsync(RETRY_EVERY_MS * MAX_ATTEMPTS))

    expect(fetch).toHaveBeenCalledTimes(MAX_ATTEMPTS)
    expect(errorNotification).toHaveBeenCalledWith('Serveur indisponible', expect.any(String))
  })
})
