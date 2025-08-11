import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowLeft, Heart, MessageCircle, Share2, UserPlus, MoreHorizontal } from "lucide-react"
import { FaQuoteLeft, FaHeart } from "react-icons/fa"
import { BiBook } from "react-icons/bi"

// Mock data for demonstration
const mockArticles = [
  {
    id: 1,
    type: "wisdom",
    content: "La patience est amère, mais son fruit est doux.",
    author: "Aristote",
    source: "Philosophie grecque",
    category: "Philosophie",
    tags: ["patience", "sagesse", "philosophie"],
    user: {
      name: "Marie Dubois",
      avatar: "/placeholder.svg?height=40&width=40",
      username: "@marie_d",
    },
    likes: 24,
    comments: 8,
    shares: 3,
    isLiked: false,
    isFollowing: false,
    createdAt: "2h",
  },
  {
    id: 2,
    type: "story",
    title: "Ma première présentation en public",
    content:
      "Il y a deux ans, j'avais une peur bleue de parler en public. Mes mains tremblaient, ma voix se cassait... Mais j'ai décidé de m'inscrire à un club de prise de parole. La première fois, j'ai bégayé pendant 5 minutes sur un sujet que je maîtrisais parfaitement. Mais j'y suis retournée. Encore et encore. Aujourd'hui, je donne des conférences devant 200 personnes et j'adore ça !",
    lesson: "La peur ne disparaît pas, on apprend juste à danser avec elle.",
    category: "Inspirante",
    tags: ["courage", "développement personnel", "peur"],
    user: {
      name: "Ahmed Benali",
      avatar: "/placeholder.svg?height=40&width=40",
      username: "@ahmed_b",
    },
    likes: 156,
    comments: 23,
    shares: 12,
    isLiked: true,
    isFollowing: true,
    createdAt: "4h",
  },
  {
    id: 3,
    type: "wisdom",
    content: "Un arbre qui tombe fait plus de bruit qu'une forêt qui pousse.",
    author: "Proverbe africain",
    source: "Sagesse populaire africaine",
    category: "Sagesse populaire",
    tags: ["nature", "croissance", "discrétion"],
    user: {
      name: "Fatima Kone",
      avatar: "/placeholder.svg?height=40&width=40",
      username: "@fatima_k",
    },
    likes: 89,
    comments: 15,
    shares: 7,
    isLiked: false,
    isFollowing: false,
    createdAt: "1j",
  },
]

