import { useState, useEffect } from "react"
import { useSelector } from "react-redux"
import { useAuth } from "../../services/useAuth"
import { getAllUsers, getDashboardStats } from "../../services/AdminService"
import profileAvatar from "../../assets/profileAvatar.jpg"
import Signals from "./Signals"
import {
    Users,
    FileText,
    Search,
    UserPlus,
    Eye,
    Edit,
    Trash2,
    Mail,
    Calendar,
    BookOpen,
    Brain,
    Lightbulb,
    GraduationCap,
    Scroll,
    Glasses,
    Feather,
    Atom,
    AlertTriangle,
    BarChart3,
} from "lucide-react"

import Spline from '@splinetool/react-spline';

export function AppSpline() {
    return (
        <Spline scene="https://prod.spline.design/9vqrsAo-EuX1s3i7/scene.splinecode" />
    );
}


const AdminDashboard = () => {
    const user = useSelector((state) => state.user)
    const { logout } = useAuth()
    const [searchTerm, setSearchTerm] = useState("")
    const [filterStatus, setFilterStatus] = useState("tous")
    const [currentPage, setCurrentPage] = useState(1)
    const [usersPerPage] = useState(5)
    const [mounted, setMounted] = useState(false)
    const [isLoading, setIsLoading] = useState(true)
    const [showSpline, setShowSpline] = useState(true)
    const [splineProgress, setSplineProgress] = useState(0)
    const [activeTab, setActiveTab] = useState("dashboard")
    const [dashboardStats, setDashboardStats] = useState({
        totalUsers: 0,
        totalArticles: 0,
        newUsersToday: 0,
        newArticlesToday: 0,
        totalSignals: 0,
        pendingSignals: 0,
    })

    // État pour les utilisateurs, initialisé comme tableau vide
    const [users, setUsers] = useState([])

    // Statistiques dynamiques basées sur les données de l'API
    const stats = [
        {
            title: "Utilisateurs Total",
            value: dashboardStats.totalUsers.toLocaleString(),
            change: "+12%",
            trend: "up",
            icon: Users,
            color: "from-blue-500 via-purple-500 to-pink-500",
        },
        {
            title: "Articles Publiés",
            value: dashboardStats.totalArticles.toLocaleString(),
            change: "+8%",
            trend: "up",
            icon: FileText,
            color: "from-green-500 to-emerald-500",
        },
        {
            title: "Signalements Total",
            value: dashboardStats.totalSignals,
            change: "+15%",
            trend: "up",
            icon: AlertTriangle,
            color: "from-orange-500 to-red-500",
        },
        {
            title: "Signalements en Attente",
            value: dashboardStats.pendingSignals,
            change: "+5%",
            trend: "up",
            icon: BarChart3,
            color: "from-purple-500 to-indigo-500",
        },
    ]

    // Fonction pour charger les statistiques du tableau de bord
    const loadDashboardStats = async () => {
        try {
            const data = await getDashboardStats()
            setDashboardStats({
                ...data,
                totalSignals: 47, // Exemple de données
                pendingSignals: 12, // Exemple de données
            })
        } catch (error) {
            console.error("Erreur lors du chargement des statistiques:", error)
        }
    }

    // Fonction pour charger la liste des utilisateurs
    const loadUsers = async () => {
        try {
            const data = await getAllUsers()
            console.log(
                "Données des utilisateurs reçues avec profilePicture:",
                data.map((u) => ({ id: u.id, profilePicture: u.profilePicture })),
            )
            // Transformation des données utilisateurs reçues pour correspondre à la structure attendue
            const formattedUsers = data.map((user) => ({
                id: user.id,
                nom: user.nom,
                prenom: user.prenom,
                email: user.email,
                role:
                    user.accountType === "ADMIN"
                        ? "Administrateur"
                        : user.accountType === "MODERATOR"
                            ? "Modérateur"
                            : "Utilisateur",
                status: "Actif", // Par défaut, on considère tous les utilisateurs comme actifs
                dateInscription: user.dateInscription, // Date d'inscription (à ajuster si disponible dans l'API)
                profilePicture: user.profilePicture || null,
            }))
            setUsers(formattedUsers)
        } catch (error) {
            console.error("Erreur lors du chargement des utilisateurs:", error)
        } finally {
            setIsLoading(false)
        }
    }

    useEffect(() => {
        setMounted(true)
        // Chargement des données au montage du composant
        loadDashboardStats()
        loadUsers()

        const duration = 8000
        const start = Date.now()
        setSplineProgress(0)
        const interval = setInterval(() => {
            const elapsed = Date.now() - start
            const pct = Math.min(100, Math.round((elapsed / duration) * 100))
            setSplineProgress(pct)
            if (elapsed >= duration) {
                clearInterval(interval)
                setShowSpline(false)
            }
        }, 100)
        return () => clearInterval(interval)
    }, [])


    const handleLogout = () => {
        logout("Vous avez été déconnecté avec succès")
    }

    const filteredUsers = users.filter((user) => {
        const matchesSearch =
            user.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.prenom.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.email.toLowerCase().includes(searchTerm.toLowerCase())
        const matchesFilter = filterStatus === "tous" || user.status.toLowerCase() === filterStatus.toLowerCase()
        return matchesSearch && matchesFilter
    })

    const getStatusColor = (status) => {
        switch (status.toLowerCase()) {
            case "actif":
                return "bg-green-100 text-green-800 border-green-200"
            case "inactif":
                return "bg-red-100 text-red-800 border-red-200"
            default:
                return "bg-gray-100 text-gray-800 border-gray-200"
        }
    }

    const getRoleColor = (role) => {
        switch (role.toLowerCase()) {
            case "administrateur":
                return "bg-purple-100 text-purple-800 border-purple-200"
            case "modérateur":
                return "bg-blue-100 text-blue-800 border-blue-200"
            case "utilisateur":
                return "bg-gray-100 text-gray-800 border-gray-200"
            default:
                return "bg-gray-100 text-gray-800 border-gray-200"
        }
    }

    const indexOfLastUser = currentPage * usersPerPage
    const indexOfFirstUser = indexOfLastUser - usersPerPage
    const currentUsers = filteredUsers.slice(indexOfFirstUser, indexOfLastUser)
    const totalPages = Math.ceil(filteredUsers.length / usersPerPage)

    const paginate = (pageNumber) => setCurrentPage(pageNumber)

    // Navigation tabs
    const tabs = [
        { id: "dashboard", label: "Tableau de Bord", icon: BarChart3 },
        { id: "signals", label: "Signalements", icon: AlertTriangle },
    ]

    if (showSpline) {
        return (
            <div className="w-screen h-screen bg-gradient-to-bl from-[#0f0f0f] to-[#121212] flex">
                {/* Left */}
                <div className="w-1/2 h-full flex items-center justify-center">
                    <div className="w-full h-full">
                        <AppSpline />
                    </div>
                </div>

                {/* Right: big rotating circle with centered progress counter */}
                <div className="w-1/2 h-full flex items-center justify-center">
                    <div className="flex items-center justify-center w-full h-full">
                        <div className="w-96 h-96 flex items-center justify-center">
                            <div className="relative w-80 h-80">
                                {/* ring that rotates */}
                                <div className="absolute inset-0 rounded-full border-8 border-[#4C3163] border-t-transparent animate-spin shadow-xl"></div>

                                {/* fixed centered content (does NOT rotate) */}
                                <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                                    <div className="text-4xl font-bold text-white select-none">{splineProgress}%</div>
                                    <div className="text-xs text-[#A09F87] mt-1 select-none">Initialisation</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    // Afficher un loader pendant le chargement des données
    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center relative overflow-hidden bg-gray-900">
                <div className="w-16 h-16 border-4 border-dashed rounded-full animate-spin border-blue-500"></div>
            </div>
        )
    }

    return (
        <div className="min-h-screen relative overflow-hidden">
            <div className="fixed inset-0 -z-10">
                <div className="absolute inset-0 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%"></div>

                {/* Floating books */}
                <div className="absolute top-20 left-10 w-16 h-20 opacity-20 animate-float-slow">
                    <BookOpen className="w-full h-full text-[#A09F87]" />
                </div>
                <div className="absolute top-40 right-20 w-20 h-24 opacity-15 animate-float-delayed">
                    <BookOpen className="w-full h-full text-[#A09F87]/80" />
                </div>
                <div className="absolute bottom-20 left-1/3 w-18 h-22 opacity-25 animate-float-reverse">
                    <BookOpen className="w-full h-full text-[#A09F87]/70" />
                </div>

                {/* Floating brains */}
                <div className="absolute top-1/2 right-10 w-14 h-14 opacity-30 animate-pulse-slow">
                    <Brain className="w-full h-full text-[#4C3163]" />
                </div>
                <div className="absolute bottom-40 left-1/2 w-16 h-16 opacity-25 animate-bounce-gentle">
                    <Brain className="w-full h-full text-[#4C3163]/80" />
                </div>

                {/* Floating lightbulbs */}
                <div className="absolute top-32 right-1/3 w-12 h-12 opacity-35 animate-float-particle">
                    <Lightbulb className="w-full h-full text-[#A09F87]" />
                </div>
                <div className="absolute top-1/3 left-20 w-10 h-10 opacity-40 animate-drift-right">
                    <Lightbulb className="w-full h-full text-[#A09F87]/90" />
                </div>
                <div className="absolute bottom-60 right-1/4 w-14 h-14 opacity-30 animate-bounce-slow">
                    <Lightbulb className="w-full h-full text-[#A09F87]/70" />
                </div>

                {/* Graduation caps */}
                <div className="absolute top-16 right-1/2 w-12 h-12 opacity-25 animate-rotate-slow">
                    <GraduationCap className="w-full h-full text-[#4C3163]/80" />
                </div>
                <div className="absolute bottom-32 left-1/5 w-16 h-16 opacity-20 animate-float-shape">
                    <GraduationCap className="w-full h-full text-[#4C3163]/70" />
                </div>

                {/* Scrolls */}
                <div className="absolute top-2/3 right-16 w-10 h-10 opacity-30 animate-pulse-shape">
                    <Scroll className="w-full h-full text-[#A09F87]/80" />
                </div>
                <div className="absolute top-1/4 left-3/4 w-12 h-12 opacity-25 animate-orbit-slow">
                    <Scroll className="w-full h-full text-[#A09F87]/70" />
                </div>

                {/* Glasses */}
                <div className="absolute bottom-1/4 left-1/6 w-14 h-14 opacity-20 animate-orbit-reverse">
                    <Glasses className="w-full h-full text-[#4C3163]/60" />
                </div>

                {/* Feathers */}
                <div className="absolute top-3/4 right-1/5 w-8 h-8 opacity-35 animate-drift-left">
                    <Feather className="w-full h-full text-[#A09F87]/60" />
                </div>

                {/* Atoms */}
                <div className="absolute top-10 left-1/4 w-6 h-6 opacity-40 animate-orbit-fast">
                    <Atom className="w-full h-full text-[#4C3163]/70" />
                </div>

                <div className="absolute top-20 left-10 w-72 h-72 bg-gradient-to-r from-[#A09F87]/10 to-[#4C3163]/10 rounded-full blur-3xl animate-float-slow"></div>
                <div className="absolute top-40 right-20 w-96 h-96 bg-gradient-to-r from-[#4C3163]/8 to-[#A09F87]/8 rounded-full blur-3xl animate-float-delayed"></div>
                <div className="absolute bottom-20 left-1/3 w-80 h-80 bg-gradient-to-r from-[#A09F87]/12 to-[#4C3163]/12 rounded-full blur-3xl animate-float-reverse"></div>
                <div className="absolute top-1/2 right-10 w-64 h-64 bg-gradient-to-r from-[#4C3163]/15 to-[#A09F87]/15 rounded-full blur-3xl animate-pulse-slow"></div>

                <div className="absolute top-24 left-1/3 w-32 h-0.5 bg-gradient-to-r from-transparent via-[#A09F87]/30 to-transparent animate-slide-horizontal"></div>
                <div className="absolute bottom-48 right-1/3 w-24 h-0.5 bg-gradient-to-r from-transparent via-[#4C3163]/25 to-transparent animate-slide-horizontal-reverse"></div>

                {/* Subtle grid pattern */}
                <div className="absolute inset-0 opacity-[0.03]">
                    <div className="absolute inset-0 bg-[linear-gradient(90deg,#fff_1px,transparent_1px),linear-gradient(180deg,#fff_1px,transparent_1px)] bg-[size:50px_50px] animate-grid-move"></div>
                </div>
            </div>

            <header className="relative backdrop-blur-xl bg-[#171717]/80 shadow-lg border-b border-[#202020]/50 top-0 ">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            <h1 className="text-2xl font-bold bg-gradient-to-r from-white via-[#A09F87] to-[#4C3163] bg-clip-text text-transparent animate-gradient-x">
                                Dashboard Administrateur
                            </h1>
                        </div>
                        <div className="flex items-center space-x-4">
                            <div className="flex items-center space-x-2">
                                <div className="w-10 h-10 bg-gradient-to-r from-[#4C3163] via-[#4C3163] to-[#4C3163] rounded-full flex items-center justify-center shadow-lg animate-pulse-gentle ring-2 ring-[#202020]/50">
                                    <span className="text-white text-sm font-medium">
                                        {user?.prenom?.[0]}
                                        {user?.nom?.[0]}
                                    </span>
                                </div>
                                <span className="text-sm text-[#A09F87] font-medium">
                                    Bienvenue, {user?.prenom} {user?.nom}
                                </span>
                            </div>
                            <button
                                onClick={handleLogout}
                                className="px-6 py-2.5 text-sm font-medium text-white/80 bg-gradient-to-r from-red-900 to-red-700 hover:from-red-800 hover:to-red-700 rounded-xl transition-all duration-300 transform hover:scale-105 hover:shadow-lg active:scale-95"
                            >
                                Déconnexion
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            {/* Navigation Tabs */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6">
                <div className="flex space-x-1 mb-6">
                    {tabs.map((tab) => {
                        const IconComponent = tab.icon
                        return (
                            <button
                                key={tab.id}
                                onClick={() => setActiveTab(tab.id)}
                                className={`flex items-center space-x-2 px-6 py-3 rounded-xl font-medium transition-all duration-300 transform hover:scale-105 ${
                                    activeTab === tab.id
                                        ? "bg-gradient-to-r from-[#A09F87] to-[#4C3163] text-white shadow-lg"
                                        : "text-[#A09F87] hover:bg-[#202020]/50 backdrop-blur-sm"
                                }`}
                            >
                                <IconComponent className="w-5 h-5" />
                                <span>{tab.label}</span>
                                {tab.id === "signals" && dashboardStats.pendingSignals > 0 && (
                                    <span className="bg-red-500 text-white text-xs px-2 py-1 rounded-full animate-pulse">
                                        {dashboardStats.pendingSignals}
                                    </span>
                                )}
                            </button>
                        )
                    })}
                </div>
            </div>

            {/* Content based on active tab */}
            {activeTab === "signals" ? (
                <Signals />
            ) : (
                <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 relative z-10">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                        {stats.map((stat, index) => {
                            const IconComponent = stat.icon
                            return (
                                <div
                                    key={index}
                                    className={`group relative backdrop-blur-xl bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-2xl shadow-xl border border-[#202020] p-6 hover:shadow-2xl transition-all duration-500 transform hover:scale-105 hover:-translate-y-2 ${mounted ? "animate-slide-up" : "opacity-0"
                                        }`}
                                    style={{
                                        animationDelay: `${index * 0.15}s`,
                                        animationFillMode: "both",
                                    }}
                                >
                                    <div className="absolute inset-0 bg-gradient-to-br from-[#4C3163]/20 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>

                                    <div className="relative flex items-center justify-between">
                                        <div className="flex-1">
                                            <p className="text-sm font-semibold text-[#A09F87]/80 mb-2 group-hover:text-[#A09F87] transition-colors">
                                                {stat.title}
                                            </p>
                                            <p className="text-3xl font-bold text-white mb-2 group-hover:scale-105 transition-transform duration-300">
                                                {stat.value}
                                            </p>
                                            <div className="flex items-center">
                                                <span
                                                    className={`text-sm font-bold px-2 py-1 rounded-full ${stat.trend === "up" ? "text-emerald-300 bg-emerald-900/50" : "text-red-300 bg-red-900/50"
                                                        }`}
                                                >
                                                    {stat.change}
                                                </span>
                                                <span className="text-xs text-[#A09F87]/60 ml-2 font-medium">vs mois dernier</span>
                                            </div>
                                        </div>
                                        <div className="w-14 h-14 bg-gradient-to-br from-[#A09F87] to-[#4C3163] rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-xl group-hover:scale-110 transition-all duration-300 animate-pulse-gentle">
                                            <IconComponent className="w-7 h-7 text-white" />
                                        </div>
                                    </div>
                                </div>
                            )
                        })}
                    </div>

                    <div
                        className={`relative backdrop-blur-xl bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-2xl shadow-2xl border border-[#202020] overflow-hidden ${mounted ? "animate-fade-in-up" : "opacity-0"
                            }`}
                        style={{ animationDelay: "0.6s", animationFillMode: "both" }}
                    >
                        <div className="absolute inset-0 opacity-5">
                            <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(160,159,135,0.2),transparent_50%)] animate-pulse-slow"></div>
                        </div>

                        <div className="relative px-6 py-5 border-b border-[#4C3163]/50 backdrop-blur-sm">
                            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
                                <h2 className="text-xl font-bold bg-gradient-to-r from-white to-[#A09F87] bg-clip-text text-transparent mb-4 sm:mb-0">
                                    Gestion des Utilisateurs
                                </h2>
                                <div className="flex flex-col sm:flex-row gap-3">
                                    <div className="relative group">
                                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-[#A09F87]/90 w-4 h-4 group-focus-within:text-[#A09F87] transition-colors" />
                                        <input
                                            type="text"
                                            placeholder="Rechercher un utilisateur..."
                                            value={searchTerm}
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                            className="pl-10 pr-4 py-2.5 border border-[#A09F87] rounded-xl focus:ring-2 focus:ring-[#A09F87]/50 focus:border-[#A09F87]/50 text-sm backdrop-blur-sm bg-[#202020]/50 text-white/70 placeholder-[#A09F87] transition-all duration-300 hover:bg-[#202020]/70 focus:bg-[#202020]/80"
                                        />
                                    </div>
                                    <select
                                        value={filterStatus}
                                        onChange={(e) => setFilterStatus(e.target.value)}
                                        className="px-4 py-2.5 border border-[#A09F87] rounded-xl focus:ring-2 focus:ring-[#A09F87]/50 focus:border-[#A09F87]/50 text-sm backdrop-blur-sm bg-[#202020]/50 text-white/80 transition-all duration-300 hover:bg-[#202020]/70 focus:bg-[#202020]/80"
                                    >
                                        <option value="tous">Tous les statuts</option>
                                        <option value="actif">Actif</option>
                                        <option value="inactif">Inactif</option>
                                    </select>
                                    <button className="px-6 py-2.5 bg-gradient-to-r from-[#A09F87] to-[#4C3163] text-white rounded-xl hover:from-[#A09F87]/80 hover:to-[#4C3163]/80 transition-all duration-300 text-sm font-medium flex items-center gap-2 shadow-lg hover:shadow-xl transform hover:scale-105 active:scale-95">
                                        <UserPlus className="w-4 h-4" />
                                        Ajouter
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead className="bg-gradient-to-r from-[#202020]/80 to-[#4C3163]/80 backdrop-blur-sm">
                                    <tr>
                                        <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase tracking-wider">
                                            Utilisateur
                                        </th>
                                        <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase tracking-wider">
                                            Contact
                                        </th>
                                        <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase tracking-wider">
                                            Rôle
                                        </th>
                                        <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase tracking-wider">
                                            Statut
                                        </th>
                                        <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase tracking-wider">
                                            Date d'inscription
                                        </th>
                                        <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase tracking-wider">
                                            Actions
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-[#4C3163]/50">
                                    {currentUsers.map((user, index) => (
                                        <tr
                                            key={user.id}
                                            className={`group  bg-[#202020] transition-all duration-300 backdrop-blur-sm ${mounted ? "animate-slide-in-right" : "opacity-0"
                                                }`}
                                            style={{
                                                animationDelay: `${0.8 + index * 0.1}s`,
                                                animationFillMode: "both",
                                            }}
                                        >
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex items-center">
                                                    <div className="relative">
                                                        <img
                                                            className="h-12 w-12 rounded-full object-cover ring-2 ring-[#4C3163]/50 shadow-lg group-hover:ring-[#A09F87]/50 transition-all duration-300 group-hover:scale-110"
                                                            src={
                                                                user.profilePicture
                                                                    ? user.profilePicture.startsWith("http")
                                                                        ? user.profilePicture
                                                                        : `http://localhost:8080${user.profilePicture || "/placeholder.svg"}`
                                                                    : profileAvatar
                                                            }
                                                            alt={`${user.prenom} ${user.nom}`}
                                                        />
                                                        <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-gradient-to-r from-green-400 to-emerald-500 rounded-full border-2 border-[#171717] shadow-sm animate-pulse-gentle"></div>
                                                    </div>
                                                    <div className="ml-4">
                                                        <div className="text-sm font-bold text-white/70 group-hover:text-[#A09F87] transition-colors">
                                                            {user.prenom} {user.nom}
                                                        </div>
                                                        <div className="text-xs text-[#A09F87]/60 font-medium">ID: {user.id}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-sm text-[#A09F87]/80 flex items-center group-hover:text-[#A09F87] transition-colors">
                                                    <Mail className="w-4 h-4 mr-2 text-[#A09F87]/60 group-hover:text-[#A09F87] transition-colors" />
                                                    {user.email}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span
                                                    className={`inline-flex px-3 py-1.5 text-xs font-bold rounded-full border backdrop-blur-sm ${getRoleColor(user.role)} group-hover:scale-105 transition-transform duration-300`}
                                                >
                                                    {user.role}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span
                                                    className={`inline-flex items-center px-3 py-1.5 text-xs font-bold rounded-full border backdrop-blur-sm ${getStatusColor(user.status)} group-hover:scale-105 transition-transform duration-300`}
                                                >
                                                    <div
                                                        className={`w-2 h-2 rounded-full mr-2 ${user.status === "Actif" ? "bg-green-400 animate-pulse" : "bg-red-400"}`}
                                                    ></div>
                                                    {user.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-[#A09F87]/60">
                                                <div className="flex items-center group-hover:text-[#A09F87] transition-colors">
                                                    <Calendar className="w-4 h-4 mr-2 text-[#A09F87]/60 group-hover:text-[#A09F87] transition-colors" />
                                                    {new Date(user.dateInscription).toLocaleDateString("fr-FR")}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                <div className="flex items-center space-x-3">
                                                    <button className="p-2 text-[#A09F87] hover:text-[#A09F87]/80 hover:bg-[#A09F87]/20 rounded-lg transition-all duration-200 transform hover:scale-110 active:scale-95">
                                                        <Eye className="w-4 h-4" />
                                                    </button>
                                                    <button className="p-2 text-[#89759c] hover:text-[#89759c]/80 hover:bg-[#89759c]/20 rounded-lg transition-all duration-200 transform hover:scale-110 active:scale-95">
                                                        <Edit className="w-4 h-4" />
                                                    </button>
                                                    <button className="p-2 text-red-400 hover:text-red-300 hover:bg-red-900/30 rounded-lg transition-all duration-200 transform hover:scale-110 active:scale-95">
                                                        <Trash2 className="w-4 h-4" />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {totalPages > 1 && (
                            <div className="px-6 py-4 border-t border-[#4C3163]/50 backdrop-blur-sm bg-gradient-to-r from-[#202020]/50 to-[#4C3163]/50">
                                <div className="flex items-center justify-between">
                                    <div className="text-sm text-[#A09F87] font-medium">
                                        Affichage de {indexOfFirstUser + 1} à {Math.min(indexOfLastUser, filteredUsers.length)} sur{" "}
                                        {filteredUsers.length} utilisateurs
                                    </div>
                                    <div className="flex space-x-2">
                                        <button
                                            onClick={() => paginate(currentPage - 1)}
                                            disabled={currentPage === 1}
                                            className="px-4 py-2 text-sm border border-[#4C3163]/50 rounded-xl hover:bg-[#202020]/70 disabled:opacity-50 disabled:cursor-not-allowed backdrop-blur-sm bg-[#202020]/50 text-white transition-all duration-300 transform hover:scale-105 active:scale-95"
                                        >
                                            Précédent
                                        </button>
                                        {[...Array(totalPages)].map((_, index) => (
                                            <button
                                                key={index + 1}
                                                onClick={() => paginate(index + 1)}
                                                className={`px-4 py-2 text-sm border rounded-xl transition-all duration-300 transform hover:scale-105 active:scale-95 ${currentPage === index + 1
                                                    ? "bg-gradient-to-r from-[#A09F87] to-[#4C3163] text-white border-[#A09F87] shadow-lg"
                                                    : "border-[#4C3163]/50 hover:bg-[#202020]/70 backdrop-blur-sm bg-[#202020]/50 text-white"
                                                    }`}
                                            >
                                                {index + 1}
                                            </button>
                                        ))}
                                        <button
                                            onClick={() => paginate(currentPage + 1)}
                                            disabled={currentPage === totalPages}
                                            className="px-4 py-2 text-sm border border-[#4C3163]/50 rounded-xl hover:bg-[#202020]/70 disabled:opacity-50 disabled:cursor-not-allowed backdrop-blur-sm bg-[#202020]/50 text-white transition-all duration-300 transform hover:scale-105 active:scale-95"
                                        >
                                            Suivant
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}

                        {filteredUsers.length === 0 && (
                            <div className="text-center py-16 backdrop-blur-sm">
                                <Users className="mx-auto h-16 w-16 text-[#A09F87]/50 animate-pulse" />
                                <h3 className="mt-4 text-lg font-bold text-white">Aucun utilisateur trouvé</h3>
                                <p className="mt-2 text-sm text-[#A09F87]/60">Essayez de modifier vos critères de recherche.</p>
                            </div>
                        )}
                    </div>
                </main>
            )}
        </div>
    )
}

export default AdminDashboard