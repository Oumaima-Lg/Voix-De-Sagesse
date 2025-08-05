

import { useState } from "react"
import { useSelector, useDispatch } from "react-redux"
// import { updateUser } from "../../Slices/UserSlice"
import HeaderArticla from "./HeaderArticla"
import ShinyText from "../../blocks/TextAnimations/ShinyText/ShinyText"
import { PiUser, PiUserCircle, PiCamera, PiPencil, PiCheck, PiX } from "react-icons/pi"
import { IoPersonOutline, IoMailOutline, IoCallOutline, IoLocationOutline } from "react-icons/io5"
import { MdOutlineLanguage, MdOutlineNotifications, MdOutlineVisibility } from "react-icons/md"
import profileAvatar from "../../assets/profileAvatar.jpg"



const Profile = () => {
    const user = useSelector((state) => state.user)
    const dispatch = useDispatch()
    const [isEditing, setIsEditing] = useState(false)
    const [editedUser, setEditedUser] = useState({
        nom: user.nom || "",
        prenom: user.prenom || "",
        email: user.email || "",
        username: user.username || "",
        phoneNumber: user.phoneNumber || "",
        location: user.location || "",
        bio: user.bio || "",
        website: user.website || "",
        preferredLanguage: user.preferredLanguage || "fr",
        emailNotifications: user.emailNotifications || true,
        pushNotifications: user.pushNotifications || true,
        profileVisibility: user.profileVisibility || "PUBLIC",
    })

    const handleInputChange = (field, value) => {
        setEditedUser((prev) => ({
            ...prev,
            [field]: value,
        }))
    }

    const handleSave = () => {
        // dispatch(updateUser(editedUser))
        setIsEditing(false)
    }

    const handleCancel = () => {
        setEditedUser({
            nom: user.nom || "",
            prenom: user.prenom || "",
            email: user.email || "",
            username: user.username || "",
            phoneNumber: user.phoneNumber || "",
            location: user.location || "",
            bio: user.bio || "",
            website: user.website || "",
            preferredLanguage: user.preferredLanguage || "fr",
            emailNotifications: user.emailNotifications || true,
            pushNotifications: user.pushNotifications || true,
            profileVisibility: user.profileVisibility || "PUBLIC",
        })
        setIsEditing(false)
    }

    return (
        <div className="pt-26 px-4 lg:px-8">
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
                                        src={user.profilePicture || profileAvatar}
                                        alt="Profile"
                                        className="w-32 h-32 rounded-full border-4 border-[#A09F87] object-cover"
                                    />
                                    <div className="absolute inset-0 bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center cursor-pointer">
                                        <PiCamera className="text-3xl text-[#A09F87]" />
                                    </div>
                                </div>
                                <h2 className="text-2xl font-bold text-[#A09F87] mt-4 charm">
                                    {user.nom} {user.prenom}
                                </h2>
                                <p className="text-gray-300">@{user.username || 'nom_utilisateur'}</p>
                            </div>

                            {/* Statistiques */}
                            <div className="grid grid-cols-2 gap-4 mb-6">
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{user.contentCount || 0}</div>
                                    <div className="text-sm text-gray-300">Articles</div>
                                </div>
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{user.followersCount || 0}</div>
                                    <div className="text-sm text-gray-300">Followers</div>
                                </div>
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{user.followingCount || 0}</div>
                                    <div className="text-sm text-gray-300">Following</div>
                                </div>
                                <div className="text-center p-3 bg-[#202020] rounded-lg">
                                    <div className="text-2xl font-bold text-[#A09F87]">{user.likesReceivedCount || 0}</div>
                                    <div className="text-sm text-gray-300">Likes</div>
                                </div>
                            </div>

                            {/* Bouton d'édition */}
                            <button
                                onClick={() => setIsEditing(!isEditing)}
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
                                            {user.nom || "Non renseigné"}
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
                                            {user.prenom || "Non renseigné"}
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
                                            {user.email || "Non renseigné"}
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
                                            @{user.username || "Non renseigné"}
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
                                            {user.phoneNumber || "Non renseigné"}
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
                                            {user.location || "Non renseigné"}
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
                                        onChange={(e) => handleInputChange("biography", e.target.value)}
                                        rows={4}
                                        className="w-full bg-[#202020] border-2 border-[#4C3163] rounded-lg px-4 py-3 text-white focus:border-[#A09F87] focus:outline-none transition-colors resize-none"
                                        placeholder="Parlez-nous de vous..."
                                    />
                                ) : (
                                    <div className="bg-[#202020] rounded-lg px-4 py-3 border-2 border-transparent min-h-[100px]">
                                        {user.bio || "Aucune biographie renseignée"}
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
                                        {user.website ? (
                                            <a
                                                href={user.website}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-[#A09F87] hover:underline"
                                            >
                                                {user.website}
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
                    </div>
                </div>
            </div>
        </div>

    )
}

export default Profile
