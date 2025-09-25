import { useState } from "react"
import { useNavigate, useLocation } from "react-router-dom"
import { ArrowLeft, Send, Tag, User, Quote, BookOpen } from "lucide-react"
import { FaQuoteLeft } from "react-icons/fa"
import { BiBook } from "react-icons/bi"
import { useSelector } from "react-redux"
import { createHistoireArticle, createSagesseArticle } from "../../services/ArticleService"
import { errorNotification, successNotification } from "../../services/NotificationService"

const CreateArticleForm = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const searchParams = new URLSearchParams(location.search)
  const type = searchParams.get("type") || "wisdom"
  const isWisdom = type === "wisdom"

  const form = {
    title: "",
    content: "",
    source: "",
    category: "",
    tags: "",
    lesson: "",
  }

  const [formData, setFormData] = useState(form)
  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const user = useSelector((state) => state.user)

  const handleBack = () => {
    navigate("/articla/nouveauArticle")
  }

  const validateForm = () => {
    const newErrors = {}

    // Validation pour sagesse
    if (isWisdom) {
      if (!formData.title.trim()) {
        newErrors.title = "Le contenu de la sagesse est obligatoire"
      } else if (formData.title.trim().length < 10) {
        newErrors.title = "Le contenu doit contenir au moins 10 caractères"
      } else if (formData.title.trim().length > 1000) {
        newErrors.title = "Le contenu ne peut pas dépasser 1000 caractères"
      }

      if (formData.source && formData.source.length > 200) {
        newErrors.source = "La source ne peut pas dépasser 200 caractères"
      }
    } else {
      // Validation pour histoire
      if (!formData.title.trim()) {
        newErrors.title = "Le titre de l'histoire est obligatoire"
      } else if (formData.title.trim().length < 5) {
        newErrors.title = "Le titre doit contenir au moins 5 caractères"
      } else if (formData.title.trim().length > 200) {
        newErrors.title = "Le titre ne peut pas dépasser 200 caractères"
      }

      if (!formData.content.trim()) {
        newErrors.content = "Le contenu de l'histoire est obligatoire"
      } else if (formData.content.trim().length < 50) {
        newErrors.content = "Le contenu doit contenir au moins 50 caractères"
      } else if (formData.content.trim().length > 5000) {
        newErrors.content = "Le contenu ne peut pas dépasser 5000 caractères"
      }

      if (formData.lesson && formData.lesson.length > 1000) {
        newErrors.lesson = "La leçon ne peut pas dépasser 1000 caractères"
      }
    }

    // Validation commune
    if (!formData.category) {
      newErrors.category = "La catégorie est obligatoire"
    }

    // Validation des tags
    if (formData.tags.trim()) {
      const tagsArray = formData.tags.split(",").map(tag => tag.trim()).filter(tag => tag.length > 0)
      if (tagsArray.length > 10) {
        newErrors.tags = "Vous ne pouvez pas ajouter plus de 10 tags"
      }
      if (tagsArray.some(tag => tag.length > 30)) {
        newErrors.tags = "Chaque tag ne peut pas dépasser 30 caractères"
      }
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!validateForm()) {
      errorNotification('Erreur', 'Veuillez corriger les erreurs dans le formulaire')
      return
    }

    setIsSubmitting(true)

    try {
      const tagsArray = formData.tags
        .split(",")
        .map(tag => tag.trim())
        .filter(tag => tag.length > 0)

      if (isWisdom) {
        const data = {
          content: formData.title,
          userId: user.id,
          source: formData.source || null,
          category: formData.category,
          tags: tagsArray,
        }
        
        await createSagesseArticle(data)
        successNotification('Succès', 'Sagesse créée avec succès !')
      } else {
        const data = {
          title: formData.title,
          content: formData.content,
          userId: user.id,
          lesson: formData.lesson || null,
          category: formData.category,
          tags: tagsArray,
        }
        
        await createHistoireArticle(data)
        successNotification('Succès', 'Histoire créée avec succès !')
      }

      setFormData(form)
      setTimeout(() => {
        navigate("/articla")
      }, 2000)
    } catch (err) {
      console.error(err)
      const errorMessage = err.response?.data?.error || err.response?.data?.errorMessage || 'Une erreur est survenue'
      errorNotification('Erreur', errorMessage)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
    // Effacer l'erreur pour ce champ quand l'utilisateur tape
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }))
    }
  }

  return (
    <div className="min-h-screen p-6 flex items-center justify-center bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%">
      <div className="w-full max-w-4xl bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-3 rounded-lg shadow-lg">
        {/* Header */}
        <div className="text-center pb-8 p-6">
          <div className="flex items-center justify-between mb-4">
            <button
              onClick={handleBack}
              className="text-[#A09F87] hover:text-white hover:bg-[#4C3163] p-2 rounded transition-colors"
            >
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div className="flex items-center gap-3">
              {isWisdom ? (
                <FaQuoteLeft className="text-4xl text-[#A09F87]" />
              ) : (
                <BiBook className="text-4xl text-[#A09F87]" />
              )}
            </div>
            <div></div>
          </div>
          <h1 className="text-3xl md:text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#A09F87] to-white">
            {isWisdom ? "Nouvelle Sagesse Partagée" : "Nouvelle Expérience Personnelle"}
          </h1>
        </div>

        {/* Form */}
        <div className="p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Title/Quote Field */}
            <div className="space-y-2">
              <label className="text-[#A09F87] font-semibold flex items-center gap-2">
                {isWisdom ? <Quote className="w-4 h-4" /> : <BookOpen className="w-4 h-4" />}
                {isWisdom ? "Citation/Proverbe" : "Titre de l'histoire"} *
              </label>
              <input
                value={formData.title}
                onChange={(e) => handleInputChange("title", e.target.value)}
                placeholder={
                  isWisdom ? "Entrez votre citation ou proverbe..." : "Donnez un titre à votre histoire..."
                }
                className={`w-full bg-[#202020] border ${errors.title ? 'border-red-500' : 'border-[#4C3163]'} text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none`}
              />
              {errors.title && <p className="text-red-400 text-sm">{errors.title}</p>}
              <p className="text-xs text-[#A09F87]/60">
                {isWisdom 
                  ? `${formData.title.length}/1000 caractères (minimum 10)` 
                  : `${formData.title.length}/200 caractères (minimum 5)`
                }
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Category Selection */}
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold">Catégorie *</label>
                <select
                  value={formData.category}
                  onChange={(e) => handleInputChange("category", e.target.value)}
                  className={`w-full mt-1.5 bg-[#202020] border ${errors.category ? 'border-red-500' : 'border-[#4C3163]'} text-white focus:border-[#A09F87] px-3 py-2 rounded outline-none`}
                >
                  <option value="">Choisir une catégorie</option>
                  {isWisdom ? (
                    <>
                      <option value="philosophie">Philosophie</option>
                      <option value="inspiration">Inspiration</option>
                      <option value="vie_quotidienne">Vie quotidienne</option>
                      <option value="citations_celebres">Citations célèbres</option>
                      <option value="sagesse_populaire">Sagesse populaire</option>
                    </>
                  ) : (
                    <>
                      <option value="inspirante">Inspirante</option>
                      <option value="drole">Drôle</option>
                      <option value="emouvante">Émouvante</option>
                      <option value="surprenante">Surprenante</option>
                      <option value="defi">Défi personnel</option>
                    </>
                  )}
                </select>
                {errors.category && <p className="text-red-400 text-sm">{errors.category}</p>}
              </div>

              {/* Tags */}
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold flex items-center gap-2">
                  <Tag className="w-4 h-4" />
                  Tags (séparés par des virgules)
                </label>
                <input
                  value={formData.tags}
                  onChange={(e) => handleInputChange("tags", e.target.value)}
                  placeholder="motivation, amour, travail..."
                  className={`w-full bg-[#202020] border ${errors.tags ? 'border-red-500' : 'border-[#4C3163]'} text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none`}
                />
                {errors.tags && <p className="text-red-400 text-sm">{errors.tags}</p>}
                <p className="text-xs text-[#A09F87]/60">Maximum 10 tags, 30 caractères par tag</p>
              </div>
            </div>

            {/* Content/Story Field */}
            {!isWisdom && (
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold">Racontez votre histoire *</label>
                <textarea
                  value={formData.content}
                  onChange={(e) => handleInputChange("content", e.target.value)}
                  placeholder="Partagez votre expérience, vos émotions, les détails qui rendent votre histoire unique..."
                  rows={8}
                  className={`w-full bg-[#202020] border ${errors.content ? 'border-red-500' : 'border-[#4C3163]'} text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none resize-none`}
                />
                {errors.content && <p className="text-red-400 text-sm">{errors.content}</p>}
                <p className="text-xs text-[#A09F87]/60">{formData.content.length}/5000 caractères (minimum 50)</p>
              </div>
            )}

            {/* Source for wisdom */}
            {isWisdom && (
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold">Source/Origine culturelle (optionnel)</label>
                <input
                  value={formData.source}
                  onChange={(e) => handleInputChange("source", e.target.value)}
                  placeholder="Proverbe africain, Citation de..."
                  className={`w-full bg-[#202020] border ${errors.source ? 'border-red-500' : 'border-[#4C3163]'} text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none`}
                />
                {errors.source && <p className="text-red-400 text-sm">{errors.source}</p>}
                <p className="text-xs text-[#A09F87]/60">{formData.source.length}/200 caractères</p>
              </div>
            )}

            {/* Lesson for stories */}
            {!isWisdom && (
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold">Leçon tirée (optionnel)</label>
                <textarea
                  value={formData.lesson}
                  onChange={(e) => handleInputChange("lesson", e.target.value)}
                  placeholder="Quelle leçon avez-vous tirée de cette expérience ?"
                  rows={3}
                  className={`w-full bg-[#202020] border ${errors.lesson ? 'border-red-500' : 'border-[#4C3163]'} text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none resize-none`}
                />
                {errors.lesson && <p className="text-red-400 text-sm">{errors.lesson}</p>}
                <p className="text-xs text-[#A09F87]/60">{formData.lesson.length}/1000 caractères</p>
              </div>
            )}

            <div className="flex justify-center pt-6">
              <button
                type="submit"
                disabled={isSubmitting}
                className="bg-gradient-to-r from-[#4C3163] to-[#A09F87] hover:from-[#A09F87] hover:to-[#4C3163] disabled:opacity-50 disabled:cursor-not-allowed text-white px-12 py-3 text-lg font-semibold rounded-lg transition-all duration-300 flex items-center gap-3"
              >
                <Send className="w-5 h-5" />
                {isSubmitting ? 'Publication...' : `Partager ${isWisdom ? "la sagesse" : "l'histoire"}`}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default CreateArticleForm