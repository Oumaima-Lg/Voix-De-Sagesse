import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowLeft, Heart, MessageCircle, Share2, UserPlus, MoreHorizontal } from "lucide-react"
import { FaQuoteLeft, FaHeart } from "react-icons/fa"
import { BiBook } from "react-icons/bi"
import { LoadingOverlay } from "@mantine/core"
import { useSelector } from "react-redux"
import { DisplayPosts, likeArticle, unlikeArticle } from "../../services/ArticleService"
import { followUser, unfollowUser, isFollowing } from "../../services/UserService" // ✅ Import des nouvelles fonctions
import { errorNotification, successNotification } from "../../services/NotificationService" // ✅ Import pour les notifications
import profileAvatar from "../../assets/profileAvatar.jpg"

const posteFeed = () => {
  const navigate = useNavigate()
  const [commentInputs, setCommentInputs] = useState({})
  const [showDropdown, setShowDropdown] = useState(null)
  const user = useSelector((state) => state.user)
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [followLoading, setFollowLoading] = useState({}); // ✅ État pour les boutons de suivi

  useEffect(() => {
    setLoading(true);
    DisplayPosts(user.id)
      .then((data) => {
        console.log(data);
        setPosts(data);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
        console.error(err);
      });
  }, []);

  const handleBack = () => {
    navigate("/articla")
  }

  const handleLike = async (posteId) => {
    try {
      const poste = posts.find((p) => p.article.id === posteId);

      if (poste.interaction.liked) {
        await unlikeArticle(posteId, user.id);
      } else {
        await likeArticle(posteId, user.id);
      }

      // mettre à jour l'état APRES confirmation du backend
      setPosts((prev) =>
        prev.map((poste) =>
          poste.article.id === posteId
            ? {
              ...poste,
              interaction: {
                ...poste.interaction,
                liked: !poste.interaction.liked,
              },
              article: {
                ...poste.article,
                likes: poste.interaction.liked
                  ? poste.article.likes - 1
                  : poste.article.likes + 1,
              },
            }
            : poste
        )
      );
    } catch (err) {
      console.error(err);
      errorNotification('Erreur', 'Impossible de mettre à jour le like');
    }
  };

  // ✅ Nouvelle fonction pour gérer le suivi/désuivi
  const handleFollow = async (targetUserId, isCurrentlyFollowing) => {
    // Vérifier que ce n'est pas l'utilisateur lui-même
    if (user.id === targetUserId) {
      errorNotification('Erreur', 'Vous ne pouvez pas vous suivre vous-même');
      return;
    }

    // Activer le loading pour ce bouton spécifique
    setFollowLoading(prev => ({ ...prev, [targetUserId]: true }));

    try {
      if (isCurrentlyFollowing) {
        // Désuivre l'utilisateur
        await unfollowUser(targetUserId);
        successNotification('Succès', 'Vous ne suivez plus cet utilisateur');
      } else {
        // Suivre l'utilisateur
        await followUser(targetUserId);
        successNotification('Succès', 'Vous suivez maintenant cet utilisateur');
      }

      // Mettre à jour l'état local
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
            : poste
        )
      );

    } catch (err) {
      console.error('Erreur lors du suivi:', err);
      errorNotification('Erreur', err.response?.data?.error || 'Impossible de modifier le suivi');
    } finally {
      // Désactiver le loading
      setFollowLoading(prev => ({ ...prev, [targetUserId]: false }));
    }
  };

  const handleCommentChange = (posteId, value) => {
    setCommentInputs((prev) => ({ ...prev, [posteId]: value }))
  }

  const handleCommentSubmit = (posteId) => {
    const comment = commentInputs[posteId]
    if (comment?.trim()) {
      console.log("Comment submitted:", { posteId, comment })
      setCommentInputs((prev) => ({ ...prev, [posteId]: "" }))
    }
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
          POSTES PARTAGÉS
        </h1>
        <div></div>
      </div>

      {/* postes Feed */}
      <div className="max-w-2xl mx-auto space-y-6">
        {posts.map((poste) => (
          <div
            key={poste.article.id}
            className="bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-2 rounded-lg shadow-lg"
          >
            {/* Header */}
            <div className="p-6 pb-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-full border-2 border-[#4C3163] overflow-hidden">
                    {poste.user.profilePicture ? (
                      <img
                        src={poste.user.profilePicture.startsWith('http') ?
                          poste.user.profilePicture :
                          `http://localhost:8080${poste.user.profilePicture}`}
                        alt={`${poste.user.nom} ${poste.user.prenom}`}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.target.style.display = 'none';
                          e.target.nextElementSibling.style.display = 'flex';
                        }}
                      />
                    ) : null}
                    <div
                      className="w-full h-full bg-[#4C3163] flex items-center justify-center text-white font-semibold text-sm"
                      style={{ display: poste.user.profilePicture ? 'none' : 'flex' }}
                    >
                      {(poste.user.nom + " " + poste.user.prenom)
                        .split(" ")
                        .map((n) => n[0])
                        .join("")}
                    </div>
                  </div>
                  <div>
                    <h3 className="text-white font-semibold">{poste.user.nom + " " + poste.user.prenom}</h3>
                    <p className="text-[#A09F87] text-sm">
                      @{poste.user.username} • {poste.createdAt}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {user.id !== poste.user.id && (
                    <button
                      onClick={() => handleFollow(poste.user.id, poste.interaction.following)}
                      disabled={followLoading[poste.user.id]} // ✅ Désactiver pendant le chargement
                      className={`px-3 py-1 text-sm rounded transition-colors flex items-center gap-1 min-w-[80px] justify-center ${
                        followLoading[poste.user.id]
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
                </div>
              </div>
            </div>

            {/* Content */}
            <div className="px-6 space-y-4">
              {/* poste Type Icon and Category */}
              <div className="flex items-center gap-2 mb-3">
                {poste.article.type === "SAGESSE" ? (
                  <FaQuoteLeft className="text-[#A09F87] text-lg" />
                ) : (
                  <BiBook className="text-[#A09F87] text-lg" />
                )}
                <span className="text-[#A09F87] text-sm font-medium">{poste.article.category}</span>
              </div>

              {/* poste Content */}
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

              {/* Tags */}
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
                    className={`flex items-center gap-2 transition-colors ${
                      poste.interaction.liked ? "text-red-500 hover:text-red-400" : "text-[#A09F87] hover:text-red-500"
                    }`}
                  >
                    {poste.interaction.liked ? <FaHeart className="w-4 h-4" /> : <Heart className="w-4 h-4" />}
                    {poste.article.likes}
                  </button>
                  <button className="flex items-center gap-2 text-[#A09F87] hover:text-white transition-colors">
                    <MessageCircle className="w-4 h-4" />
                    {poste.article.comments}
                  </button>
                  <button className="flex items-center gap-2 text-[#A09F87] hover:text-white transition-colors">
                    <Share2 className="w-4 h-4" />
                    {poste.article.shares}
                  </button>
                </div>
              </div>

              {/* Comment Input */}
              <div className="flex gap-3 pt-2 pb-6">
                <div className="w-8 h-8 rounded-full overflow-hidden border border-[#4C3163]">
                  {user.profilePicture ? (
                    <img
                      src={user.profilePicture.startsWith('http') ?
                        user.profilePicture :
                        `http://localhost:8080${user.profilePicture}`}
                      alt="Votre avatar"
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.src = profileAvatar;
                      }}
                    />
                  ) : (
                    <img
                      src={profileAvatar}
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
                    className="flex-1 bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
                    onKeyPress={(e) => {
                      if (e.key === "Enter") {
                        handleCommentSubmit(poste.article.id)
                      }
                    }}
                  />
                  <button
                    onClick={() => handleCommentSubmit(poste.article.id)}
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

export default posteFeed