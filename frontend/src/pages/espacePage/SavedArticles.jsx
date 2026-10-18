import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowLeft } from "lucide-react"
import { FaBookmark } from "react-icons/fa"
import { LoadingOverlay } from "@mantine/core"
import { getSavedArticles } from "../../api/articleService"
import { errorNotification } from "../../utils/notifications"
import { getApiErrorMessage } from "../../api/apiError"
import { usePostInteractions } from "../../hooks/usePostInteractions"
import PostCard, { FollowButton, UnsaveButton } from "../../components/posts/PostCard"

const SavedArticles = () => {
    const navigate = useNavigate()
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const interactions = usePostInteractions(posts, setPosts, { removeWhenUnsaved: true })
    const { user } = interactions

    useEffect(() => {
        setLoading(true);
        getSavedArticles()
            .then((data) => {
                setPosts(data.savedArticles || []);
                setLoading(false);
            })
            .catch((err) => {
                setLoading(false);
                console.error(err);
                errorNotification('Erreur', getApiErrorMessage(err, 'Impossible de charger les articles sauvegardés'));
            });
    }, []);

    const handleBack = () => {
        navigate("/espace")
    }

    return (
        <div className="min-h-screen p-4 md:p-6 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%">
            <LoadingOverlay
                visible={loading}
                zIndex={1000}
                overlayProps={{ radius: 'sm', blur: 2 }}
                loaderProps={{ color: '#A09F87', type: 'bars' }}
            />

            {/* Header */}
            <div className="flex items-center justify-between mb-8">
                <button
                    onClick={handleBack}
                    className="text-[#A09F87] hover:text-white hover:bg-[#4C3163] p-2 rounded transition-colors"
                >
                    <ArrowLeft className="w-6 h-6" />
                </button>
                <h1 className="text-2xl md:text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#A09F87] to-white">
                    ARTICLES SAUVEGARDÉS
                </h1>
                <div className="flex items-center gap-2 text-[#A09F87]">
                    <FaBookmark className="w-5 h-5" />
                    <span className="text-sm">{posts.length} article{posts.length !== 1 ? 's' : ''}</span>
                </div>
            </div>

            {/* Articles sauvegardés */}
            <div className="max-w-2xl mx-auto space-y-6">
                {posts.length === 0 ? (
                    <div className="text-center py-12">
                        <FaBookmark className="w-16 h-16 text-[#A09F87] mx-auto mb-4 opacity-50" />
                        <h3 className="text-xl font-semibold text-white mb-2">Aucun article sauvegardé</h3>
                        <p className="text-gray-400 mb-6">Commencez à sauvegarder des articles qui vous intéressent</p>
                        <button
                            onClick={() => navigate("/espace/fil")}
                            className="bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717] px-6 py-3 rounded-lg font-medium transition-colors"
                        >
                            Découvrir des articles
                        </button>
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
                                    <UnsaveButton poste={poste} interactions={interactions} />
                                </>
                            }
                        />
                    ))
                )}
            </div>
        </div>
    )
}

export default SavedArticles