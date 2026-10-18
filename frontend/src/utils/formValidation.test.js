import { describe, expect, it } from 'vitest'
import { loginValidation, signupValidation } from './formValidation'

describe('signupValidation', () => {
  it('requires the names and the email', () => {
    expect(signupValidation('nom', '  ')).toBe('Le nom est obligatoire.')
    expect(signupValidation('prenom', '')).toBe('Le prénom est obligatoire.')
    expect(signupValidation('email', '')).toBe("L'email est obligatoire.")
  })

  it('checks the email format', () => {
    expect(signupValidation('email', 'sara@')).toBe("L'email n'est pas valide.")
    expect(signupValidation('email', 'sara@exemple.fr')).toBe('')
  })

  it('applies the same password rule as the backend', () => {
    expect(signupValidation('motdepasse', '')).toBe('Le mot de passe est obligatoire.')
    for (const weak of ['motdepasse1!', 'MOTDEPASSE1!', 'MotDePasse!', 'MotDePasse1', 'Ab1!', 'MotDePasseTropLong1!']) {
      expect(signupValidation('motdepasse', weak)).not.toBe('')
    }
    expect(signupValidation('motdepasse', 'Sagesse2025!')).toBe('')
  })
})

describe('loginValidation', () => {
  it('requires the email and the password', () => {
    expect(loginValidation('email', '')).toBe("L'email est obligatoire.")
    expect(loginValidation('password', '')).toBe('Le mot de passe est obligatoire.')
  })

  it('does not apply the sign-up password rule at login', () => {
    expect(loginValidation('password', 'ancien')).toBe('')
  })
})
