import { describe, expect, it } from 'vitest'
import { timeAgo } from './timeAgo'

const now = new Date('2026-10-18T12:00:00')
const before = (seconds) => new Date(now.getTime() - seconds * 1000)

describe('timeAgo', () => {
  it('reads like French in the notification list', () => {
    expect(timeAgo(before(20), now)).toBe("à l'instant")
    expect(timeAgo(before(5 * 60), now)).toBe('il y a 5 minutes')
    expect(timeAgo(before(3 * 3600), now)).toBe('il y a 3 heures')
    expect(timeAgo(before(26 * 3600), now)).toBe('hier')
    expect(timeAgo(before(30 * 86400), now)).toBe('18/09/2026')
  })
})
