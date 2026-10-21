import { describe, expect, it } from 'vitest'
import { getApiErrorMessage, SERVER_UNAVAILABLE } from './apiError'

describe('getApiErrorMessage', () => {
  it('shows the message sent by the API', () => {
    expect(getApiErrorMessage({ response: { data: { errorMessage: 'Code incorrect.' } } })).toBe('Code incorrect.')
    expect(getApiErrorMessage({ response: { data: { message: 'Article introuvable' } } })).toBe('Article introuvable')
  })

  it('keeps the API message for its own 503 errors', () => {
    const mailDown = { response: { status: 503, data: { errorMessage: 'Les emails ne peuvent pas être envoyés pour le moment.' } } }
    expect(getApiErrorMessage(mailDown)).toBe('Les emails ne peuvent pas être envoyés pour le moment.')
  })

  it('falls back when the API sent no message', () => {
    expect(getApiErrorMessage({ response: { status: 500, data: '' } }, 'Échec')).toBe('Échec')
  })

  it('explains when the server cannot be reached', () => {
    expect(getApiErrorMessage(new Error('Network Error'))).toBe(SERVER_UNAVAILABLE)
  })

  it('explains when the host answers instead of the API', () => {
    expect(getApiErrorMessage({ response: { status: 503, data: '<html>Service Suspended</html>' } })).toBe(SERVER_UNAVAILABLE)
    expect(getApiErrorMessage({ response: { status: 502, data: '' } })).toBe(SERVER_UNAVAILABLE)
  })
})
