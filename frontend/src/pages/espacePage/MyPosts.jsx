import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowLeft, Plus } from "lucide-react"
import { BiBook } from "react-icons/bi"
import { LoadingOverlay } from "@mantine/core"
import { getMyPosts, deleteArticle } from "../../api/articleService"
import { getApiErrorMessage } from "../../api/apiError"
import { errorNotification, successNotification } from "../../utils/notifications"
import { usePostInteractions } from "../../hooks/usePostInteractions"
import PostCard, { SaveButton, DeleteButton } from "../../components/posts/PostCard"

const MyPosts = () => {
    const navigate = useNavigate()
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleteLoading, setDeleteLoading] = useState({});
    const interactions = usePostInteractions(posts, setPosts)
    const { user } = interactions

    useEffect(() => {
        setLoading(true);
        getMyPosts()
            .then((data) => {
                setPosts(data.myPosts || []);
                setLoading(false);
            })
            .catch((err) => {
                setLoading(false);
                console.error(err);
                errorNotification('Erreur', getApiErrorMessage(err, 'Impossible de charger vos articles'));
            });
    }, [user.id]);

    const handleBack = () => {
        navigate("/espace")
    }

    const handleDelete = async (articleId) => {
        const article = posts.find(p => p.article.id === articleId);
        const articleTitle = article?.article.title || article?.article.content?.substring(0, 50) + '...';

        if (window.confirm(`Êtes-vous sûr de vouloir supprimer cet article ?\n\n"${articleTitle}"\n\nCette action est irréversible et supprimera également tous les likes et sauvegardes associés.`)) {
            setDeleteLoading(prev => ({ ...prev, [articleId]: true }));

            try {
                await deleteArticle(articleId);
                setPosts(prev => prev.filter(poste => poste.article.id !== articleId));
                successNotification('Succès', 'Article supprimé avec succès');
            } catch (err) {
                errorNotification('Erreur', getApiErrorMessage(err, 'Impossible de supprimer l\'article'));
            } finally {
                setDeleteLoading(prev => ({ ...prev, [articleId]: false }));
            }
        }
    };

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
                    MES POSTES
                </h1>
                <div className="flex items-center gap-4">
                    <span className="text-[#A09F87] text-sm">{posts.length} article{posts.length !== 1 ? 's' : ''}</span>
                    <button
                        onClick={() => navigate("/espace/publier")}
                        className="bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717] p-2 rounded-lg transition-colors"
                        title="Créer un nouvel article"
                    >
                        <Plus className="w-5 h-5" />
                    </button>
                </div>
            </div>

            {/* Mes postes */}
            <div className="max-w-2xl mx-auto space-y-6">
                {posts.length === 0 ? (
                    <div className="text-center py-12">
                        <BiBook className="w-16 h-16 text-[#A09F87] mx-auto mb-4 opacity-50" />
                        <h3 className="text-xl font-semibold text-white mb-2">Aucun article publié</h3>
                        <p className="text-gray-400 mb-6">Commencez à partager vos sagesses et expériences</p>
                        <button
                            onClick={() => navigate("/espace/publier")}
                            className="bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717] px-6 py-3 rounded-lg font-medium transition-colors flex items-center gap-2 mx-auto"
                        >
                            <Plus className="w-5 h-5" />
                            Créer mon premier article
                        </button>
                    </div>
                ) : (
                    posts.map((poste) => (
                        <PostCard
                            key={poste.article.id}
                            poste={poste}
                            interactions={interactions}
                            linkToAuthor={false}
                            showYou={false}
                            actions={
                                <>
                                    <SaveButton poste={poste} interactions={interactions} />
                                    <DeleteButton loading={deleteLoading[poste.article.id]} onDelete={() => handleDelete(poste.article.id)} />
                                </>
                            }
                        />
                    ))
                )}
            </div>
        </div>
    )
}

export default MyPosts