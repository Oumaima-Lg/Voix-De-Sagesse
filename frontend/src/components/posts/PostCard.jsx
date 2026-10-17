import { Link } from "react-router-dom"
import { Heart, MessageCircle, UserPlus, MoreHorizontal, Bookmark, Trash2 } from "lucide-react"
import { FaQuoteLeft, FaHeart, FaBookmark } from "react-icons/fa"
import { BiBook } from "react-icons/bi"
import profileAvatar from "../../assets/profileAvatar.webp"
import { fileUrl } from "../../config"

const Spinner = ({ className = "w-4 h-4 border-2" }) => (
  <div className={`${className} border-current border-t-transparent rounded-full animate-spin`}></div>
)

// One post of a list, with its comments. `actions` are the page-specific buttons shown top right;
// `interactions` comes from usePostInteractions.
const PostCard = ({ poste, interactions, actions, linkToAuthor = true, showYou = true }) => {
  const {
    user, commentInputs, postComments, showComments, commentLoading, deleteCommentLoading, likeLoading,
    toggleComments, handleCommentChange, handleCommentSubmit, handleDeleteComment, handleLike,
  } = interactions
  const articleId = poste.article.id
  const comments = postComments[articleId]

  const avatar = (
    <>
      {poste.user.profilePicture ? (
        <img
          src={fileUrl(poste.user.profilePicture)}
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
    </>
  )

  return (
    <div className="bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-2 rounded-lg shadow-lg">
      {/* Header */}
      <div className="p-6 pb-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full border-2 border-[#4C3163] overflow-hidden">
              {linkToAuthor ? <Link to={`/espace/auteur/${poste.user.id}`}>{avatar}</Link> : avatar}
            </div>
            <div>
              <h3 className="text-white font-semibold">
                {showYou && user.id === poste.user.id ? "Vous" : poste.user.nom + " " + poste.user.prenom}
              </h3>
              <p className="text-[#A09F87] text-sm">
                @{poste.user.username} • {poste.createdAt}
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">{actions}</div>
        </div>
      </div>

      {/* Content */}
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

        {/* Like and comment counters */}
        <div className="flex items-center justify-between pt-4 border-t border-[#4C3163]">
          <div className="flex items-center gap-6">
            <button
              onClick={() => handleLike(articleId)}
              disabled={likeLoading[articleId]}
              className={`flex items-center gap-2 transition-colors ${likeLoading[articleId]
                ? "opacity-50 cursor-not-allowed"
                : poste.interaction.liked
                  ? "text-red-500 hover:text-red-400"
                  : "text-[#A09F87] hover:text-red-500"
                }`}
            >
              {likeLoading[articleId] ? (
                <Spinner />
              ) : poste.interaction.liked ? (
                <FaHeart className="w-4 h-4" />
              ) : (
                <Heart className="w-4 h-4" />
              )}
              {poste.article.likes}
            </button>
            <button
              onClick={() => toggleComments(articleId)}
              className="flex items-center gap-2 text-[#A09F87] hover:text-white transition-colors"
            >
              <MessageCircle className="w-4 h-4" />
              {poste.article.comments}
            </button>
          </div>
        </div>

        {/* Comments */}
        {showComments[articleId] && (
          <div className="mt-4 border-t border-[#4C3163] pt-4">
            <div className={`space-y-3 ${comments?.length > 2 ? "max-h-64 overflow-y-auto pr-2" : ""}`}>
              {comments?.map((comment) => (
                <div key={comment.id} className="flex gap-3">
                  <div className="w-8 h-8 rounded-full overflow-hidden border border-[#4C3163] flex-shrink-0">
                    {comment.user.profilePicture ? (
                      <img
                        src={fileUrl(comment.user.profilePicture)}
                        alt={`${comment.user.nom} ${comment.user.prenom}`}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.target.src = profileAvatar
                        }}
                      />
                    ) : (
                      <img src={profileAvatar} alt="Avatar par défaut" className="w-full h-full object-cover" />
                    )}
                  </div>
                  <div className="flex-1 bg-[#202020] rounded-lg p-3 relative">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-white font-medium text-sm">
                        {comment.user.nom} {comment.user.prenom}
                      </span>
                      <span className="text-[#A09F87] text-xs">@{comment.user.username}</span>
                      <span className="text-gray-400 text-xs">• {comment.createdAt}</span>

                      {/* The comment's author and the article's author can delete it */}
                      {(comment.user.id === user.id || poste.user.id === user.id) && (
                        <button
                          onClick={() => handleDeleteComment(comment.id, articleId)}
                          disabled={deleteCommentLoading[comment.id]}
                          className="ml-auto p-1 text-gray-400 hover:text-red-500 transition-colors"
                          title="Supprimer le commentaire"
                        >
                          {deleteCommentLoading[comment.id] ? (
                            <Spinner className="w-3 h-3 border" />
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

              {comments?.length === 0 && (
                <p className="text-gray-400 text-center py-4">Aucun commentaire pour le moment</p>
              )}
            </div>

            {comments?.length > 2 && (
              <div className="text-center mt-2">
                <p className="text-[#A09F87] text-xs">Faites défiler pour voir plus de commentaires</p>
              </div>
            )}
          </div>
        )}

        {/* New comment */}
        <div className="flex gap-3 pt-4 pb-6">
          <div className="w-8 h-8 rounded-full overflow-hidden border border-[#4C3163]">
            {user.profilePicture ? (
              <img
                src={fileUrl(user.profilePicture)}
                alt="Votre avatar"
                className="w-full h-full object-cover"
                onError={(e) => {
                  e.target.src = profileAvatar
                }}
              />
            ) : (
              <img src={profileAvatar} alt="Avatar par défaut" className="w-full h-full object-cover" />
            )}
          </div>
          <div className="flex-1 flex gap-2">
            <input
              value={commentInputs[articleId] || ""}
              onChange={(e) => handleCommentChange(articleId, e.target.value)}
              placeholder="Ajouter un commentaire..."
              maxLength={500}
              className="flex-1 bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault()
                  handleCommentSubmit(articleId)
                }
              }}
              disabled={commentLoading[articleId]}
            />
            <button
              onClick={() => handleCommentSubmit(articleId)}
              disabled={!commentInputs[articleId]?.trim() || commentLoading[articleId]}
              className={`px-4 py-2 rounded font-medium transition-colors ${!commentInputs[articleId]?.trim() || commentLoading[articleId]
                ? "bg-gray-500 text-gray-300 cursor-not-allowed"
                : "bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717]"
                }`}
            >
              {commentLoading[articleId] ? <Spinner /> : "Publier"}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

// --- Buttons for the top-right `actions` of a PostCard

export const FollowButton = ({ poste, interactions }) => {
  const { followLoading, handleFollow } = interactions
  const loading = followLoading[poste.user.id]
  return (
    <button
      onClick={() => handleFollow(poste.user.id, poste.interaction.following)}
      disabled={loading}
      className={`px-3 py-1 text-sm rounded transition-colors flex items-center gap-1 min-w-[80px] justify-center ${loading
        ? "bg-gray-500 text-gray-300 cursor-not-allowed"
        : poste.interaction.following
          ? "bg-[#A09F87] text-[#171717] hover:bg-[#A09F87]/80"
          : "border border-[#A09F87] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717]"
        }`}
    >
      {loading ? (
        <Spinner />
      ) : (
        <>
          {!poste.interaction.following && <UserPlus className="w-4 h-4" />}
          {poste.interaction.following ? "Suivi" : "Suivre"}
        </>
      )}
    </button>
  )
}

// Toggles the saved state (feed, my posts).
export const SaveButton = ({ poste, interactions }) => {
  const { saveLoading, handleSave } = interactions
  const loading = saveLoading[poste.article.id]
  return (
    <button
      onClick={() => handleSave(poste.article.id, poste.interaction.saved)}
      disabled={loading}
      className={`p-2 rounded transition-colors ${loading
        ? "text-gray-500 cursor-not-allowed"
        : poste.interaction.saved
          ? "text-yellow-500 hover:text-yellow-400"
          : "text-[#A09F87] hover:text-yellow-500"
        }`}
      title={poste.interaction.saved ? "Retirer des favoris" : "Ajouter aux favoris"}
    >
      {loading ? (
        <Spinner />
      ) : poste.interaction.saved ? (
        <FaBookmark className="w-4 h-4" />
      ) : (
        <Bookmark className="w-4 h-4" />
      )}
    </button>
  )
}

// Removes a post from the saved articles (saved articles page).
export const UnsaveButton = ({ poste, interactions }) => {
  const { saveLoading, handleSave } = interactions
  return (
    <button
      onClick={() => handleSave(poste.article.id, true)}
      disabled={saveLoading[poste.article.id]}
      className="p-2 rounded transition-colors text-yellow-500 hover:text-red-500"
      title="Retirer des favoris"
    >
      {saveLoading[poste.article.id] ? <Spinner /> : <FaBookmark className="w-4 h-4" />}
    </button>
  )
}

export const DeleteButton = ({ loading, onDelete }) => (
  <button
    onClick={onDelete}
    disabled={loading}
    className={`p-2 rounded transition-colors ${loading ? "text-gray-500 cursor-not-allowed" : "text-[#A09F87] hover:text-red-500"}`}
    title="Supprimer l'article"
  >
    {loading ? <Spinner /> : <Trash2 className="w-4 h-4" />}
  </button>
)

export const ReportMenu = ({ open, onToggle, onReport }) => (
  <div className="relative">
    <button onClick={onToggle} className="text-[#A09F87] hover:text-white p-1">
      <MoreHorizontal className="w-4 h-4" />
    </button>
    {open && (
      <div className="absolute right-0 top-8 mt-2 w-32 bg-[#202020] border border-[#4C3163] rounded shadow-lg z-10">
        <button onClick={onReport} className="w-full text-left px-3 py-2 text-white hover:bg-[#4C3163] text-sm">
          Signaler
        </button>
      </div>
    )}
  </div>
)

export default PostCard
