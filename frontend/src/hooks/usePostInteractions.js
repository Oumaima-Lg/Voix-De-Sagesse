import { useState } from "react"
import { useSelector } from "react-redux"
import { likeArticle, unlikeArticle } from "../api/articleService"
import { followUser, unfollowUser, saveArticle, unsaveArticle } from "../api/userService"
import { addComment, getCommentsByArticle, deleteComment } from "../api/commentService"
import { getApiErrorMessage } from "../api/apiError"
import { errorNotification, successNotification } from "../utils/notifications"

// State and actions shared by the pages that list posts (feed, saved articles, my posts).
// Errors are always reported; a success message is only shown when a post leaves the list.
// `removeWhenUnsaved`: on the saved-articles page, unsaving a post removes it from the list.
export const usePostInteractions = (posts, setPosts, { removeWhenUnsaved = false } = {}) => {
  const user = useSelector((state) => state.user)
  const [commentInputs, setCommentInputs] = useState({})
  const [postComments, setPostComments] = useState({})
  const [showComments, setShowComments] = useState({})
  const [commentLoading, setCommentLoading] = useState({})
  const [deleteCommentLoading, setDeleteCommentLoading] = useState({})
  const [likeLoading, setLikeLoading] = useState({})
  const [saveLoading, setSaveLoading] = useState({})
  const [followLoading, setFollowLoading] = useState({})

  const updateArticle = (articleId, update) =>
    setPosts((prev) => prev.map((poste) => (poste.article.id === articleId ? update(poste) : poste)))

  const loadComments = async (articleId) => {
    try {
      const response = await getCommentsByArticle(articleId)
      setPostComments((prev) => ({ ...prev, [articleId]: response.comments || [] }))
    } catch (err) {
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible de charger les commentaires"))
    }
  }

  const toggleComments = async (articleId) => {
    const isCurrentlyShown = showComments[articleId]
    setShowComments((prev) => ({ ...prev, [articleId]: !isCurrentlyShown }))
    if (!isCurrentlyShown && !postComments[articleId]) {
      await loadComments(articleId)
    }
  }

  const handleCommentChange = (articleId, value) => {
    setCommentInputs((prev) => ({ ...prev, [articleId]: value }))
  }

  const handleCommentSubmit = async (articleId) => {
    const content = commentInputs[articleId]
    if (!content?.trim()) return

    setCommentLoading((prev) => ({ ...prev, [articleId]: true }))
    try {
      await addComment(articleId, content.trim())
      await loadComments(articleId)
      updateArticle(articleId, (poste) => ({ ...poste, article: { ...poste.article, comments: poste.article.comments + 1 } }))
      setCommentInputs((prev) => ({ ...prev, [articleId]: "" }))
      setShowComments((prev) => ({ ...prev, [articleId]: true }))
    } catch (err) {
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible d'ajouter le commentaire"))
    } finally {
      setCommentLoading((prev) => ({ ...prev, [articleId]: false }))
    }
  }

  const handleDeleteComment = async (commentId, articleId) => {
    if (!window.confirm("Êtes-vous sûr de vouloir supprimer ce commentaire ?")) return

    setDeleteCommentLoading((prev) => ({ ...prev, [commentId]: true }))
    try {
      await deleteComment(commentId)
      setPostComments((prev) => ({
        ...prev,
        [articleId]: prev[articleId]?.filter((comment) => comment.id !== commentId) || [],
      }))
      updateArticle(articleId, (poste) => ({
        ...poste,
        article: { ...poste.article, comments: Math.max(0, poste.article.comments - 1) },
      }))
    } catch (err) {
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible de supprimer le commentaire"))
    } finally {
      setDeleteCommentLoading((prev) => ({ ...prev, [commentId]: false }))
    }
  }

  const handleLike = async (articleId) => {
    if (likeLoading[articleId]) return
    const poste = posts.find((p) => p.article.id === articleId)
    if (!poste) return

    setLikeLoading((prev) => ({ ...prev, [articleId]: true }))
    const isCurrentlyLiked = poste.interaction.liked
    try {
      if (isCurrentlyLiked) {
        await unlikeArticle(articleId)
      } else {
        await likeArticle(articleId)
      }
      updateArticle(articleId, (p) => ({
        ...p,
        interaction: { ...p.interaction, liked: !isCurrentlyLiked },
        article: { ...p.article, likes: isCurrentlyLiked ? Math.max(0, p.article.likes - 1) : p.article.likes + 1 },
      }))
    } catch (err) {
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible de mettre à jour le like"))
    } finally {
      setTimeout(() => setLikeLoading((prev) => ({ ...prev, [articleId]: false })), 300)
    }
  }

  const handleSave = async (articleId, isCurrentlySaved) => {
    setSaveLoading((prev) => ({ ...prev, [articleId]: true }))
    try {
      if (isCurrentlySaved) {
        await unsaveArticle(articleId)
      } else {
        await saveArticle(articleId)
      }
      if (isCurrentlySaved && removeWhenUnsaved) {
        setPosts((prev) => prev.filter((poste) => poste.article.id !== articleId))
        successNotification("Succès", "Article retiré des favoris")
      } else {
        updateArticle(articleId, (poste) => ({ ...poste, interaction: { ...poste.interaction, saved: !isCurrentlySaved } }))
      }
    } catch (err) {
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible de modifier les favoris"))
    } finally {
      setSaveLoading((prev) => ({ ...prev, [articleId]: false }))
    }
  }

  const handleFollow = async (targetUserId, isCurrentlyFollowing) => {
    if (user.id === targetUserId) return

    setFollowLoading((prev) => ({ ...prev, [targetUserId]: true }))
    try {
      if (isCurrentlyFollowing) {
        await unfollowUser(targetUserId)
      } else {
        await followUser(targetUserId)
      }
      setPosts((prev) =>
        prev.map((poste) =>
          poste.user.id === targetUserId
            ? { ...poste, interaction: { ...poste.interaction, following: !isCurrentlyFollowing } }
            : poste,
        ),
      )
    } catch (err) {
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible de modifier l'abonnement"))
    } finally {
      setFollowLoading((prev) => ({ ...prev, [targetUserId]: false }))
    }
  }

  return {
    user,
    commentInputs,
    postComments,
    showComments,
    commentLoading,
    deleteCommentLoading,
    likeLoading,
    saveLoading,
    followLoading,
    toggleComments,
    handleCommentChange,
    handleCommentSubmit,
    handleDeleteComment,
    handleLike,
    handleSave,
    handleFollow,
  }
}
