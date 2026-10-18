import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { renderWithProviders } from '../../test/render'
import { getAllSignals, processSignal } from '../../api/adminService'
import { successNotification } from '../../utils/notifications'
import Signals from './Signals'

vi.mock('../../api/adminService')
vi.mock('../../utils/notifications')

const WISDOM = 'La patience est un arbre dont la racine est amère, mais dont les fruits sont doux.'
const pending = {
  id: 3, status: 'PENDING', reason: 'Spam', articleId: 20, articleTitle: null, articleContent: WISDOM, articleType: 'SAGESSE',
  reporterName: 'Sara Testeur', reporterEmail: 'sara@exemple.fr', reportedUserName: 'Karim Benali', createdAt: '2026-10-18T10:00:00',
}

describe('Signals (admin)', () => {
  beforeEach(() => {
    getAllSignals.mockResolvedValue([pending])
    processSignal.mockResolvedValue({})
  })
  afterEach(() => vi.restoreAllMocks())

  it('names an article without a title by the start of its text', async () => {
    renderWithProviders(<Signals />)
    expect(await screen.findByText(/^La patience est un arbre dont la racine est amère, mais dont/)).toBeInTheDocument()
  })

  it('asks before deleting an article, and does nothing if the admin cancels', async () => {
    const confirm = vi.spyOn(window, 'confirm').mockReturnValue(false)
    const user = userEvent.setup()
    renderWithProviders(<Signals />)
    await user.click(await screen.findByRole('button', { name: "Supprimer l'article" }))

    expect(confirm).toHaveBeenCalledWith(expect.stringContaining('Supprimer cet article ?'))
    expect(processSignal).not.toHaveBeenCalled()
  })

  it('deletes after confirmation, then reloads the list and the dashboard counters', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true)
    const onChange = vi.fn()
    const user = userEvent.setup()
    renderWithProviders(<Signals onChange={onChange} />)
    await user.click(await screen.findByRole('button', { name: "Supprimer l'article" }))

    expect(processSignal).toHaveBeenCalledWith(3, 'APPROVED')
    await waitFor(() => expect(onChange).toHaveBeenCalled())
    expect(getAllSignals).toHaveBeenCalledTimes(2)
    expect(successNotification).toHaveBeenCalledWith('Article supprimé', expect.any(String))
  })

  it('keeps an article without asking', async () => {
    const confirm = vi.spyOn(window, 'confirm')
    const user = userEvent.setup()
    renderWithProviders(<Signals />)
    await user.click(await screen.findByRole('button', { name: "Garder l'article" }))

    expect(confirm).not.toHaveBeenCalled()
    expect(processSignal).toHaveBeenCalledWith(3, 'REJECTED')
  })
})
