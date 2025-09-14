import { useState, useEffect, useRef } from "react"
import { useSelector, useDispatch } from "react-redux"
import ShinyText from "../../blocks/TextAnimations/ShinyText/ShinyText"
import { PiUser, PiUserCircle, PiCamera, PiPencil, PiCheck, PiX } from "react-icons/pi"
import { IoPersonOutline, IoMailOutline, IoCallOutline, IoLocationOutline } from "react-icons/io5"
import { MdOutlineLanguage, MdOutlineNotifications, MdOutlineVisibility } from "react-icons/md"
import profileAvatar from "../../assets/profileAvatar.jpg"
import { errorNotification, successNotification } from "../../services/NotificationService"
import { updateUser } from "../../Slices/UserSlice"
import { updateUserProfile, getUserProfile, uploadProfilePicture } from '../../services/UserService';
import { LoadingOverlay } from "@mantine/core"
import { useParams } from "react-router-dom";


const Profile = ({userId: propUserId}) => {

    const { id } = useParams();  // récupère l'id de l'URL
    const userId = propUserId || id; // si un prop est passé, sinon depuis l'URL

    const [loading, setLoading] = useState(true);
    const [uploadingImage, setUploadingImage] = useState(false);

    // État pour stocker les données du profil reçues du backend
    const [userProfile, setUserProfile] = useState(null);

    useEffect(() => {
        setLoading(true);
        getUserProfile(userId)
            .then((data) => {
                console.log(data);
                setUserProfile(data);
                setLoading(false);
            })
            .catch((err) => {
                setLoading(false);
                console.error(err);
            });
    }, []);



    // Si les données ne sont pas encore chargées, afficher le loading
    if (!userProfile) {
        return (
            <div className="pt-26 px-4 lg:px-8">
                <LoadingOverlay
                    visible={loading}
                    zIndex={1000}
                    overlayProps={{ radius: 'sm', blur: 2 }}
                    loaderProps={{ color: '#A09F87', type: 'bars' }}
                />
            </div>
        );
    }

    return (
        <div className="pt-26 px-4 lg:px-8">
            <LoadingOverlay
                visible={loading || uploadingImage}
                zIndex={1000}
                overlayProps={{ radius: 'sm', blur: 2 }}
                loaderProps={{ color: '#A09F87', type: 'bars' }}
            />
            {/* Titre principal */}
            <div className="text-center mb-8  text-gradient charm xl:text-5xl text-3xl leading-tight">
                <ShinyText
                    text="PROFIL"
                    disabled={false}
                    speed={4}
                />
            </div>

            {/* profile */}
            <div className="max-w-6xl mx-auto">
                <div className="grid lg:grid-cols-3 gap-8">
                    {/* Section Avatar et Informations principales */}
                    <div className="lg:col-span-1">
                        <div className="border-3 border-[#202020] bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-lg shadow-lg p-6 text-white">
                            {/* Avatar */}
                            <div className="flex flex-col items-center mb-6">
                                <div className="relative group">
                                    <img
                                        src={userProfile.profilePicture ?
                                            (userProfile.profilePicture.startsWith('http') ?
                                                userProfile.profilePicture :
                                                `http://localhost:8080${userProfile.profilePicture}` // Le chemin commence déjà par /uploads
                                            ) : profileAvatar}
                                        alt="Profile"
                                        className="w-32 h-32 rounded-full border-4 border-[#A09F87] object-cover"
                                        onError={(e) => {
                                            console.error('Erreur de chargement de l\'image:', e.target.src);
                                            e.target.src = profileAvatar;
                                        }}
                                    />
                                </div>
                                <h2 className="text-2xl font-bold text-[#A09F87] mt-4 charm">
                                    {userProfile.nom} {userProfile.prenom}
                                </h2>
                                <p className="text-gray-300">@{userProfile.username || 'nom_utilisateur'}</p>
                            </div>

                            {/* Statistiques */}
                            <div className="grid grid-cols-2 gap-4 mb-6">
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{userProfile.contentCount || 0}</div>
                                    <div className="text-sm text-gray-300">Articles</div>
                                </div>
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{userProfile.followersCount || 0}</div>
                                    <div className="text-sm text-gray-300">Followers</div>
                                </div>
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{userProfile.followingCount || 0}</div>
                                    <div className="text-sm text-gray-300">Following</div>
                                </div>
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{userProfile.likesReceived || 0}</div>
                                    <div className="text-sm text-gray-300">Likes</div>
                                </div>
                            </div>

                        </div>
                    </div>

                    {/* Section Informations détaillées */}
                    <div className="lg:col-span-2">
                        <div className="border-3 border-[#202020] bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-lg shadow-lg p-6 text-white">
                            <div className="flex items-center justify-between mb-6">
                                <h3 className="text-2xl font-bold text-[#A09F87] charm">Informations personnelles</h3>
                            </div>

                            <div className="grid md:grid-cols-2 gap-6">
                                {/* Nom */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoPersonOutline className="text-xl" />
                                        Nom
                                    </label>
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.nom || "Non renseigné"}
                                        </div>
                                    
                                </div>

                                {/* Prénom */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoPersonOutline className="text-xl" />
                                        Prénom
                                    </label>
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.prenom || "Non renseigné"}
                                        </div>
                                </div>

                                {/* Email */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoMailOutline className="text-xl" />
                                        Email
                                    </label>
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.email || "Non renseigné"}
                                        </div>
                                </div>

                                {/* Nom d'utilisateur */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <PiUserCircle className="text-xl" />
                                        Nom d'utilisateur
                                    </label>
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            @{userProfile.username || "Non renseigné"}
                                        </div>
                                </div>

                                {/* Téléphone */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoCallOutline className="text-xl" />
                                        Téléphone
                                    </label>
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.phoneNumber || "Non renseigné"}
                                        </div>
                                </div>

                                {/* Localisation */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoLocationOutline className="text-xl" />
                                        Localisation
                                    </label>
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.location || "Non renseigné"}
                                        </div>
                                </div>
                            </div>

                            {/* Biographie */}
                            <div className="mt-6 space-y-2">
                                <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                    <PiUser className="text-xl" />
                                    Biographie
                                </label>
                                    <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent min-h-[100px]">
                                        {userProfile.bio || "Aucune biographie renseignée"}
                                    </div>
                            </div>

                            {/* Site web */}
                            <div className="mt-6 space-y-2">
                                <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                    <MdOutlineLanguage className="text-xl" />
                                    Site web
                                </label>
                                    <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                        {userProfile.website ? (
                                            <a
                                                href={userProfile.website}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-[#A09F87] hover:underline"
                                            >
                                                {userProfile.website}
                                            </a>
                                        ) : (
                                            "Non renseigné"
                                        )}
                                    </div>
                            </div>
                        </div>

                        
                    </div>
                </div>
            </div>
        </div>

    )
}

export default Profile