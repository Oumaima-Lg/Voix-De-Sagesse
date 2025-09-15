import { useState, useEffect } from 'react'
import { ChevronUp } from 'lucide-react'

const ScrollToTop = () => {
  const [isVisible, setIsVisible] = useState(false)

  // Fonction pour vérifier la position de scroll
  const toggleVisibility = () => {
    if (window.pageYOffset > 300) {
      setIsVisible(true)
    } else {
      setIsVisible(false)
    }
  }

  // Fonction pour remonter en haut
  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  useEffect(() => {
    window.addEventListener('scroll', toggleVisibility)
    return () => {
      window.removeEventListener('scroll', toggleVisibility)
    }
  }, [])

  return (
    <>
      {isVisible && (
        <button
          onClick={scrollToTop}
          className="fixed bottom-6 right-6 z-50 bg-gradient-to-r from-[#A09F87] to-[#8B8970] hover:from-[#8B8970] hover:to-[#A09F87] text-[#171717] p-4 rounded-full shadow-2xl transition-all duration-300 ease-in-out transform hover:scale-110 hover:shadow-[0_0_30px_rgba(160,159,135,0.5)] group"
          title="Remonter en haut"
        >
          <ChevronUp className="w-6 h-6 transition-transform duration-300 group-hover:animate-bounce" />
          
          {/* Effet de brillance */}
          <div className="absolute inset-0 rounded-full bg-gradient-to-r from-transparent via-white/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 transform -skew-x-12"></div>
          
          {/* Anneau de pulse */}
          <div className="absolute inset-0 rounded-full border-2 border-[#A09F87]/30 animate-ping"></div>
        </button>
      )}
    </>
  )
}

export default ScrollToTop