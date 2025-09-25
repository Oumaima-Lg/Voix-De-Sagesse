import { useState, useEffect } from "react"
import { useSelector } from "react-redux"
import { 
    AlertTriangle, 
    Eye, 
    Check, 
    X, 
    Clock, 
    User, 
    FileText, 
    Calendar, 
    Filter,
    Search,
    ChevronLeft,
    ChevronRight,
    Tag,
    BookOpen,
    Quote
} from "lucide-react"
import { getAllSignals, processSignal } from "../../services/AdminService"
import { errorNotification, successNotification } from "../../services/NotificationService"

const Signals = () => {
    const user = useSelector((state) => state.user)
    const [signals, setSignals] = useState([])
    const [isLoading, setIsLoading] = useState(true)
    const [searchTerm, setSearchTerm] = useState("")
    const [filterStatus, setFilterStatus] = useState("tous")
    const [currentPage, setCurrentPage] = useState(1)
    const [signalsPerPage] = useState(10)
    const [selectedSignal, setSelectedSignal] = useState(null)
    const [showModal, setShowModal] = useState(false)
    const [processingSignal, setProcessingSignal] = useState(false)

    useEffect(() => {
        fetchSignals()
    }, [])

    const fetchSignals = async () => {
        try {
            setIsLoading(true)
            const data = await getAllSignals()
            setSignals(data)
        } catch (error) {
            console.error("Erreur lors de la récupération des signalements:", error)
            errorNotification("Erreur", "Impossible de charger les signalements")
        } finally {
            setIsLoading(false)
        }
    }

    const getStatusColor = (status) => {
        switch (status) {
            case "PENDING":
                return "bg-yellow-100 text-yellow-800 border-yellow-200"
            case "APPROVED":
                return "bg-green-100 text-green-800 border-green-200"
            case "REJECTED":
                return "bg-red-100 text-red-800 border-red-200"
            default:
                return "bg-gray-100 text-gray-800 border-gray-200"
        }
    }

    const getTypeColor = (reason) => {
        switch (reason?.toLowerCase()) {
            case "spam":
                return "bg-orange-100 text-orange-800 border-orange-200"
            case "harassment":
                return "bg-red-100 text-red-800 border-red-200"
            case "inappropriate_content":
                return "bg-purple-100 text-purple-800 border-purple-200"
            case "hate_speech":
                return "bg-red-200 text-red-900 border-red-300"
            default:
                return "bg-gray-100 text-gray-800 border-gray-200"
        }
    }

    const getStatusText = (status) => {
        switch (status) {
            case "PENDING":
                return "En attente"
            case "APPROVED":
                return "Approuvé"
            case "REJECTED":
                return "Rejeté"
            default:
                return status
        }
    }

    const getReasonText = (reason) => {
        switch (reason?.toLowerCase()) {
            case "spam":
                return "Spam"
            case "harassment":
                return "Harcèlement"
            case "inappropriate_content":
                return "Contenu inapproprié"
            case "hate_speech":
                return "Discours haineux"
            default:
                return reason || "Autre"
        }
    }

    const filteredSignals = signals.filter((signal) => {
        const matchesSearch = 
            signal.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            signal.reporterName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            signal.reportedUserName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            signal.articleTitle?.toLowerCase().includes(searchTerm.toLowerCase())
        const matchesFilter = filterStatus === "tous" || signal.status === filterStatus
        return matchesSearch && matchesFilter
    })

    const indexOfLastSignal = currentPage * signalsPerPage
    const indexOfFirstSignal = indexOfLastSignal - signalsPerPage
    const currentSignals = filteredSignals.slice(indexOfFirstSignal, indexOfLastSignal)
    const totalPages = Math.ceil(filteredSignals.length / signalsPerPage)

    const handleProcessSignal = async (signalId, status) => {
        try {
            setProcessingSignal(true)
            await processSignal(signalId, status, user.id)
            
            // Mise à jour locale
            setSignals(signals.map(signal => 
                signal.id === signalId 
                    ? { 
                        ...signal, 
                        status: status, 
                        processedAt: new Date().toISOString(), 
                        adminId: user.id 
                    }
                    : signal
            ))

            successNotification("Succès", `Signalement ${status === 'APPROVED' ? 'approuvé' : 'rejeté'} avec succès`)
            
            if (showModal) {
                setShowModal(false)
            }
        } catch (error) {
            console.error("Erreur lors du traitement du signalement:", error)
            errorNotification("Erreur", "Impossible de traiter le signalement")
        } finally {
            setProcessingSignal(false)
        }
    }

    const openModal = (signal) => {
        setSelectedSignal(signal)
        setShowModal(true)
    }

    const formatArticleType = (type) => {
        switch (type) {
            case "SAGESSE":
                return "Sagesse"
            case "HISTOIRE":
                return "Histoire"
            default:
                return type
        }
    }

    const formatCategory = (category) => {
        const categoryMap = {
            "philosophie": "Philosophie",
            "inspiration": "Inspiration",
            "vie_quotidienne": "Vie quotidienne",
            "citations_celebres": "Citations célèbres",
            "sagesse_populaire": "Sagesse populaire",
            "inspirante": "Inspirante",
            "drole": "Drôle",
            "emouvante": "Émouvante",
            "surprenante": "Surprenante",
            "defi": "Défi personnel"
        }
        return categoryMap[category] || category
    }

    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center relative overflow-hidden">
                <div className="w-16 h-16 border-4 border-dashed rounded-full animate-spin border-blue-500"></div>
            </div>
        )
    }

    return (
        <div className="min-h-screen relative overflow-hidden">
            <div className="fixed inset-0 -z-10">
                <div className="absolute inset-0 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%"></div>
            </div>

            <header className="relative backdrop-blur-xl bg-[#171717]/80 shadow-lg border-b border-[#202020]/50 top-0">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            <h1 className="text-2xl font-bold bg-gradient-to-r from-white via-[#A09F87] to-[#4C3163] bg-clip-text text-transparent">
                                Gestion des Signalements
                            </h1>
                        </div>
                    </div>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 relative z-10">
                {/* Statistics Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <div className="backdrop-blur-xl bg-gradient-to-br from-[#171717] to-[#4E3F59] rounded-2xl shadow-xl border border-[#202020] p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-semibold text-[#A09F87]/80 mb-2">Total Signalements</p>
                                <p className="text-3xl font-bold text-white">{signals.length}</p>
                            </div>
                            <div className="w-14 h-14 bg-gradient-to-br from-[#A09F87] to-[#4C3163] rounded-2xl flex items-center justify-center">
                                <AlertTriangle className="w-7 h-7 text-white" />
                            </div>
                        </div>
                    </div>

                    <div className="backdrop-blur-xl bg-gradient-to-br from-[#171717] to-[#4E3F59] rounded-2xl shadow-xl border border-[#202020] p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-semibold text-[#A09F87]/80 mb-2">En Attente</p>
                                <p className="text-3xl font-bold text-white">{signals.filter(s => s.status === 'PENDING').length}</p>
                            </div>
                            <div className="w-14 h-14 bg-gradient-to-br from-yellow-500 to-orange-500 rounded-2xl flex items-center justify-center">
                                <Clock className="w-7 h-7 text-white" />
                            </div>
                        </div>
                    </div>

                    <div className="backdrop-blur-xl bg-gradient-to-br from-[#171717] to-[#4E3F59] rounded-2xl shadow-xl border border-[#202020] p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-semibold text-[#A09F87]/80 mb-2">Traités</p>
                                <p className="text-3xl font-bold text-white">{signals.filter(s => s.status !== 'PENDING').length}</p>
                            </div>
                            <div className="w-14 h-14 bg-gradient-to-br from-green-500 to-emerald-500 rounded-2xl flex items-center justify-center">
                                <Check className="w-7 h-7 text-white" />
                            </div>
                        </div>
                    </div>
                </div>

                {/* Signals Management Table */}
                <div className="backdrop-blur-xl bg-gradient-to-br from-[#171717] to-[#4E3F59] rounded-2xl shadow-2xl border border-[#202020] overflow-hidden">
                    <div className="px-6 py-5 border-b border-[#4C3163]/50">
                        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
                            <h2 className="text-xl font-bold bg-gradient-to-r from-white to-[#A09F87] bg-clip-text text-transparent mb-4 sm:mb-0">
                                Liste des Signalements
                            </h2>
                            <div className="flex flex-col sm:flex-row gap-3">
                                <div className="relative">
                                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-[#A09F87]/90 w-4 h-4" />
                                    <input
                                        type="text"
                                        placeholder="Rechercher..."
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                        className="pl-10 pr-4 py-2.5 border border-[#A09F87] rounded-xl focus:ring-2 focus:ring-[#A09F87]/50 backdrop-blur-sm bg-[#202020]/50 text-white placeholder-[#A09F87]"
                                    />
                                </div>
                                <select
                                    value={filterStatus}
                                    onChange={(e) => setFilterStatus(e.target.value)}
                                    className="px-4 py-2.5 border border-[#A09F87] rounded-xl backdrop-blur-sm bg-[#202020]/50 text-white"
                                >
                                    <option value="tous">Tous les statuts</option>
                                    <option value="PENDING">En attente</option>
                                    <option value="APPROVED">Approuvé</option>
                                    <option value="REJECTED">Rejeté</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gradient-to-r from-[#202020]/80 to-[#4C3163]/80">
                                <tr>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">ID</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Raison</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Signalé par</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Utilisateur ciblé</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Article</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Statut</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Date</th>
                                    <th className="px-6 py-4 text-left text-xs font-bold text-[#A09F87] uppercase">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-[#4C3163]/50">
                                {currentSignals.map((signal) => (
                                    <tr key={signal.id} className="bg-[#202020] hover:bg-[#202020]/70 transition-colors">
                                        <td className="px-6 py-4 text-white font-mono">#{signal.id}</td>
                                        <td className="px-6 py-4">
                                            <span className={`inline-flex px-3 py-1 text-xs font-bold rounded-full border ${getTypeColor(signal.reason)}`}>
                                                {getReasonText(signal.reason)}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="text-sm text-white">
                                                {signal.reporterName}
                                            </div>
                                            <div className="text-xs text-[#A09F87]/60">{signal.reporterEmail}</div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="text-sm text-white">
                                                {signal.reportedUserName}
                                            </div>
                                            <div className="text-xs text-[#A09F87]/60">{signal.reportedUserEmail}</div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="text-sm text-white">
                                                {signal.articleTitle || "Article sans titre"}
                                            </div>
                                            <div className="text-xs text-[#A09F87]/60">{formatArticleType(signal.articleType)}</div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className={`inline-flex px-3 py-1 text-xs font-bold rounded-full border ${getStatusColor(signal.status)}`}>
                                                {getStatusText(signal.status)}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-sm text-[#A09F87]/80">
                                            {new Date(signal.createdAt).toLocaleDateString("fr-FR")}
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center space-x-2">
                                                <button
                                                    onClick={() => openModal(signal)}
                                                    className="p-2 text-[#A09F87] hover:bg-[#A09F87]/20 rounded-lg transition-colors"
                                                >
                                                    <Eye className="w-4 h-4" />
                                                </button>
                                                {signal.status === 'PENDING' && (
                                                    <>
                                                        <button
                                                            onClick={() => handleProcessSignal(signal.id, 'APPROVED')}
                                                            disabled={processingSignal}
                                                            className="p-2 text-green-400 hover:bg-green-900/30 rounded-lg transition-colors disabled:opacity-50"
                                                        >
                                                            <Check className="w-4 h-4" />
                                                        </button>
                                                        <button
                                                            onClick={() => handleProcessSignal(signal.id, 'REJECTED')}
                                                            disabled={processingSignal}
                                                            className="p-2 text-red-400 hover:bg-red-900/30 rounded-lg transition-colors disabled:opacity-50"
                                                        >
                                                            <X className="w-4 h-4" />
                                                        </button>
                                                    </>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="px-6 py-4 border-t border-[#4C3163]/50 bg-gradient-to-r from-[#202020]/50 to-[#4C3163]/50">
                            <div className="flex items-center justify-between">
                                <div className="text-sm text-[#A09F87]">
                                    Affichage de {indexOfFirstSignal + 1} à {Math.min(indexOfLastSignal, filteredSignals.length)} sur {filteredSignals.length} signalements
                                </div>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => setCurrentPage(currentPage - 1)}
                                        disabled={currentPage === 1}
                                        className="px-4 py-2 text-sm border border-[#4C3163]/50 rounded-xl hover:bg-[#202020]/70 disabled:opacity-50 disabled:cursor-not-allowed bg-[#202020]/50 text-white"
                                    >
                                        <ChevronLeft className="w-4 h-4" />
                                    </button>
                                    {[...Array(totalPages)].map((_, index) => (
                                        <button
                                            key={index + 1}
                                            onClick={() => setCurrentPage(index + 1)}
                                            className={`px-4 py-2 text-sm border rounded-xl ${
                                                currentPage === index + 1
                                                    ? "bg-gradient-to-r from-[#A09F87] to-[#4C3163] text-white border-[#A09F87]"
                                                    : "border-[#4C3163]/50 hover:bg-[#202020]/70 bg-[#202020]/50 text-white"
                                            }`}
                                        >
                                            {index + 1}
                                        </button>
                                    ))}
                                    <button
                                        onClick={() => setCurrentPage(currentPage + 1)}
                                        disabled={currentPage === totalPages}
                                        className="px-4 py-2 text-sm border border-[#4C3163]/50 rounded-xl hover:bg-[#202020]/70 disabled:opacity-50 disabled:cursor-not-allowed bg-[#202020]/50 text-white"
                                    >
                                        <ChevronRight className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* Modal for Signal Details */}
                {showModal && selectedSignal && (
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-70 p-4">
                        <div className="bg-gradient-to-br from-[#171717] to-[#4E3F59] rounded-2xl p-6 max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-[#202020]">
                            <div className="flex justify-between items-center mb-6">
                                <h3 className="text-xl font-bold text-white">Détails du Signalement #{selectedSignal.id}</h3>
                                <button
                                    onClick={() => setShowModal(false)}
                                    className="p-2 hover:bg-[#202020]/50 rounded-lg transition-colors"
                                >
                                    <X className="w-5 h-5 text-[#A09F87]" />
                                </button>
                            </div>

                            <div className="space-y-6">
                                {/* Informations du signalement */}
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div>
                                        <label className="text-sm font-semibold text-[#A09F87] block mb-2">Raison du signalement</label>
                                        <span className={`inline-flex px-3 py-1 text-sm font-bold rounded-full border ${getTypeColor(selectedSignal.reason)}`}>
                                            {getReasonText(selectedSignal.reason)}
                                        </span>
                                    </div>

                                    <div>
                                        <label className="text-sm font-semibold text-[#A09F87] block mb-2">Statut actuel</label>
                                        <span className={`inline-flex px-3 py-1 text-sm font-bold rounded-full border ${getStatusColor(selectedSignal.status)}`}>
                                            {getStatusText(selectedSignal.status)}
                                        </span>
                                    </div>
                                </div>

                                {selectedSignal.description && (
                                    <div>
                                        <label className="text-sm font-semibold text-[#A09F87] block mb-2">Description du signalement</label>
                                        <p className="text-white bg-[#202020]/50 p-3 rounded-lg">{selectedSignal.description}</p>
                                    </div>
                                )}

                                {/* Informations sur les utilisateurs */}
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="text-sm font-semibold text-[#A09F87] block mb-2">Signalé par</label>
                                        <div className="bg-[#202020]/50 p-3 rounded-lg">
                                            <p className="text-white font-medium">
                                                {selectedSignal.reporterName}
                                            </p>
                                            <p className="text-[#A09F87]/80 text-sm">{selectedSignal.reporterEmail}</p>
                                        </div>
                                    </div>

                                    <div>
                                        <label className="text-sm font-semibold text-[#A09F87] block mb-2">Utilisateur ciblé</label>
                                        <div className="bg-[#202020]/50 p-3 rounded-lg">
                                            <p className="text-white font-medium">
                                                {selectedSignal.reportedUserName}
                                            </p>
                                            <p className="text-[#A09F87]/80 text-sm">{selectedSignal.reportedUserEmail}</p>
                                        </div>
                                    </div>
                                </div>

                                {/* Informations détaillées de l'article */}
                                <div className="border-t border-[#4C3163]/50 pt-6">
                                    <h4 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                                        {selectedSignal.articleType === 'SAGESSE' ? <Quote className="w-5 h-5" /> : <BookOpen className="w-5 h-5" />}
                                        Article signalé - {formatArticleType(selectedSignal.articleType)}
                                    </h4>

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                                        {selectedSignal.articleTitle && (
                                            <div>
                                                <label className="text-sm font-semibold text-[#A09F87] block mb-2">Titre</label>
                                                <p className="text-white bg-[#202020]/50 p-3 rounded-lg">{selectedSignal.articleTitle}</p>
                                            </div>
                                        )}

                                        <div>
                                            <label className="text-sm font-semibold text-[#A09F87] block mb-2">Catégorie</label>
                                            <p className="text-white bg-[#202020]/50 p-3 rounded-lg">{formatCategory(selectedSignal.articleCategory)}</p>
                                        </div>
                                    </div>

                                    <div className="mb-4">
                                        <label className="text-sm font-semibold text-[#A09F87] block mb-2">Contenu</label>
                                        <div className="text-white bg-[#202020]/50 p-3 rounded-lg max-h-40 overflow-y-auto">
                                            {selectedSignal.articleContent || "Contenu non disponible"}
                                        </div>
                                    </div>

                                    {selectedSignal.articleSource && (
                                        <div className="mb-4">
                                            <label className="text-sm font-semibold text-[#A09F87] block mb-2">Source</label>
                                            <p className="text-white bg-[#202020]/50 p-3 rounded-lg">{selectedSignal.articleSource}</p>
                                        </div>
                                    )}

                                    {selectedSignal.articleLesson && (
                                        <div className="mb-4">
                                            <label className="text-sm font-semibold text-[#A09F87] block mb-2">Leçon</label>
                                            <p className="text-white bg-[#202020]/50 p-3 rounded-lg">{selectedSignal.articleLesson}</p>
                                        </div>
                                    )}

                                    {selectedSignal.articleTags && (
                                        <div className="mb-4">
                                            <label className="text-sm font-semibold text-[#A09F87] block mb-2 flex items-center gap-2">
                                                <Tag className="w-4 h-4" />
                                                Tags
                                            </label>
                                            <div className="flex flex-wrap gap-2">
                                                {selectedSignal.articleTags.split(', ').map((tag, index) => (
                                                    <span key={index} className="bg-[#4C3163] text-white px-2 py-1 rounded-full text-xs">
                                                        {tag}
                                                    </span>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </div>

                                {/* Actions pour les signalements en attente */}
                                {selectedSignal.status === 'PENDING' && (
                                    <div className="flex gap-3 pt-4 border-t border-[#4C3163]/50">
                                        <button
                                            onClick={() => handleProcessSignal(selectedSignal.id, 'APPROVED')}
                                            disabled={processingSignal}
                                            className="flex-1 bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                                        >
                                            <Check className="w-4 h-4" />
                                            {processingSignal ? "Traitement..." : "Approuver le signalement"}
                                        </button>
                                        <button
                                            onClick={() => handleProcessSignal(selectedSignal.id, 'REJECTED')}
                                            disabled={processingSignal}
                                            className="flex-1 bg-red-600 hover:bg-red-700 text-white py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                                        >
                                            <X className="w-4 h-4" />
                                            {processingSignal ? "Traitement..." : "Rejeter le signalement"}
                                        </button>
                                    </div>
                                )}

                                {/* Informations de traitement */}
                                {selectedSignal.processedAt && (
                                    <div className="pt-4 border-t border-[#4C3163]/50">
                                        <div className="text-sm text-[#A09F87]/80">
                                            Traité le {new Date(selectedSignal.processedAt).toLocaleDateString("fr-FR")} à {new Date(selectedSignal.processedAt).toLocaleTimeString("fr-FR")}
                                        </div>
                                        {selectedSignal.adminComment && (
                                            <div className="mt-2">
                                                <label className="text-sm font-semibold text-[#A09F87] block mb-1">Commentaire administrateur</label>
                                                <p className="text-white bg-[#202020]/50 p-2 rounded text-sm">{selectedSignal.adminComment}</p>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                )}
            </main>
        </div>
    )
}

export default Signals