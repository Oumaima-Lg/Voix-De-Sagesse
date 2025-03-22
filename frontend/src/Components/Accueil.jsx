import React from 'react'
import brain from '../assets/brain.jpg'

const Accueil = () => {
  return (
    <section id='Accueil' className='text-white section '>
      <div className='container p-[20px] mx-auto flex items-center gap-x-12'>
        <div className='flex-1 space-y-10'>
          <div className='text-gradient text-6xl charm'>
            Bienvenue
          </div>
          <div className='text-white/80 text-[30px] charm leading-18 '>
            Un espace où les mots prennent vie. Ici, proverbes, citations et expériences personnelles se partagent pour nourrir l'âme et éclairer le chemin de chacun. Chaque partage devient une source d'inspiration, un écho de sagesse traversant le temps et les cultures. Chaque histoire, leçon et pensée a le pouvoir de transformer et d’enrichir la vie des autres.
          </div>
          <div className='flex justify-center'>
              <button className='border-gradient hover:border-white/45 px-6 py-4 text-2xl font-bold cursor-pointer background-gradient '>Se Connecter</button>
          </div>
        </div>
        <div className='flex-1 justify-items-end items-center '>
          <img src={brain} alt="" className='h-150 rounded-full border-3 border-gradient' />
        </div>
      </div>
    </section>
  )
}

export default Accueil
