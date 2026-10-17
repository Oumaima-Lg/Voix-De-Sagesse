// EspacePage.jsx - Solution 2: Utiliser un wrapper avec z-index
import React from 'react'
import Espace from './Espace'

const EspacePage = () => {
  return (
    <div className='min-h-screen bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%'>
      <div className="relative z-10">
        <Espace />
      </div>
    </div>
  )
}

export default EspacePage