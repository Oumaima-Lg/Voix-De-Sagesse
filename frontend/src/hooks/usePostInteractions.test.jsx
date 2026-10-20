import { beforeEach, describe, expect, it, vi } from 'vitest'
import { act, renderHook } from '@testing-library/react'
import { useState } from 'react'
import { Provider } from 'react-redux'
import { makeStore } from '../test/render'
import { likeArticle, unlikeArticle } from '../api/articleService'
import { followUser, unsaveArticle } from '../api/userService'
import { addComment, getCommentsByArticle } from '../api/commentService'
import { errorNotification, successNotification } from '../utils/notifications'
import { usePostInteractions } from './usePostInteractions'

vi.mock('../api/articleService')
vi.mock('../api/userService')
vi.mock('../api/commentService')
vi.mock('../utils/notifications')

const post = (id, authorId, interaction = {}) => ({
  article: { id, likes: 3, comments: 0 },
  user: { id: authorId },
  interaction: { liked: false, saved: false, following: false, ...interaction },
})

const setup = (initialPosts, options) =>
  renderHook(
    () => {
      const [posts, setPosts] = useState(initialPosts)
      return { posts, ...usePostInteractions(posts, setPosts, options) }
    },
    { wrapper: ({ children }) => <Provider store={makeStore({ id: 1 })}>{children}</Provider> },
  )

describe('usePostInteractions', () => {
  beforeEach(() => {
    likeArticle.mockResolvedValue('ok')
    unlikeArticle.mockResolvedValue('ok')
  })

  it('likes a post', async () => {
    const { result } = setup([post(10, 2)])

    await act(() => result.current.handleLike(10))
    expect(likeArticle).toHaveBeenCalledWith(10)
    expect(result.current.posts[0]).toMatchObject({ interaction: { liked: true }, article: { likes: 4 } })
  })

  it('unlikes a post already liked', async () => {
    const { result } = setup([post(10, 2, { liked: true })])

    await act(() => result.current.handleLike(10))
    expect(unlikeArticle).toHaveBeenCalledWith(10)
    expect(result.current.posts[0]).toMatchObject({ interaction: { liked: false }, article: { likes: 2 } })
  })

  it('keeps the post unchanged and reports the error when the like fails', async () => {
    likeArticle.mockRejectedValue({ response: { data: { errorMessage: 'Article introuvable' } } })
    const { result } = setup([post(10, 2)])

    await act(() => result.current.handleLike(10))
    expect(result.current.posts[0]).toMatchObject({ interaction: { liked: false }, article: { likes: 3 } })
    expect(errorNotification).toHaveBeenCalledWith('Erreur', 'Article introuvable')
  })

  it('removes a post from the saved list when it is unsaved', async () => {
    unsaveArticle.mockResolvedValue({})
    const { result } = setup([post(10, 2, { saved: true }), post(11, 2, { saved: true })], { removeWhenUnsaved: true })

    await act(() => result.current.handleSave(10, true))
    expect(result.current.posts.map((p) => p.article.id)).toEqual([11])
    expect(successNotification).toHaveBeenCalledWith('Succès', 'Article retiré des favoris')
  })

  it('marks every post of an author as followed', async () => {
    followUser.mockResolvedValue({})
    const { result } = setup([post(10, 2), post(11, 3), post(12, 2)])

    await act(() => result.current.handleFollow(2, false))
    expect(result.current.posts.map((p) => p.interaction.following)).toEqual([true, false, true])
  })

  it('adds a comment and updates the counter', async () => {
    addComment.mockResolvedValue({})
    getCommentsByArticle.mockResolvedValue({ comments: [{ id: 1, content: 'Très juste' }] })
    const { result } = setup([post(10, 2)])

    act(() => result.current.handleCommentChange(10, '  Très juste  '))
    await act(() => result.current.handleCommentSubmit(10))

    expect(addComment).toHaveBeenCalledWith(10, 'Très juste')
    expect(result.current.posts[0].article.comments).toBe(1)
    expect(result.current.postComments[10]).toHaveLength(1)
    expect(result.current.commentInputs[10]).toBe('')
  })
})
