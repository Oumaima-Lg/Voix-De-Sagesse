import { describe, expect, it } from 'vitest'
import { screen } from '@testing-library/react'
import { Route, Routes } from 'react-router-dom'
import { renderWithProviders } from '../test/render'
import { ProtectedRoute, PublicRoute } from './AuthGuard'

const app = (
  <Routes>
    <Route path="/espace" element={<ProtectedRoute><p>Espace</p></ProtectedRoute>} />
    <Route path="/auth/login" element={<PublicRoute><p>Connexion</p></PublicRoute>} />
    <Route path="/admin/dashboard" element={<p>Admin</p>} />
  </Routes>
)

const logIn = (accountType) => {
  localStorage.setItem('token', 'jwt')
  return { id: 1, accountType }
}

describe('route guards', () => {
  it('sends visitors to the login page', () => {
    renderWithProviders(app, { route: '/espace' })
    expect(screen.getByText('Connexion')).toBeInTheDocument()
  })

  it('opens the logged-in area to users', () => {
    renderWithProviders(app, { route: '/espace', user: logIn('USER') })
    expect(screen.getByText('Espace')).toBeInTheDocument()
  })

  it('sends a logged-in user away from the login page', () => {
    renderWithProviders(app, { route: '/auth/login', user: logIn('USER') })
    expect(screen.getByText('Espace')).toBeInTheDocument()
  })

  it('sends admins to their dashboard', () => {
    renderWithProviders(app, { route: '/espace', user: logIn('ADMIN') })
    expect(screen.getByText('Admin')).toBeInTheDocument()
  })
})
