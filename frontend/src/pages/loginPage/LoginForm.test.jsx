import { describe, expect, it, vi } from 'vitest'
import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Route, Routes } from 'react-router-dom'
import { renderWithProviders } from '../../test/render'
import { loginUser } from '../../api/authService'
import { errorNotification } from '../../utils/notifications'
import LoginForm from './LoginForm'

vi.mock('../../api/authService')
vi.mock('../../utils/notifications')

const fakeJwt = (claims) => `e30.${btoa(JSON.stringify(claims))}.signature`

const renderLogin = () =>
  renderWithProviders(
    <Routes>
      <Route path="/auth/login" element={<LoginForm />} />
      <Route path="/espace" element={<p>Mon espace</p>} />
    </Routes>,
    { route: '/auth/login' },
  )

describe('LoginForm', () => {
  it('requires the password as well as the email', async () => {
    const user = userEvent.setup()
    renderLogin()
    await user.type(screen.getByPlaceholderText('Email'), 'sara@exemple.fr')
    await user.click(screen.getByRole('button', { name: 'CONNEXION' }))

    expect(screen.getByText('Le mot de passe est obligatoire.')).toBeInTheDocument()
    expect(loginUser).not.toHaveBeenCalled()
  })

  it('shows a clear message for wrong credentials', async () => {
    loginUser.mockRejectedValue({ response: { status: 401, data: {} } })
    const user = userEvent.setup()
    renderLogin()
    await user.type(screen.getByPlaceholderText('Email'), 'sara@exemple.fr')
    await user.type(screen.getByPlaceholderText('Mot de passe'), 'mauvais')
    await user.click(screen.getByRole('button', { name: 'CONNEXION' }))

    expect(errorNotification).toHaveBeenCalledWith('Échec de la connexion', 'Email ou mot de passe incorrect.')
    expect(localStorage.getItem('token')).toBeNull()
  })

  it('keeps the session and opens the logged-in area', async () => {
    const jwt = fakeJwt({ sub: 'sara@exemple.fr', id: 2, accountType: 'USER' })
    loginUser.mockResolvedValue({ jwt })
    const user = userEvent.setup()
    const { store } = renderLogin()
    await user.type(screen.getByPlaceholderText('Email'), 'sara@exemple.fr')
    await user.type(screen.getByPlaceholderText('Mot de passe'), 'Sagesse2025!')
    await user.click(screen.getByRole('button', { name: 'CONNEXION' }))

    expect(await screen.findByText('Mon espace', {}, { timeout: 3000 })).toBeInTheDocument()
    expect(localStorage.getItem('token')).toBe(jwt)
    expect(store.getState().user).toMatchObject({ id: 2, email: 'sara@exemple.fr' })
  })
})
