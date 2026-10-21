import { useEffect } from 'react'
import { notifications } from '@mantine/notifications'
import { API_URL } from '../config'
import { errorNotification } from '../utils/notifications'
import { SERVER_UNAVAILABLE } from '../api/apiError'

export const SLOW_AFTER_MS = 3000
export const RETRY_EVERY_MS = 5000
export const MAX_ATTEMPTS = 36
const NOTIFICATION_ID = 'server-wake-up'

const isServerUp = () => fetch(`${API_URL}/actuator/health`).then((response) => response.ok, () => false)

export const useServerWakeUp = () => {
  useEffect(() => {
    let cancelled = false
    let retry

    const slow = setTimeout(() => {
      notifications.show({
        id: NOTIFICATION_ID,
        loading: true,
        title: 'Le serveur démarre…',
        message: "Il se met en veille quand personne ne l'utilise. Cela peut prendre jusqu'à deux minutes.",
        autoClose: false,
        withCloseButton: false,
      })
    }, SLOW_AFTER_MS)

    const check = async (attempt) => {
      const up = await isServerUp()
      if (cancelled) return
      if (up) {
        clearTimeout(slow)
        notifications.hide(NOTIFICATION_ID)
      } else if (attempt < MAX_ATTEMPTS) {
        retry = setTimeout(() => check(attempt + 1), RETRY_EVERY_MS)
      } else {
        clearTimeout(slow)
        notifications.hide(NOTIFICATION_ID)
        errorNotification('Serveur indisponible', SERVER_UNAVAILABLE)
      }
    }
    check(1)

    return () => {
      cancelled = true
      clearTimeout(slow)
      clearTimeout(retry)
      notifications.hide(NOTIFICATION_ID)
    }
  }, [])
}
