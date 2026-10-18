import { describe, expect, it } from 'vitest'
import { getApiErrorMessage } from './apiError'

describe('getApiErrorMessage', () => {
  it('shows the message sent by the API', () => {
    expect(getApiErrorMessage({ response: { data: { errorMessage: 'Code incorrect.' } } })).toBe('Code incorrect.')
    expect(getApiErrorMessage({ response: { data: { message: 'Article introuvable' } } })).toBe('Article introuvable')
  })

  it('falls back when the API sent no message', () => {
    expect(getApiErrorMessage({ response: { data: '' } }, 'Échec')).toBe('Échec')
  })

  it('explains when the server cannot be reached', () => {
    expect(getApiErrorMessage(new Error('Network Error'))).toBe('Impossible de contacter le serveur. Réessayez plus tard.')
  })
})
