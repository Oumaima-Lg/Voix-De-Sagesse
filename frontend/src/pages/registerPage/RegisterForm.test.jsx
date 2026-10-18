import { beforeEach, describe, expect, it, vi } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Route, Routes } from 'react-router-dom'
import { renderWithProviders } from '../../test/render'
import { registerUser, verifyRegistration } from '../../api/authService'
import { errorNotification } from '../../utils/notifications'
import RegisterForm from './RegisterForm'

vi.mock('../../api/authService')
vi.mock('../../utils/notifications')

const renderForm = () =>
  renderWithProviders(
    <Routes>
      <Route path="/auth/register" element={<RegisterForm />} />
      <Route path="/auth/login" element={<p>Page de connexion</p>} />
    </Routes>,
    { route: '/auth/register' },
  )

const fillForm = async (user) => {
  await user.type(screen.getByPlaceholderText('Prénom'), 'Sara')
  await user.type(screen.getByPlaceholderText('Nom de famille'), 'Testeur')
  await user.type(screen.getByPlaceholderText('Email'), 'sara@exemple.fr')
  await user.type(screen.getByPlaceholderText('Mot de passe'), 'Sagesse2025!')
  await user.type(screen.getByPlaceholderText('Confirmez le mot de passe'), 'Sagesse2025!')
  await user.click(screen.getByRole('button', { name: "S'inscrire" }))
}

const codeBoxes = () => screen.getAllByLabelText('Code de vérification')

describe('RegisterForm', () => {
  beforeEach(() => {
    registerUser.mockResolvedValue({ message: 'Un code de vérification a été envoyé à votre adresse email.' })
  })

  it('does not call the API while the form is invalid', async () => {
    const user = userEvent.setup()
    renderForm()
    await user.click(screen.getByRole('button', { name: "S'inscrire" }))

    expect(screen.getByText('Le prénom est obligatoire.')).toBeInTheDocument()
    expect(screen.getByText('Le mot de passe est obligatoire.')).toBeInTheDocument()
    expect(registerUser).not.toHaveBeenCalled()
  })

  it('asks for the emailed code, then creates the account', async () => {
    verifyRegistration.mockResolvedValue({ id: 7, email: 'sara@exemple.fr' })
    const user = userEvent.setup()
    renderForm()
    await fillForm(user)

    expect(registerUser).toHaveBeenCalledWith(expect.objectContaining({ email: 'sara@exemple.fr', motdepasse: 'Sagesse2025!' }))
    expect(await screen.findByRole('heading', { name: 'Vérifiez votre email' })).toBeInTheDocument()
    expect(codeBoxes()).toHaveLength(6)

    await user.click(codeBoxes()[0])
    await user.keyboard('123456')

    expect(verifyRegistration).toHaveBeenCalledWith('sara@exemple.fr', '123456')
    expect(await screen.findByText('Page de connexion', {}, { timeout: 3000 })).toBeInTheDocument()
  })

  it('empties the boxes when the code is refused', async () => {
    verifyRegistration.mockRejectedValue({ response: { data: { errorMessage: 'Code incorrect.' } } })
    const user = userEvent.setup()
    renderForm()
    await fillForm(user)
    await screen.findByRole('heading', { name: 'Vérifiez votre email' })

    await user.click(codeBoxes()[0])
    await user.keyboard('000000')

    await waitFor(() => expect(errorNotification).toHaveBeenCalledWith('Code refusé', 'Code incorrect.'))
    expect(codeBoxes().map((box) => box.value)).toEqual(['', '', '', '', '', ''])
    expect(codeBoxes()[0]).toHaveFocus()
  })

  it('makes the user wait before asking for a new code, and keeps the form when going back', async () => {
    const user = userEvent.setup()
    renderForm()
    await fillForm(user)
    await screen.findByRole('heading', { name: 'Vérifiez votre email' })

    expect(screen.getByRole('button', { name: /Renvoyer le code \(\d+ s\)/ })).toBeDisabled()

    await user.click(screen.getByRole('button', { name: 'Modifier mes informations' }))
    expect(screen.getByPlaceholderText('Email')).toHaveValue('sara@exemple.fr')
  })
})
