
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
  const user = useSelector((state) => state.user)

  const handleBack = () => {
    navigate("/articla/nouveauArticle")
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    console.log("Form submitted:", formData)
    const tagsArray = formData.tags
      .split(",")
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0);
    // Sagesse
    if (isWisdom) {
      console.log("Sagesse Sagesse");
      const data = {
        content: formData.title,
        userId: user.id,
        source: formData.source,
        category: formData.category,
        tags: tagsArray,
      }
      createSagesseArticle(data).then((res) => {
        console.log(res);
        setFormData(form);
        successNotification('Success', 'Création Article/Sagesse !');

        setTimeout(() => {
          navigate("/articla")
        }, 2000)

      }).catch((err) => {
        console.log(err);
        errorNotification('Failed', err.response.data.errorMessage);
      });
    } else {
      console.log("Sagesse Sagesse");
      const data = {
        title: formData.title,
        content: formData.content,
        userId: user.id,
        lesson: formData.lesson,
        category: formData.category,
        tags: tagsArray,
      }
      createHistoireArticle(data).then((res) => {
        console.log(res);
        setFormData(form);
        successNotification('Success', 'Création Article/Histoire !');

        setTimeout(() => {
          navigate("/articla")
        }, 2000)

      }).catch((err) => {
        console.log(err);
        errorNotification('Failed', err.response.data.errorMessage);
      });
    }
  } 

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
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
            {isWisdom ? "Nouvelle Sagesse Partagée" : "Nouvelle Expériences Personnelle"}
          </h1>
        </div>

        {/* Form */}
        <div className="p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Title/Quote Field */}
            <div className="space-y-2">
              <label className="text-[#A09F87] font-semibold flex items-center gap-2">
                {isWisdom ? <Quote className="w-4 h-4" /> : <BookOpen className="w-4 h-4" />}
                {isWisdom ? "Citation/Proverbe" : "Titre de l'histoire"}
              </label>
              <input
                value={formData.title}
                onChange={(e) => handleInputChange("title", e.target.value)}
                placeholder={
                  isWisdom ? "Entrez votre citation ou proverbe..." : "Donnez un titre à votre histoire..."
                }
                className="w-full bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 ">


              {/* Author/Source Field */}
              {/* {isWisdom && (
                <div className="space-y-2">
                  <label className="text-[#A09F87] font-semibold flex items-center gap-2">
                    <User className="w-4 h-4" />
                    Auteur (optionnel)
                  </label>
                  <input
                    value={formData.author}
                    onChange={(e) => handleInputChange("author", e.target.value)}
                    placeholder="Nom de l'auteur..."
                    className="w-full bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
                  />
                </div>
              )} */}

              {/* Category Selection */}
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold">Catégorie</label>
                <select
                  onChange={(e) => handleInputChange("category", e.target.value)}
                  className="w-full mt-1.5 bg-[#202020] border border-[#4C3163] text-white focus:border-[#A09F87] px-3 py-2 rounded outline-none"
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
                  className="w-full bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
                />
              </div>
            </div>

            {/* Content/Story Field */}
            {!isWisdom && (
              <div className="space-y-2">
                <label className="text-[#A09F87] font-semibold">Racontez votre histoire</label>
                <textarea
                  value={formData.content}
                  onChange={(e) => handleInputChange("content", e.target.value)}
                  placeholder="Partagez votre expérience, vos émotions, les détails qui rendent votre histoire unique..."
                  rows={8}
                  className="w-full bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none resize-none"
                />
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
                  className="w-full bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none"
                />
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
                  className="w-full bg-[#202020] border border-[#4C3163] text-white placeholder:text-gray-400 focus:border-[#A09F87] px-3 py-2 rounded outline-none resize-none"
                />
              </div>
            )}

            <div className="flex justify-center pt-6">
              <button
                type="submit"
                className="bg-gradient-to-r from-[#4C3163] to-[#A09F87] hover:from-[#A09F87] hover:to-[#4C3163] text-white px-12 py-3 text-lg font-semibold rounded-lg transition-all duration-300 flex items-center gap-3"
              >
                <Send className="w-5 h-5" />
                Partager {isWisdom ? "la sagesse" : "l'histoire"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default CreateArticleForm
