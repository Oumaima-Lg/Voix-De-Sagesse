"use client"
import { useNavigate } from "react-router-dom"
import { PiArticleLight } from "react-icons/pi"
import { FaQuoteLeft } from "react-icons/fa"
import { BiBook } from "react-icons/bi"

const ArticleTypeSelection = () => {
  const navigate = useNavigate()

  const handleTypeSelect = (type) => {
    navigate(`/articla/creerArticle?type=${type}`)
  }

  const handleViewFeed = () => {
    navigate("/articla/article")
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-6 bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%">
      <div className="text-center mb-12">
        <h1 className="text-4xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#A09F87] to-white mb-4">
          CRÉER UN NOUVEL ARTICLE
        </h1>
        <p className="text-[#A09F87] text-lg md:text-xl">Choisissez le type d'article que vous souhaitez partager</p>
      </div>

      <div className="flex flex-col md:flex-row gap-8 mb-12">
        {/* Sagesse Partagée Card */}
        <div
          className="w-full md:w-96 bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-3 hover:scale-105 transform-gpu transition-all duration-300 cursor-pointer group rounded-lg shadow-lg"
          onClick={() => handleTypeSelect("wisdom")}
        >
          <div className="p-8 text-center">
            <div className="mb-6 text-6xl text-[#A09F87] group-hover:text-white transition-colors">
              <FaQuoteLeft className="mx-auto" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-4 group-hover:text-[#A09F87] transition-colors">
              Sagesse Partagée
            </h3>
            <p className="text-gray-300 mb-6 leading-relaxed">
              Partagez votre proverbe ou citation inspirante. Sagesse populaire, Inspiration, Philosophie, Vie
              quotidienne, Citations célèbres
            </p>
            <div className="flex flex-wrap gap-2 justify-center">
              <span className="px-3 py-1 bg-[#4C3163] text-white text-sm rounded-full">#Philosophie</span>
              <span className="px-3 py-1 bg-[#4C3163] text-white text-sm rounded-full">#Inspiration</span>
              <span className="px-3 py-1 bg-[#4C3163] text-white text-sm rounded-full">#Citations</span>
            </div>
          </div>
        </div>

        {/* Histoires Personnelles Card */}
        <div
          className="w-full md:w-96 bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% border-[#202020] border-3 hover:scale-105 transform-gpu transition-all duration-300 cursor-pointer group rounded-lg shadow-lg"
          onClick={() => handleTypeSelect("story")}
        >
          <div className="p-8 text-center">
            <div className="mb-6 text-6xl text-[#A09F87] group-hover:text-white transition-colors">
              <BiBook className="mx-auto" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-4 group-hover:text-[#A09F87] transition-colors">
              Histoires Personnelles
            </h3>
            <p className="text-gray-300 mb-6 leading-relaxed">
              Partagez vos moments de vie, vos défis et vos triomphes. Racontez vos histoires inspirantes, drôles,
              émouvantes ou surprenantes.
            </p>
            <div className="flex flex-wrap gap-2 justify-center">
              <span className="px-3 py-1 bg-[#4C3163] text-white text-sm rounded-full">#Inspirant</span>
              <span className="px-3 py-1 bg-[#4C3163] text-white text-sm rounded-full">#Émouvant</span>
              <span className="px-3 py-1 bg-[#4C3163] text-white text-sm rounded-full">#Vie</span>
            </div>
          </div>
        </div>
      </div>

      <button
        onClick={handleViewFeed}
        className="bg-gradient-to-r from-[#4C3163] to-[#A09F87] hover:from-[#A09F87] hover:to-[#4C3163] text-white px-8 py-3 text-lg font-semibold rounded-lg transition-all duration-300 flex items-center gap-3"
      >
        <PiArticleLight className="text-2xl" />
        Voir tous les articles
      </button>
    </div>
  )
}

export default ArticleTypeSelection
