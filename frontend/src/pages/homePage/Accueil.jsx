import React from 'react'
import brain from '../../assets/brain.jpg'
import { motion } from 'framer-motion';
import { fadeIn } from '../../animations/variants';
import { Link as LinkRouter } from "react-router-dom";
import { useSelector } from 'react-redux';

const Accueil = () => {
  const user = useSelector((state) => state.user);
  return (
    <section id='Accueil' className='text-white min-h-svh flex items-center pt-24 lg:pt-28 pb-8'>
      <div className='sm:container w-full p-[20px] mx-auto flex lg:flex-row flex-col items-center gap-x-12 gap-y-10'>
        <div className='flex-1 space-y-6 lg:space-y-8 place-items-center'>
          <motion.div
            variants={fadeIn('up', 0.3)}
            initial="hidden" animate="show"
            className='text-gradient lg:text-6xl text-5xl charm'
          >
            Bienvenue
          </motion.div>
          <motion.div
            variants={fadeIn('up', 0.4)}
            initial="hidden" animate="show"
            className='text-white/80 2xl:text-[28px] xl:text-[24px] lg:text-[22px] text-[20px] charm 2xl:leading-12 xl:leading-10 lg:leading-9 leading-8 lg:text-current text-center'
          >
            Un espace où les mots prennent vie. Ici, proverbes, citations et expériences personnelles se partagent pour nourrir l'âme et éclairer le chemin de chacun. Chaque partage devient une source d'inspiration, un écho de sagesse traversant le temps et les cultures. Chaque histoire, leçon et pensée a le pouvoir de transformer et d’enrichir la vie des autres.
          </motion.div>
          <motion.div className='flex justify-center'
            variants={fadeIn('up', 0.5)}
            initial="hidden" animate="show"
          >
            <button className='border-gradient hover:border-white/45 lg:px-6 lg:py-4 px-4 py-2 text-xl lg:text-2xl font-bold cursor-pointer background-gradient '>
              {user ?
              <LinkRouter to='/espace'>Mon espace</LinkRouter>
              :
              <LinkRouter to='/auth/login'>Se Connecter</LinkRouter>
              }
            </button>
          </motion.div>
        </div>
        <motion.div
          variants={fadeIn('down', 0.5)}
          initial="hidden" animate="show"
          className='flex-1 flex justify-center lg:justify-end'
        >
          <img src={brain} alt="" className='w-[min(70vw,40svh)] lg:w-[min(100%,65svh,37.5rem)] aspect-square object-cover rounded-full border-3 border-gradient' />
        </motion.div>
      </div>
    </section>
  )
}

export default Accueil
