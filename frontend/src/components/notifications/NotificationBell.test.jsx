import { beforeEach, describe, expect, it, vi } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Route, Routes } from 'react-router-dom'
import { renderWithProviders } from '../../test/render'
import { getNotifications, getUnreadCount, markAllNotificationsRead } from '../../api/notificationService'
import NotificationBell from './NotificationBell'

vi.mock('../../api/notificationService')
vi.mock('../../utils/notifications')

const karim = { id: 5, nom: 'Benali', prenom: 'Karim', profilePicture: null }
const now = new Date().toISOString()
const list = [
  { id: 4, type: 'FOLLOW', read: false, createdAt: now, actor: karim },
  { id: 3, type: 'COMMENT', read: false, createdAt: now, actor: karim, articleId: 1, articleExcerpt: 'La patience…', commentExcerpt: 'Très juste' },
  { id: 2, type: 'LIKE', read: false, createdAt: now, actor: karim, articleId: 1, articleExcerpt: 'La patience…' },
  { id: 1, type: 'ARTICLE_REMOVED', read: true, createdAt: '2026-01-02T10:00:00', actor: null, articleId: 9, articleExcerpt: 'Un vieil article' },
]

const renderBell = () =>
  renderWithProviders(
    <Routes>
      <Route path="/espace" element={<NotificationBell />} />
      <Route path="/espace/auteur/:id" element={<p>Profil de l'auteur</p>} />
    </Routes>,
    { route: '/espace' },
  )

describe('NotificationBell', () => {
  beforeEach(() => {
    getUnreadCount.mockResolvedValue(3)
    getNotifications.mockResolvedValue(list)
    markAllNotificationsRead.mockResolvedValue({})
  })

  it('shows the number of new notifications', async () => {
    renderBell()
    expect(await screen.findByRole('button', { name: 'Notifications : 3 nouvelles' })).toBeInTheDocument()
    expect(screen.getByText('3')).toBeInTheDocument()
  })

  it('opens the list, marks everything as read and clears the badge', async () => {
    const user = userEvent.setup()
    renderBell()
    await user.click(await screen.findByRole('button', { name: 'Notifications : 3 nouvelles' }))

    expect(await screen.findByText(/vous suit désormais/)).toBeInTheDocument()
    expect(screen.getByText(/a aimé votre article « La patience… »/)).toBeInTheDocument()
    expect(screen.getByText('Très juste')).toBeInTheDocument()
    expect(screen.getByText(/supprimé par la modération/)).toBeInTheDocument()
    expect(screen.getAllByText("à l'instant")).toHaveLength(3)
    await waitFor(() => expect(markAllNotificationsRead).toHaveBeenCalledTimes(1))
    expect(screen.getByRole('button', { name: 'Notifications' })).toBeInTheDocument()
  })

  it("leads to the follower's profile", async () => {
    const user = userEvent.setup()
    renderBell()
    await user.click(await screen.findByRole('button', { name: /Notifications/ }))
    await user.click(await screen.findByText(/vous suit désormais/))

    expect(await screen.findByText("Profil de l'auteur")).toBeInTheDocument()
  })

  it('says when there is nothing new', async () => {
    getUnreadCount.mockResolvedValue(0)
    getNotifications.mockResolvedValue([])
    const user = userEvent.setup()
    renderBell()
    await user.click(await screen.findByRole('button', { name: 'Notifications' }))

    expect(await screen.findByText('Aucune notification pour le moment.')).toBeInTheDocument()
    expect(markAllNotificationsRead).not.toHaveBeenCalled()
  })
})
