import { useState } from "react"
import { Search, Filter, X } from "lucide-react"

const SearchBar = ({ onSearch, onFilterChange, currentFilters }) => {
  const [searchText, setSearchText] = useState("")
  const [showFilters, setShowFilters] = useState(false)
  const [selectedType, setSelectedType] = useState(currentFilters?.type || "")

  const handleSearch = () => {
    onSearch({
      searchText: searchText.trim(),
      type: selectedType
    })
  }

  const handleClearSearch = () => {
    setSearchText("")
    setSelectedType("")
    onSearch({
      searchText: "",
      type: ""
    })
  }

  
  const handleKeyDown = (e) => {
+    if (e.key === "Enter") {
+      e.preventDefault()
+      handleSearch()
+    }
+  }



  return (
    <div className="w-full max-w-2xl mx-auto mb-6">
      <div className="relative">
        <div className="flex items-center bg-gradient-to-r from-[#202020] to-[#2a2a2a] border-2 border-[#4C3163] rounded-lg overflow-hidden shadow-lg">
          <div className="flex-1 flex items-center">
            <Search className="w-5 h-5 text-[#A09F87] ml-4 mr-3" />
            <input
              type="text"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Rechercher dans les articles..."
              className="flex-1 bg-transparent text-white placeholder:text-gray-400 py-3 pr-4 outline-none"
            />
          </div>
          
          <button
            onClick={() => setShowFilters(!showFilters)}
            className={`p-3 transition-colors border-l border-[#4C3163] ${
              showFilters ? "bg-[#A09F87] text-[#171717]" : "text-[#A09F87] hover:bg-[#4C3163]"
            }`}
            title="Filtres"
          >
            <Filter className="w-5 h-5" />
          </button>
          
          <button
            onClick={handleSearch}
            className="bg-[#A09F87] hover:bg-[#A09F87]/80 text-[#171717] px-6 py-3 font-medium transition-colors"
          >
            Rechercher
          </button>
        </div>

        {showFilters && (
          <div className="absolute top-full left-0 right-0 mt-2 bg-[#202020] border-2 border-[#4C3163] rounded-lg p-4 shadow-xl z-10">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-white font-medium">Filtrer par type</h3>
              <button
                onClick={() => setShowFilters(false)}
                className="text-[#A09F87] hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            
            <div className="flex gap-3">
              <button
                onClick={() => setSelectedType("")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  selectedType === ""
                    ? "bg-[#A09F87] text-[#171717]"
                    : "bg-[#4C3163] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717]"
                }`}
              >
                Tous
              </button>
              <button
                onClick={() => setSelectedType("SAGESSE")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  selectedType === "SAGESSE"
                    ? "bg-[#A09F87] text-[#171717]"
                    : "bg-[#4C3163] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717]"
                }`}
              >
                Sagesse
              </button>
              <button
                onClick={() => setSelectedType("HISTOIRE")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  selectedType === "HISTOIRE"
                    ? "bg-[#A09F87] text-[#171717]"
                    : "bg-[#4C3163] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717]"
                }`}
              >
                Expérience
              </button>
            </div>
          </div>
        )}
      </div>

      {(searchText || selectedType) && (
        <div className="flex items-center gap-2 mt-3">
          <span className="text-[#A09F87] text-sm">Filtres actifs:</span>
          {searchText && (
            <span className="bg-[#4C3163] text-[#A09F87] px-3 py-1 rounded-full text-sm">
              Texte: "{searchText}"
            </span>
          )}
          {selectedType && (
            <span className="bg-[#4C3163] text-[#A09F87] px-3 py-1 rounded-full text-sm">
              Type: {selectedType}
            </span>
          )}
          <button
            onClick={handleClearSearch}
            className="text-[#A09F87] hover:text-white text-sm underline ml-2"
          >
            Effacer tout
          </button>
        </div>
      )}
    </div>
  )
}

export default SearchBar