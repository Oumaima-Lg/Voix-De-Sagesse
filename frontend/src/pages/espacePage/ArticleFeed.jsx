import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowLeft } from "lucide-react"
import { LoadingOverlay } from "@mantine/core"
import { DisplayPosts, searchArticles } from "../../api/articleService"
import { reportArticle } from "../../api/signalService"
import { getApiErrorMessage } from "../../api/apiError"
import { errorNotification, successNotification } from "../../utils/notifications"
import { usePostInteractions } from "../../hooks/usePostInteractions"
import PostCard, { FollowButton, SaveButton, ReportMenu } from "../../components/posts/PostCard"
import SearchBar from "./SearchBar"
import ScrollToTop from "./ScrollToTop"

const ArticleFeed = () => {
  const navigate = useNavigate()
  const [showDropdown, setShowDropdown] = useState(null)
  const [posts, setPosts] = useState([])
  const [allPosts, setAllPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const interactions = usePostInteractions(posts, setPosts)
  const { user } = interactions
  const [searchLoading, setSearchLoading] = useState(false)
  const [isSearchActive, setIsSearchActive] = useState(false)
  const [searchFilters, setSearchFilters] = useState({ searchText: "", type: "" })
  const [showReportModal, setShowReportModal] = useState(null)
  const [reportData, setReportData] = useState({ reason: "", description: "" })
  const [reportLoading, setReportLoading] = useState(false)

  useEffect(() => {
    loadAllPosts()
  }, [])

  const loadAllPosts = () => {
    setLoading(true)
    DisplayPosts()
      .then((data) => {
        setAllPosts(data)
        setPosts(data)
        setLoading(false)
      })
      .catch((err) => {
        setLoading(false)
        errorNotification("Erreur", getApiErrorMessage(err, "Impossible de charger les articles"))
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
      const searchResults = await searchArticles(filters)
      setPosts(searchResults)
    } catch (error) {
      errorNotification("Erreur", getApiErrorMessage(error, "La recherche a échoué"))
      setPosts(allPosts)
      setIsSearchActive(false)
    } finally {
      setSearchLoading(false)
    }
  }

  const handleBack = () => {
    navigate("/espace")
  }

  const handleReport = async (articleId) => {
    if (!reportData.reason.trim()) {
      errorNotification("Signalement", "Veuillez sélectionner une raison pour le signalement")
      return
    }

    setReportLoading(true)
    try {
      await reportArticle({
        articleId: articleId,
        reason: reportData.reason,
        description: reportData.description
      })

      setPosts(prev => prev.filter(post => post.article.id !== articleId))

      setShowReportModal(null)
      setReportData({ reason: "", description: "" })
      successNotification("Succès", "Article signalé avec succès")
    } catch (error) {
      errorNotification("Erreur", getApiErrorMessage(error, "Erreur lors du signalement"))
    } finally {
      setReportLoading(false)
    }
  }

  return (
    <div className="min-h-screen p-4 md:p-6 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%">
      <LoadingOverlay
        visible={loading}
        zIndex={1000}
        overlayProps={{ radius: "sm", blur: 2 }}
        loaderProps={{ color: "#A09F87", type: "bars" }}
      />

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

      <SearchBar
        onSearch={handleSearch}
        currentFilters={searchFilters}
      />

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
            <PostCard
              key={poste.article.id}
              poste={poste}
              interactions={interactions}
              actions={
                <>
                  {user.id !== poste.user.id && <FollowButton poste={poste} interactions={interactions} />}
                  <SaveButton poste={poste} interactions={interactions} />
                  {user.id !== poste.user.id && (
                    <ReportMenu
                      open={showDropdown === poste.article.id}
                      onToggle={() => setShowDropdown(showDropdown === poste.article.id ? null : poste.article.id)}
                      onReport={() => {
                        setShowReportModal(poste.article.id)
                        setShowDropdown(null)
                      }}
                    />
                  )}
                </>
              }
            />
          ))
        )}
      </div>

      {showReportModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-[#202020] border border-[#4C3163] rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-white text-lg font-semibold mb-4">Signaler cet article</h3>

            <div className="space-y-4">
              <div>
                <label className="text-[#A09F87] text-sm mb-2 block">Raison du signalement *</label>
                <select
                  value={reportData.reason}
                  onChange={(e) => setReportData(prev => ({ ...prev, reason: e.target.value }))}
                  className="w-full bg-[#171717] border border-[#4C3163] text-white p-2 rounded focus:border-[#A09F87] outline-none"
                >
                  <option value="">Sélectionner une raison</option>
                  <option value="Contenu inapproprié">Contenu inapproprié</option>
                  <option value="Spam">Spam</option>
                  <option value="Harcèlement">Harcèlement</option>
                  <option value="Fausses informations">Fausses informations</option>
                  <option value="Violation de droits d'auteur">Violation de droits d'auteur</option>
                  <option value="Autre">Autre</option>
                </select>
              </div>

              <div>
                <label className="text-[#A09F87] text-sm mb-2 block">Description (optionnel)</label>
                <textarea
                  value={reportData.description}
                  onChange={(e) => setReportData(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="Décrivez le problème..."
                  maxLength={500}
                  rows={3}
                  className="w-full bg-[#171717] border border-[#4C3163] text-white p-2 rounded focus:border-[#A09F87] outline-none resize-none"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => {
                  setShowReportModal(null)
                  setReportData({ reason: "", description: "" })
                }}
                disabled={reportLoading}
                className="flex-1 px-4 py-2 border border-[#4C3163] text-[#A09F87] rounded hover:bg-[#4C3163] transition-colors"
              >
                Annuler
              </button>
              <button
                onClick={() => handleReport(showReportModal)}
                disabled={reportLoading || !reportData.reason.trim()}
                className={`flex-1 px-4 py-2 rounded font-medium transition-colors ${reportLoading || !reportData.reason.trim()
                    ? "bg-gray-500 text-gray-300 cursor-not-allowed"
                    : "bg-red-600 hover:bg-red-700 text-white"
                  }`}
              >
                {reportLoading ? (
                  <div className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin mx-auto"></div>
                ) : (
                  "Signaler"
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ArticleFeed