const ArticleFeed = () => {
  const navigate = useNavigate()
  const [articles, setArticles] = useState(mockArticles)
  const [commentInputs, setCommentInputs] = useState({})
  const [showDropdown, setShowDropdown] = useState(null)

  const handleBack = () => {
    navigate("/articla")
  }

  const handleLike = (articleId) => {
    setArticles((prev) =>
      prev.map((article) =>
        article.id === articleId
          ? {
              ...article,
              isLiked: !article.isLiked,
              likes: article.isLiked ? article.likes - 1 : article.likes + 1,
            }
          : article,
      ),
    )
  }

  const handleFollow = (articleId) => {
    setArticles((prev) =>
      prev.map((article) => (article.id === articleId ? { ...article, isFollowing: !article.isFollowing } : article)),
    )
  }

  const handleCommentChange = (articleId, value) => {
    setCommentInputs((prev) => ({ ...prev, [articleId]: value }))
  }

  const handleCommentSubmit = (articleId) => {
    const comment = commentInputs[articleId]
    if (comment?.trim()) {
      console.log("Comment submitted:", { articleId, comment })
      setCommentInputs((prev) => ({ ...prev, [articleId]: "" }))
    }
  }

  return (
    <div className="min-h-screen p-4 md:p-6 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <button
          onClick={handleBack}
          className="text-[#A09F87] hover:text-white hover:bg-[#4C3163] p-2 rounded transition-colors"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h1 className="text-2xl md:text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#A09F87] to-white">
          ARTICLES PARTAGÉS
        </h1>
        <div></div>
      </div>

      {/* Articles Feed */}
      <div className="max-w-2xl mx-auto space-y-6">
        {articles.map((article) => (
          <div
            key={article.id}
            className="bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-2 rounded-lg shadow-lg"
          >
            {/* Header */}
            <div className="p-6 pb-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-full border-2 border-[#4C3163] bg-[#4C3163] flex items-center justify-center text-white font-semibold">
                    {article.user.name
                      .split(" ")
                      .map((n) => n[0])
                      .join("")}
                  </div>
                  <div>
                    <h3 className="text-white font-semibold">{article.user.name}</h3>
                    <p className="text-[#A09F87] text-sm">
                      {article.user.username} • {article.createdAt}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleFollow(article.id)}
                    className={`px-3 py-1 text-sm rounded transition-colors flex items-center gap-1 ${
                      article.isFollowing
                        ? "bg-[#A09F87] text-[#171717] hover:bg-[#A09F87]/80"
                        : "border border-[#A09F87] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717]"
                    }`}
                  >
                    <UserPlus className="w-4 h-4" />
                    {article.isFollowing ? "Suivi" : "Suivre"}
                  </button>
                  <div className="relative">
                    <button
                      onClick={() => setShowDropdown(showDropdown === article.id ? null : article.id)}
                      className="text-[#A09F87] hover:text-white p-1"
                    >
                      <MoreHorizontal className="w-4 h-4" />
                    </button>
                    {showDropdown === article.id && (
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
                </div>
              </div>
            </div>

            {/* Content */}
            <div className="px-6 space-y-4">
              {/* Article Type Icon and Category */}
              <div className="flex items-center gap-2 mb-3">
                {article.type === "wisdom" ? (
                  <FaQuoteLeft className="text-[#A09F87] text-lg" />
                ) : (
                  <BiBook className="text-[#A09F87] text-lg" />
                )}
                <span className="text-[#A09F87] text-sm font-medium">{article.category}</span>
              </div>

              {/* Article Content */}
              {article.type === "wisdom" ? (
                <div className="space-y-3">
                  <blockquote className="text-white text-lg italic leading-relaxed border-l-4 border-[#A09F87] pl-4">
                    "{article.content}"
                  </blockquote>
                  {article.author && <p className="text-[#A09F87] text-right">— {article.author}</p>}
                  {article.source && <p className="text-gray-400 text-sm">{article.source}</p>}
                </div>
              ) : (
                <div className="space-y-4">
                  <h2 className="text-white text-xl font-semibold">{article.title}</h2>
                  <p className="text-gray-300 leading-relaxed">{article.content}</p>
                  {article.lesson && (
                    <div className="bg-[#4C3163]/30 p-4 rounded-lg border-l-4 border-[#A09F87]">
                      <p className="text-[#A09F87] font-medium text-sm mb-1">Leçon tirée :</p>
                      <p className="text-white italic">"{article.lesson}"</p>
                    </div>
                  )}
                </div>
              )}

              {/* Tags */}
              <div className="flex flex-wrap gap-2">
                {article.tags.map((tag, index) => (
                  <span key={index} className="px-3 py-1 bg-[#4C3163] text-[#A09F87] text-xs rounded-full">
                    #{tag}
                  </span>
                ))}
              </div>

              {/* Actions */}
              <div className="flex items-center justify-between pt-4 border-t border-[#4C3163]">
                <div className="flex items-center gap-6">
                  <button
                    onClick={() => handleLike(article.id)}
                    className={`flex items-center gap-2 transition-colors ${
                      article.isLiked ? "text-red-500 hover:text-red-400" : "text-[#A09F87] hover:text-red-500"
                    }`}
                  >
                    {article.isLiked ? <FaHeart className="w-4 h-4" /> : <Heart className="w-4 h-4" />}
                    {article.likes}
                  </button>
                  <button className="flex items-center gap-2 text-[#A09F87] hover:text-white transition-colors">
                    <MessageCircle className="w-4 h-4" />
                    {article.comments}
                  </button>
                  <button className="flex items-center gap-2 text-[#A09F87] hover:text-white transition-colors">
                    <Share2 className="w-4 h-4" />
                    {article.shares}
                  </button>
                </div>
              </div>

              {/* Comment Input */}
              <div className="flex gap-3 pt-2 pb-6">
                <div className="w-8 h-8 rounded-full bg-[#4C3163] flex items-center justify-center text-white text-xs font-semibold">
                  Vous
                </div>
                <div className="flex-1 flex gap-2">
                  <input
                    value={commentInputs[article.id] || ""}
                    onChange={(e) => handleCommentChange(article.id, e.target.value)}
                    placeholder="Ajouter un commentaire..."
                    className="flex-1 bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
                    onKeyPress={(e) => {
                      if (e.key === "Enter") {
                        handleCommentSubmit(article.id)
                      }
                    }}
                  />
                  <button
                    onClick={() => handleCommentSubmit(article.id)}
                    className="bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717] px-4 py-2 rounded font-medium transition-colors"
                  >
                    Publier
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default ArticleFeed
