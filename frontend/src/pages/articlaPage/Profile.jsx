import { useState, useEffect, useRef } from "react"
import { useSelector, useDispatch } from "react-redux"
import ShinyText from "../../blocks/TextAnimations/ShinyText/ShinyText"
import { PiUser, PiUserCircle, PiCamera, PiPencil, PiCheck, PiX, PiWarning, PiTrash } from "react-icons/pi"
import { IoPersonOutline, IoMailOutline, IoCallOutline, IoLocationOutline } from "react-icons/io5"
import { MdOutlineLanguage, MdOutlineNotifications, MdOutlineVisibility } from "react-icons/md"
import profileAvatar from "../../assets/profileAvatar.jpg"
import { errorNotification, successNotification } from "../../services/NotificationService"
import { updateUser } from "../../Slices/UserSlice"
import { updateUserProfile, getUserProfile, uploadProfilePicture, deleteAccount } from '../../services/UserService';
import { LoadingOverlay } from "@mantine/core"
import { Modal } from "@mantine/core";
import { useAuth } from "../../hooks/useAuth"



const Profile = () => {

    const [loading, setLoading] = useState(true);
    const [uploadingImage, setUploadingImage] = useState(false);
    const user = useSelector((state) => state.user)
    const dispatch = useDispatch()
    const { logout } = useAuth();
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [deleteConfirmation, setDeleteConfirmation] = useState("");
    const [isDeleting, setIsDeleting] = useState(false);
    const [userProfile, setUserProfile] = useState(null);

    useEffect(() => {
        setLoading(true);
        getUserProfile(user.id)
            .then((data) => {
                console.log(data);
                setUserProfile(data);
                setEditedUser({
                    id: data.id || "",
                    nom: data.nom || "",
                    prenom: data.prenom || "",
                    email: data.email || "",
                    username: data.username || "",
                    phoneNumber: data.phoneNumber || "",
                    location: data.location || "",
                    bio: data.bio || "",
                    website: data.website || "",
                    preferredLanguage: user.preferredLanguage || "fr",
                    emailNotifications: user.emailNotifications || true,
                    pushNotifications: user.pushNotifications || true,
                    profileVisibility: user.profileVisibility || "PUBLIC",
                });
                setLoading(false);
            })
            .catch((err) => {
                setLoading(false);
                console.error(err);
            });
    }, [user.id, user.preferredLanguage, user.emailNotifications, user.pushNotifications, user.profileVisibility]);

    const [isEditing, setIsEditing] = useState(false)
    const [editedUser, setEditedUser] = useState({
        id: "",
        nom: "",
        prenom: "",
        email: "",
        username: "",
        phoneNumber: "",
        location: "",
        bio: "",
        website: "",
        preferredLanguage: "fr",
        emailNotifications: true,
        pushNotifications: true,
        profileVisibility: "PUBLIC",
    })

    const handleDeleteAccount = async () => {
        if (deleteConfirmation !== "SUPPRIMER") {
            errorNotification('Erreur', 'Veuillez taper SUPPRIMER pour confirmer');
            return;
        }

        try {
            setIsDeleting(true);
            const response = await deleteAccount();

            if (response.success) {
                successNotification('Compte supprimé', 'Votre compte a été supprimé avec succès');


                setTimeout(() => {
                    logout('Votre compte a été supprimé avec succès');
                }, 1500);
            }
        } catch (error) {
            console.error('Erreur lors de la suppression:', error);
            errorNotification('Erreur', 'Une erreur est survenue lors de la suppression du compte');
            setIsDeleting(false);
        }
    };

    const handleInputChange = (field, value) => {
        setEditedUser((prev) => ({
            ...prev,
            [field]: value,
        }))
    }

    const handleSave = () => {
        updateUserProfile(editedUser).then((res) => {
            console.log(res);
            dispatch(updateUser(res));

            setUserProfile(prev => ({
                ...prev,
                ...res
            }));
            successNotification('Success', 'Profile updated successfully!');
        }).catch((err) => {
            console.log(err);
            errorNotification('Error', err.response.data.errorMessage);
        })
        setIsEditing(false)
    }

    const handleEditToggle = () => {
        if (isEditing) {
            handleCancel()
        } else {
            if (userProfile) {
                setEditedUser((prev) => ({
                    ...prev,
                    id: userProfile.id || "",
                    nom: userProfile.nom || "",
                    prenom: userProfile.prenom || "",
                    email: userProfile.email || "",
                    username: userProfile.username || "",
                    phoneNumber: userProfile.phoneNumber || "",
                    location: userProfile.location || "",
                    bio: userProfile.bio || "",
                    website: userProfile.website || "",
                    preferredLanguage: user.preferredLanguage || "fr",
                    emailNotifications: user.emailNotifications ?? true,
                    pushNotifications: user.pushNotifications ?? true,
                    profileVisibility: user.profileVisibility || "PUBLIC",
                }))
            }
            setIsEditing(true)
        }
    }

    const handleCancel = () => {
        if (userProfile) {
            setEditedUser({
                id: userProfile.id || "",
                nom: userProfile.nom || "",
                prenom: userProfile.prenom || "",
                email: userProfile.email || "",
                username: userProfile.username || "",
                phoneNumber: userProfile.phoneNumber || "",
                location: userProfile.location || "",
                bio: userProfile.bio || "",
                website: userProfile.website || "",
                preferredLanguage: user.preferredLanguage || "fr",
                emailNotifications: user.emailNotifications || true,
                pushNotifications: user.pushNotifications || true,
                profileVisibility: user.profileVisibility || "PUBLIC",
            })
        }
        setIsEditing(false)
    }

    const handleImageUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            errorNotification('Erreur', 'Veuillez sélectionner un fichier image valide.');
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            errorNotification('Erreur', 'La taille de l\'image ne doit pas dépasser 5MB.');
            return;
        }

        try {
            setUploadingImage(true);

            const formData = new FormData();
            formData.append('profilePicture', file);
            formData.append('userId', user.id);

            const response = await uploadProfilePicture(formData);

            console.log('Response from backend:', response);
            console.log('Profile picture URL:', response.profilePictureUrl);

            setUserProfile(prev => ({
                ...prev,
                profilePicture: response.profilePictureUrl
            }));

            dispatch(updateUser({
                ...user,
                profilePicture: response.profilePictureUrl
            }));

            successNotification('Succès', 'Photo de profil mise à jour avec succès!');
        } catch (error) {
            console.error('Erreur lors de l\'upload:', error);
            errorNotification('Erreur', 'Échec du téléchargement de l\'image. Veuillez réessayer.');
        } finally {
            setUploadingImage(false);
        }
    }

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
                    text="VOTRE PROFIL"
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
                                                `http://localhost:8080${userProfile.profilePicture}`
                                            ) : profileAvatar}
                                        alt="Profile"
                                        className="w-32 h-32 rounded-full border-4 border-[#A09F87] object-cover"
                                        onError={(e) => {
                                            console.error('Erreur de chargement de l\'image:', e.target.src);
                                            e.target.src = profileAvatar;
                                        }}
                                    />
                                    <div
                                        className="absolute inset-0 bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center cursor-pointer"
                                        onClick={() => document.getElementById('profile-image-upload').click()}
                                    >
                                        <PiCamera className="text-3xl text-[#A09F87]" />
                                    </div>
                                    <input
                                        id="profile-image-upload"
                                        type="file"
                                        accept="image/*"
                                        onChange={handleImageUpload}
                                        className="hidden"
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

                            {/* Bouton d'édition */}
                            <button
                                onClick={handleEditToggle}
                                className="w-full flex items-center justify-center gap-2 bg-gradient-to-r from-[#A09F87] to-[#4C3163] text-white py-3 px-4 rounded-lg hover:scale-105 transform-gpu transition-all duration-300 font-medium"
                            >
                                {isEditing ? (
                                    <>
                                        <PiX className="text-xl" />
                                        Annuler
                                    </>
                                ) : (
                                    <>
                                        <PiPencil className="text-xl" />
                                        Modifier le profil
                                    </>
                                )}
                            </button>
                        </div>
                    </div>

                    {/* Section Informations détaillées */}
                    <div className="lg:col-span-2">
                        <div className="border-3 border-[#202020] bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-lg shadow-lg p-6 text-white">
                            <div className="flex items-center justify-between mb-6">
                                <h3 className="text-2xl font-bold text-[#A09F87] charm">Informations personnelles</h3>
                                {isEditing && (
                                    <button
                                        onClick={handleSave}
                                        className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded-lg hover:scale-105 transform-gpu transition-all duration-300"
                                    >
                                        <PiCheck className="text-xl" />
                                        Sauvegarder
                                    </button>
                                )}
                            </div>

                            <div className="grid md:grid-cols-2 gap-6">
                                {/* Nom */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoPersonOutline className="text-xl" />
                                        Nom
                                    </label>
                                    {isEditing ? (
                                        <input
                                            type="text"
                                            value={editedUser.nom}
                                            onChange={(e) => handleInputChange("nom", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        />
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.nom || "Non renseigné"}
                                        </div>
                                    )}
                                </div>

                                {/* Prénom */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoPersonOutline className="text-xl" />
                                        Prénom
                                    </label>
                                    {isEditing ? (
                                        <input
                                            type="text"
                                            value={editedUser.prenom}
                                            onChange={(e) => handleInputChange("prenom", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        />
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.prenom || "Non renseigné"}
                                        </div>
                                    )}
                                </div>

                                {/* Email */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoMailOutline className="text-xl" />
                                        Email
                                    </label>
                                    {isEditing ? (
                                        <input
                                            type="email"
                                            value={editedUser.email}
                                            onChange={(e) => handleInputChange("email", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        />
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.email || "Non renseigné"}
                                        </div>
                                    )}
                                </div>

                                {/* Nom d'utilisateur */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <PiUserCircle className="text-xl" />
                                        Nom d'utilisateur
                                    </label>
                                    {isEditing ? (
                                        <input
                                            type="text"
                                            value={editedUser.username}
                                            onChange={(e) => handleInputChange("username", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        />
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            @{userProfile.username || "Non renseigné"}
                                        </div>
                                    )}
                                </div>

                                {/* Téléphone */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoCallOutline className="text-xl" />
                                        Téléphone
                                    </label>
                                    {isEditing ? (
                                        <input
                                            type="tel"
                                            value={editedUser.phoneNumber}
                                            onChange={(e) => handleInputChange("phoneNumber", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        />
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.phoneNumber || "Non renseigné"}
                                        </div>
                                    )}
                                </div>

                                {/* Localisation */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <IoLocationOutline className="text-xl" />
                                        Localisation
                                    </label>
                                    {isEditing ? (
                                        <input
                                            type="text"
                                            value={editedUser.location}
                                            onChange={(e) => handleInputChange("location", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        />
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {userProfile.location || "Non renseigné"}
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Biographie */}
                            <div className="mt-6 space-y-2">
                                <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                    <PiUser className="text-xl" />
                                    Biographie
                                </label>
                                {isEditing ? (
                                    <textarea
                                        value={editedUser.bio}
                                        onChange={(e) => handleInputChange("bio", e.target.value)}
                                        rows={4}
                                        className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors resize-none"
                                        placeholder="Parlez-nous de vous..."
                                    />
                                ) : (
                                    <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent min-h-[100px]">
                                        {userProfile.bio || "Aucune biographie renseignée"}
                                    </div>
                                )}
                            </div>

                            {/* Site web */}
                            <div className="mt-6 space-y-2">
                                <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                    <MdOutlineLanguage className="text-xl" />
                                    Site web
                                </label>
                                {isEditing ? (
                                    <input
                                        type="url"
                                        value={editedUser.website}
                                        onChange={(e) => handleInputChange("website", e.target.value)}
                                        className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        placeholder="https://votre-site.com"
                                    />
                                ) : (
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
                                )}
                            </div>
                        </div>

                        {/* Section Préférences */}
                        <div className="border-3 border-[#202020] bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-lg shadow-lg p-6 text-white mt-8">
                            <h3 className="text-2xl font-bold text-[#A09F87] charm mb-6">Préférences</h3>

                            <div className="grid md:grid-cols-2 gap-6">
                                {/* Langue préférée */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <MdOutlineLanguage className="text-xl" />
                                        Langue préférée
                                    </label>
                                    {isEditing ? (
                                        <select
                                            value={editedUser.preferredLanguage}
                                            onChange={(e) => handleInputChange("preferredLanguage", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        >
                                            <option value="fr">Français</option>
                                            <option value="en">English</option>
                                            <option value="ar">العربية</option>
                                        </select>
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {user.preferredLanguage === "fr"
                                                ? "Français"
                                                : user.preferredLanguage === "en"
                                                    ? "English"
                                                    : user.preferredLanguage === "ar"
                                                        ? "العربية"
                                                        : "Français"}
                                        </div>
                                    )}
                                </div>

                                {/* Visibilité du profil */}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 text-[#A09F87] font-medium">
                                        <MdOutlineVisibility className="text-xl" />
                                        Visibilité du profil
                                    </label>
                                    {isEditing ? (
                                        <select
                                            value={editedUser.profileVisibility}
                                            onChange={(e) => handleInputChange("profileVisibility", e.target.value)}
                                            className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors"
                                        >
                                            <option value="PUBLIC">Public</option>
                                            <option value="PRIVATE">Privé</option>
                                            <option value="FRIENDS_ONLY">Amis seulement</option>
                                        </select>
                                    ) : (
                                        <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent">
                                            {user.profileVisibility === "PUBLIC"
                                                ? "Public"
                                                : user.profileVisibility === "PRIVATE"
                                                    ? "Privé"
                                                    : user.profileVisibility === "FRIENDS_ONLY"
                                                        ? "Amis seulement"
                                                        : "Public"}
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Notifications */}
                            <div className="mt-6 space-y-4">
                                <h4 className="text-lg font-semibold text-[#A09F87]">Notifications</h4>

                                <div className="flex items-center justify-between p-4 bg-[#202020] rounded-lg">
                                    <div className="flex items-center gap-3">
                                        <MdOutlineNotifications className="text-xl text-[#A09F87]" />
                                        <span>Notifications par email</span>
                                    </div>
                                    {isEditing ? (
                                        <label className="relative inline-flex items-center cursor-pointer">
                                            <input
                                                type="checkbox"
                                                checked={editedUser.emailNotifications}
                                                onChange={(e) => handleInputChange("emailNotifications", e.target.checked)}
                                                className="sr-only peer"
                                            />
                                            <div className="w-11 h-6 bg-gray-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#A09F87]"></div>
                                        </label>
                                    ) : (
                                        <div
                                            className={`w-11 h-6 rounded-full ${user.emailNotifications ? "bg-[#A09F87]" : "bg-gray-600"} relative`}
                                        >
                                            <div
                                                className={`absolute top-[2px] left-[2px] bg-white rounded-full h-5 w-5 transition-transform ${user.emailNotifications ? "translate-x-full" : ""}`}
                                            ></div>
                                        </div>
                                    )}
                                </div>

                                <div className="flex items-center justify-between p-4 bg-[#202020] rounded-lg">
                                    <div className="flex items-center gap-3">
                                        <MdOutlineNotifications className="text-xl text-[#A09F87]" />
                                        <span>Notifications push</span>
                                    </div>
                                    {isEditing ? (
                                        <label className="relative inline-flex items-center cursor-pointer">
                                            <input
                                                type="checkbox"
                                                checked={editedUser.pushNotifications}
                                                onChange={(e) => handleInputChange("pushNotifications", e.target.checked)}
                                                className="sr-only peer"
                                            />
                                            <div className="w-11 h-6 bg-gray-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#A09F87]"></div>
                                        </label>
                                    ) : (
                                        <div
                                            className={`w-11 h-6 rounded-full ${user.pushNotifications ? "bg-[#A09F87]" : "bg-gray-600"} relative`}
                                        >
                                            <div
                                                className={`absolute top-[2px] left-[2px] bg-white rounded-full h-5 w-5 transition-transform ${user.pushNotifications ? "translate-x-full" : ""}`}
                                            ></div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="border-3 border-red-900 bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-lg shadow-lg p-6 text-white mt-8">
                            <h3 className="text-2xl font-bold text-red-500 charm mb-6 flex items-center gap-2">
                                <PiWarning className="text-3xl" />
                                Zone Dangereuse
                            </h3>

                            <div className="bg-red-950/30 border-2 border-red-800 rounded-lg p-6">
                                <h4 className="text-lg font-semibold text-red-400 mb-3">
                                    Supprimer définitivement votre compte
                                </h4>
                                <p className="text-gray-300 mb-4">
                                    Une fois votre compte supprimé, toutes vos données seront définitivement effacées.
                                    Cette action est <span className="font-bold text-red-400">irréversible</span>.
                                </p>
                                <p className="text-sm text-gray-400 mb-4">
                                    Cela inclut : vos articles, commentaires, likes, followers, photos et toutes vos données personnelles.
                                </p>
                                <button
                                    onClick={() => setShowDeleteModal(true)}
                                    className="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white py-3 px-6 rounded-lg hover:scale-105 transform-gpu transition-all duration-300 font-medium"
                                >
                                    <PiTrash className="text-xl" />
                                    Supprimer mon compte
                                </button>
                            </div>
                        </div>

                        {/* Modal de confirmation de suppression */}
                        <Modal
                            opened={showDeleteModal}
                            onClose={() => {
                                setShowDeleteModal(false);
                                setDeleteConfirmation("");
                            }}
                            title={
                                <div className="flex items-center gap-2 text-red-500">
                                    <PiWarning className="text-2xl" />
                                    <span className="text-xl font-bold">Confirmation de suppression</span>
                                </div>
                            }
                            centered
                            size="md"
                            styles={{
                                body: { backgroundColor: '#171717' },
                                header: { backgroundColor: '#171717' },
                                modal: { backgroundColor: '#171717' },
                                title: { color: '#ef4444' }
                            }}
                        >
                            <div className="text-white">
                                <div className="bg-red-950/30 border-2 border-red-800 rounded-lg p-4 mb-4">
                                    <p className="text-red-400 font-semibold mb-2">
                                        ⚠️ Attention ! Cette action est irréversible
                                    </p>
                                    <p className="text-gray-300 text-sm">
                                        La suppression de votre compte entraînera la perte définitive de :
                                    </p>
                                    <ul className="list-disc list-inside text-gray-400 text-sm mt-2 space-y-1">
                                        <li>Tous vos articles publiés</li>
                                        <li>Tous vos commentaires</li>
                                        <li>Votre photo de profil</li>
                                        <li>Vos followers et following</li>
                                        <li>Vos articles sauvegardés</li>
                                        <li>Toutes vos données personnelles</li>
                                    </ul>
                                </div>

                                <div className="mb-4">
                                    <p className="text-gray-300 mb-2">
                                        Pour confirmer la suppression, tapez <span className="font-bold text-red-400">SUPPRIMER</span> ci-dessous :
                                    </p>
                                    <input
                                        type="text"
                                        value={deleteConfirmation}
                                        onChange={(e) => setDeleteConfirmation(e.target.value)}
                                        placeholder="Tapez SUPPRIMER"
                                        className="w-full bg-[#202020] border-2 border-red-800 rounded-lg px-4 py-3 text-white focus:border-red-600 focus:outline-none transition-colors"
                                    />
                                </div>

                                <div className="flex gap-3 justify-end">
                                    <button
                                        onClick={() => {
                                            setShowDeleteModal(false);
                                            setDeleteConfirmation("");
                                        }}
                                        disabled={isDeleting}
                                        className="px-6 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-lg transition-colors disabled:opacity-50"
                                    >
                                        Annuler
                                    </button>
                                    <button
                                        onClick={handleDeleteAccount}
                                        disabled={deleteConfirmation !== "SUPPRIMER" || isDeleting}
                                        className="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                                    >
                                        {isDeleting ? (
                                            <>
                                                <span className="animate-spin">⏳</span>
                                                Suppression...
                                            </>
                                        ) : (
                                            <>
                                                <PiTrash />
                                                Supprimer définitivement
                                            </>
                                        )}
                                    </button>
                                </div>
                            </div>
                        </Modal>
                    </div>
                </div>
            </div>
        </div>

    )
}

export default Profile