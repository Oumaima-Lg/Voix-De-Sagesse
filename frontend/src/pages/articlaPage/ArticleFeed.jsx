import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowLeft, Heart, MessageCircle, Share2, UserPlus, MoreHorizontal, Bookmark, Trash2 } from "lucide-react"
import { FaQuoteLeft, FaHeart, FaBookmark } from "react-icons/fa"
import { BiBook } from "react-icons/bi"
import { LoadingOverlay } from "@mantine/core"
import { useSelector } from "react-redux"
import { DisplayPosts, likeArticle, unlikeArticle, searchArticles } from "../../services/ArticleService"
import { followUser, unfollowUser, saveArticle, unsaveArticle } from "../../services/UserService"
import { addComment, getCommentsByArticle, deleteComment } from "../../services/CommentService"
import profileAvatar from "../../assets/profileAvatar.jpg"
import { Link } from "react-router-dom"
import SearchBar from "./SearchBar"
import ScrollToTop from "./ScrollToTop" 

const posteFeed = () => {
  const navigate = useNavigate()
  const [commentInputs, setCommentInputs] = useState({})
  const [postComments, setPostComments] = useState({})
  const [showDropdown, setShowDropdown] = useState(null)
  const [showComments, setShowComments] = useState({})
  const [commentLoading, setCommentLoading] = useState({})
  const [deleteCommentLoading, setDeleteCommentLoading] = useState({})
  const user = useSelector((state) => state.user)
  const [posts, setPosts] = useState([])
  const [allPosts, setAllPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [followLoading, setFollowLoading] = useState({})
  const [saveLoading, setSaveLoading] = useState({})
  const [likeLoading, setLikeLoading] = useState({})
  const [searchLoading, setSearchLoading] = useState(false)
  const [isSearchActive, setIsSearchActive] = useState(false)
  const [searchFilters, setSearchFilters] = useState({ searchText: "", type: "" })

  useEffect(() => {
    loadAllPosts()
  }, [])

  const loadAllPosts = () => {
    setLoading(true)
    DisplayPosts(user.id)
      .then((data) => {
        console.log(data)
        setAllPosts(data)
        setPosts(data)
        setLoading(false)
      })
      .catch((err) => {
        setLoading(false)
        console.error(err)
      })
  }

  const handleSearch = async (filters) => {
    setSearchFilters(filters)
    
    if (!filters.searchText && !filters.type) {
      setPosts(allPosts)
      setIsSearchActive(false)
      return
    }

    setSearchLoading(true)
    setIsSearchActive(true)

    try {
      const searchResults = await searchArticles(user.id, filters)
      setPosts(searchResults)
      console.log(`Recherche terminée - ${searchResults.length} résultats trouvés`)
    } catch (error) {
      console.error("Erreur lors de la recherche:", error)
      setPosts(allPosts)
      setIsSearchActive(false)
    } finally {
      setSearchLoading(false)
    }
  }

  const loadComments = async (articleId) => {
    try {
      const response = await getCommentsByArticle(articleId)
      setPostComments((prev) => ({
        ...prev,
        [articleId]: response.comments || [],
      }))
    } catch (err) {
      console.error("Erreur lors du chargement des commentaires:", err)
    }
  }

  const toggleComments = async (articleId) => {
    const isCurrentlyShown = showComments[articleId]
    setShowComments((prev) => ({ ...prev, [articleId]: !isCurrentlyShown }))
    if (!isCurrentlyShown && !postComments[articleId]) {
      await loadComments(articleId)
    }
  }

  const handleCommentSubmit = async (articleId) => {
    const content = commentInputs[articleId]
    if (!content?.trim()) return

    setCommentLoading((prev) => ({ ...prev, [articleId]: true }))

    try {
      const response = await addComment(articleId, content.trim())
      await loadComments(articleId)

      setPosts((prev) =>
        prev.map((post) =>
          post.article.id === articleId
            ? {
              ...post,
              article: {
                ...post.article,
                comments: post.article.comments + 1,
              },
            }
            : post,
        ),
      )

      setCommentInputs((prev) => ({ ...prev, [articleId]: "" }))
      setShowComments((prev) => ({ ...prev, [articleId]: true }))
    } catch (err) {
      console.error("Erreur lors de l'ajout du commentaire:", err)
    } finally {
      setCommentLoading((prev) => ({ ...prev, [articleId]: false }))
    }
  }

  const handleDeleteComment = async (commentId, articleId) => {
    if (!window.confirm("Êtes-vous sûr de vouloir supprimer ce commentaire ?")) {
      return
    }

    setDeleteCommentLoading((prev) => ({ ...prev, [commentId]: true }))

    try {
      const response = await deleteComment(commentId)
      setPostComments((prev) => ({
        ...prev,
        [articleId]: prev[articleId]?.filter((comment) => comment.id !== commentId) || [],
      }))

      setPosts((prev) =>
        prev.map((post) =>
          post.article.id === articleId
            ? {
              ...post,
              article: {
                ...post.article,
                comments: Math.max(0, post.article.comments - 1),
              },
            }
            : post,
        ),
      )
    } catch (err) {
      console.error("Erreur lors de la suppression:", err)
    } finally {
      setDeleteCommentLoading((prev) => ({ ...prev, [commentId]: false }))
    }
  }

  const handleBack = () => {
    navigate("/articla")
  }

  const handleLike = async (posteId) => {
    if (likeLoading[posteId]) return

    setLikeLoading((prev) => ({ ...prev, [posteId]: true }))

    try {
      const poste = posts.find((p) => p.article.id === posteId)
      if (!poste) throw new Error("Article non trouvé")

      const isCurrentlyLiked = poste.interaction.liked

      if (isCurrentlyLiked) {
        await unlikeArticle(posteId, user.id)
      } else {
        await likeArticle(posteId, user.id)
      }

      setPosts((prev) =>
        prev.map((poste) =>
          poste.article.id === posteId
            ? {
              ...poste,
              interaction: {
                ...poste.interaction,
                liked: !isCurrentlyLiked,
              },
              article: {
                ...poste.article,
                likes: isCurrentlyLiked ? Math.max(0, poste.article.likes - 1) : poste.article.likes + 1,
              },
            }
            : poste,
        ),
      )
    } catch (err) {
      console.error("Erreur lors du like:", err)
    } finally {
      setTimeout(() => {
        setLikeLoading((prev) => ({ ...prev, [posteId]: false }))
      }, 300)
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
            ? {
              ...poste,
              interaction: {
                ...poste.interaction,
                following: !isCurrentlyFollowing,
              },
            }
            : poste,
        ),
      )
    } catch (err) {
      console.error("Erreur lors du suivi:", err)
    } finally {
      setFollowLoading((prev) => ({ ...prev, [targetUserId]: false }))
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

      setPosts((prev) =>
        prev.map((poste) =>
          poste.article.id === articleId
            ? {
              ...poste,
              interaction: {
                ...poste.interaction,
                saved: !isCurrentlySaved,
              },
            }
            : poste,
        ),
      )
    } catch (err) {
      console.error("Erreur lors de la sauvegarde:", err)
    } finally {
      setSaveLoading((prev) => ({ ...prev, [articleId]: false }))
    }
  }

  const handleCommentChange = (posteId, value) => {
    setCommentInputs((prev) => ({ ...prev, [posteId]: value }))
  }

  return (
    <div className="min-h-screen p-4 md:p-6 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%">
      <LoadingOverlay
        visible={loading}
        zIndex={1000}
        overlayProps={{ radius: "sm", blur: 2 }}
        loaderProps={{ color: "#A09F87", type: "bars" }}
      />

      {/* ✅ Bouton Scroll to Top */}
      <ScrollToTop />

      <div className="flex items-center justify-between mb-8">
        <button
          onClick={handleBack}
          className="text-[#A09F87] hover:text-white hover:bg-[#4C3163] p-2 rounded transition-colors"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h1 className="text-2xl md:text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#A09F87] to-white">
          POSTES PARTAGÉS
        </h1>
        <div></div>
      </div>

      {/* Barre de recherche */}
      <SearchBar
        onSearch={handleSearch}
        currentFilters={searchFilters}
      />

      {/* Indicateur de résultats de recherche */}
      {isSearchActive && (
        <div className="max-w-2xl mx-auto mb-4">
          <div className="bg-[#4C3163]/30 border border-[#4C3163] rounded-lg p-3">
            <p className="text-[#A09F87] text-sm">
              {searchLoading ? (
                "Recherche en cours..."
              ) : (
                `${posts.length} résultat${posts.length > 1 ? 's' : ''} trouvé${posts.length > 1 ? 's' : ''}`
              )}
            </p>
          </div>
        </div>
      )}

      {/* Loading pour la recherche */}
      {searchLoading && (
        <LoadingOverlay
          visible={true}
          zIndex={999}
          overlayProps={{ radius: "sm", blur: 1 }}
          loaderProps={{ color: "#A09F87", type: "dots" }}
        />
      )}

      <div className="max-w-2xl mx-auto space-y-6">
        {posts.length === 0 && !loading && !searchLoading ? (
          <div className="text-center py-12">
            <p className="text-[#A09F87] text-lg">
              {isSearchActive ? "Aucun article trouvé pour cette recherche" : "Aucun article disponible"}
            </p>
            {isSearchActive && (
              <button
                onClick={() => handleSearch({ searchText: "", type: "" })}
                className="mt-4 px-4 py-2 bg-[#A09F87] text-[#171717] rounded-lg hover:bg-[#A09F87]/80 transition-colors"
              >
                Voir tous les articles
              </button>
            )}
          </div>
        ) : (
          posts.map((poste) => (
            <div
              key={poste.article.id}
              className="bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-2 rounded-lg shadow-lg"
            >
              {/* Header du poste */}
              <div className="p-6 pb-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-full border-2 border-[#4C3163] overflow-hidden">
                      <Link to={`/articla/poste/profile/${poste.user.id}`}>
                        {poste.user.profilePicture ? (
                          <img
                            src={
                              poste.user.profilePicture.startsWith("http")
                                ? poste.user.profilePicture
                                : `http://localhost:8080${poste.user.profilePicture}`
                            }
                            alt={`${poste.user.nom} ${poste.user.prenom}`}
                            className="w-full h-full object-cover"
                            onError={(e) => {
                              e.target.style.display = "none"
                              e.target.nextElementSibling.style.display = "flex"
                            }}
                          />
                        ) : null}
                        <div
                          className="w-full h-full bg-[#4C3163] flex items-center justify-center text-white font-semibold text-sm"
                          style={{ display: poste.user.profilePicture ? "none" : "flex" }}
                        >
                          {(poste.user.nom + " " + poste.user.prenom)
                            .split(" ")
                            .map((n) => n[0])
                            .join("")}
                        </div>
                      </Link>
                    </div>
                    <div>
                      <h3 className="text-white font-semibold">
                        {user.id !== poste.user.id ? poste.user.nom + " " + poste.user.prenom : "Vous"}
                      </h3>
                      <p className="text-[#A09F87] text-sm">
                        @{poste.user.username} • {poste.createdAt}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {user.id !== poste.user.id && (
                      <button
                        onClick={() => handleFollow(poste.user.id, poste.interaction.following)}
                        disabled={followLoading[poste.user.id]}
                        className={`px-3 py-1 text-sm rounded transition-colors flex items-center gap-1 min-w-[80px] justify-center ${followLoading[poste.user.id]
                            ? "bg-gray-500 text-gray-300 cursor-not-allowed"
                            : poste.interaction.following
                              ? "bg-[#A09F87] text-[#171717] hover:bg-[#A09F87]/80"
                              : "border border-[#A09F87] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717]"
                          }`}
                      >
                        {followLoading[poste.user.id] ? (
                          <div className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></div>
                        ) : (
                          <>
                            {!poste.interaction.following && <UserPlus className="w-4 h-4" />}
                            {poste.interaction.following ? "Suivi" : "Suivre"}
                          </>
                        )}
                      </button>
                    )}

                    <button
                      onClick={() => handleSave(poste.article.id, poste.interaction.saved)}
                      disabled={saveLoading[poste.article.id]}
                      className={`p-2 rounded transition-colors ${saveLoading[poste.article.id]
                          ? "text-gray-500 cursor-not-allowed"
                          : poste.interaction.saved
                            ? "text-yellow-500 hover:text-yellow-400"
                            : "text-[#A09F87] hover:text-yellow-500"
                        }`}
                      title={poste.interaction.saved ? "Retirer des favoris" : "Ajouter aux favoris"}
                    >
                      {saveLoading[poste.article.id] ? (
                        <div className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></div>
                      ) : poste.interaction.saved ? (
                        <FaBookmark className="w-4 h-4" />
                      ) : (
                        <Bookmark className="w-4 h-4" />
                      )}
                    </button>

                    {user.id !== poste.user.id && (
                      <div className="relative">
                        <button
                          onClick={() => setShowDropdown(showDropdown === poste.article.id ? null : poste.article.id)}
                          className="text-[#A09F87] hover:text-white p-1"
                        >
                          <MoreHorizontal className="w-4 h-4" />
                        </button>
                        {showDropdown === poste.article.id && (
                          <div className="absolute right-0 mt-2 w-32 bg-[#202020] border border-[#4C3163] rounded shadow-lg z-10">
                            <button className="w-full text-left px-3 py-2 text-white hover:bg-[#4C3163] text-sm">
                              Signaler
                            </button>
                            <button className="w-full text-left px-3 py-2 text-white hover:bg-[#4C3163] text-sm">
                              Masquer
                            </button>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>
              </div>

              {/* Contenu du poste */}
              <div className="px-6 space-y-4">
                <div className="flex items-center gap-2 mb-3">
                  {poste.article.type === "SAGESSE" ? (
                    <FaQuoteLeft className="text-[#A09F87] text-lg" />
                  ) : (
                    <BiBook className="text-[#A09F87] text-lg" />
                  )}
                  <span className="text-[#A09F87] text-sm font-medium">{poste.article.category}</span>
                </div>

                {poste.article.type === "SAGESSE" ? (
                  <div className="space-y-3">
                    <blockquote className="text-white text-lg italic leading-relaxed border-l-4 border-[#A09F87] pl-4">
                      "{poste.article.content}"
                    </blockquote>
                    {poste.username && <p className="text-[#A09F87] text-right">— {poste.user.username}</p>}
                    {poste.article.source && <p className="text-gray-400 text-sm">{poste.article.source}</p>}
                  </div>
                ) : (
                  <div className="space-y-4">
                    <h2 className="text-white text-xl font-semibold">{poste.article.title}</h2>
                    <p className="text-gray-300 leading-relaxed">{poste.article.content}</p>
                    {poste.article.lesson && (
                      <div className="bg-[#4C3163]/30 p-4 rounded-lg border-l-4 border-[#A09F87]">
                        <p className="text-[#A09F87] font-medium text-sm mb-1">Leçon tirée :</p>
                        <p className="text-white italic">"{poste.article.lesson}"</p>
                      </div>
                    )}
                  </div>
                )}

                <div className="flex flex-wrap gap-2">
                  {poste.article.tags.map((tag, index) => (
                    <span key={index} className="px-3 py-1 bg-[#4C3163] text-[#A09F87] text-xs rounded-full">
                      #{tag}
                    </span>
                  ))}
                </div>

                {/* Actions */}
                <div className="flex items-center justify-between pt-4 border-t border-[#4C3163]">
                  <div className="flex items-center gap-6">
                    <button
                      onClick={() => handleLike(poste.article.id)}
                      disabled={likeLoading[poste.article.id]}
                      className={`flex items-center gap-2 transition-colors ${likeLoading[poste.article.id]
                          ? "opacity-50 cursor-not-allowed"
                          : poste.interaction.liked
                            ? "text-red-500 hover:text-red-400"
                            : "text-[#A09F87] hover:text-red-500"
                        }`}
                    >
                      {likeLoading[poste.article.id] ? (
                        <div className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></div>
                      ) : poste.interaction.liked ? (
                        <FaHeart className="w-4 h-4" />
                      ) : (
                        <Heart className="w-4 h-4" />
                      )}
                      {poste.article.likes}
                    </button>
                    <button
                      onClick={() => toggleComments(poste.article.id)}
                      className="flex items-center gap-2 text-[#A09F87] hover:text-white transition-colors"
                    >
                      <MessageCircle className="w-4 h-4" />
                      {poste.article.comments}
                    </button>
                  </div>
                </div>

                {/* Section des commentaires */}
                {showComments[poste.article.id] && (
                  <div className="mt-4 border-t border-[#4C3163] pt-4">
                    <div
                      className={`space-y-3 ${postComments[poste.article.id]?.length > 2 ? "max-h-64 overflow-y-auto pr-2" : ""}`}
                    >
                      {postComments[poste.article.id]?.map((comment) => (
                        <div key={comment.id} className="flex gap-3">
                          <div className="w-8 h-8 rounded-full overflow-hidden border border-[#4C3163] flex-shrink-0">
                            {comment.user.profilePicture ? (
                              <img
                                src={
                                  comment.user.profilePicture.startsWith("http")
                                    ? comment.user.profilePicture
                                    : `http://localhost:8080${comment.user.profilePicture}`
                                }
                                alt={`${comment.user.nom} ${comment.user.prenom}`}
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                  e.target.src = profileAvatar
                                }}
                              />
                            ) : (
                              <img
                                src={profileAvatar || "/placeholder.svg"}
                                alt="Avatar par défaut"
                                className="w-full h-full object-cover"
                              />
                            )}
                          </div>
                          <div className="flex-1 bg-[#202020] rounded-lg p-3 relative">
                            <div className="flex items-center gap-2 mb-1">
                              <span className="text-white font-medium text-sm">
                                {comment.user.nom} {comment.user.prenom}
                              </span>
                              <span className="text-[#A09F87] text-xs">@{comment.user.username}</span>
                              <span className="text-gray-400 text-xs">• {comment.createdAt}</span>

                              {comment.user.id === user.id && (
                                <button
                                  onClick={() => handleDeleteComment(comment.id, poste.article.id)}
                                  disabled={deleteCommentLoading[comment.id]}
                                  className="ml-auto p-1 text-gray-400 hover:text-red-500 transition-colors"
                                  title="Supprimer le commentaire"
                                >
                                  {deleteCommentLoading[comment.id] ? (
                                    <div className="w-3 h-3 border border-current border-t-transparent rounded-full animate-spin"></div>
                                  ) : (
                                    <Trash2 className="w-3 h-3" />
                                  )}
                                </button>
                              )}
                            </div>
                            <p className="text-gray-300 text-sm leading-relaxed">{comment.content}</p>
                          </div>
                        </div>
                      ))}

                      {postComments[poste.article.id]?.length === 0 && (
                        <p className="text-gray-400 text-center py-4">Aucun commentaire pour le moment</p>
                      )}
                    </div>

                    {postComments[poste.article.id]?.length > 2 && (
                      <div className="text-center mt-2">
                        <p className="text-[#A09F87] text-xs">Faites défiler pour voir plus de commentaires</p>
                      </div>
                    )}
                  </div>
                )}

                {/* Champ de saisie des commentaires */}
                <div className="flex gap-3 pt-4 pb-6">
                  <div className="w-8 h-8 rounded-full overflow-hidden border border-[#4C3163]">
                    {user.profilePicture ? (
                      <img
                        src={
                          user.profilePicture.startsWith("http")
                            ? user.profilePicture
                            : `http://localhost:8080${user.profilePicture}`
                        }
                        alt="Votre avatar"
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.target.src = profileAvatar
                        }}
                      />
                    ) : (
                      <img
                        src={profileAvatar || "/placeholder.svg"}
                        alt="Avatar par défaut"
                        className="w-full h-full object-cover"
                      />
                    )}
                  </div>
                  <div className="flex-1 flex gap-2">
                    <input
                      value={commentInputs[poste.article.id] || ""}
                      onChange={(e) => handleCommentChange(poste.article.id, e.target.value)}
                      placeholder="Ajouter un commentaire..."
                      maxLength={500}
                      className="flex-1 bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
                      onKeyPress={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                          e.preventDefault()
                          handleCommentSubmit(poste.article.id)
                        }
                      }}
                      disabled={commentLoading[poste.article.id]}
                    />
                    <button
                      onClick={() => handleCommentSubmit(poste.article.id)}
                      disabled={!commentInputs[poste.article.id]?.trim() || commentLoading[poste.article.id]}
                      className={`px-4 py-2 rounded font-medium transition-colors ${!commentInputs[poste.article.id]?.trim() || commentLoading[poste.article.id]
                          ? "bg-gray-500 text-gray-300 cursor-not-allowed"
                          : "bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717]"
                        }`}
                    >
                      {commentLoading[poste.article.id] ? (
                        <div className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></div>
                      ) : (
                        "Publier"
                      )}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default posteFeed