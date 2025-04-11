import React from 'react'
import brain from '../assets/brain.jpg'
import { motion } from 'framer-motion';
import { fadeIn } from '../motions/variants';
import { Link as LinkRouter } from "react-router-dom";

const Accueil = () => {
  return (
    <section id='Accueil' className='text-white section min-h-[85vh] lg:min-h-[78vh]'>
      <div className='sm:container p-[20px] pt-22 mx-auto flex xl:flex-row flex-col-reverse  items-center gap-x-12 gap-y-16'>
        <div className='flex-1 space-y-10 place-items-center'>
          <motion.div
            variants={fadeIn('up', 0.3)}
            initial="hidden" whileInView={'show'}
            viewport={{ once: false, amount: 0.7 }}
            className='text-gradient lg:text-6xl text-5xl charm'
          >
            Bienvenue
          </motion.div>
          <motion.div
            variants={fadeIn('up', 0.4)}
            initial="hidden" whileInView={'show'}
            viewport={{ once: false, amount: 0.7 }}
            className='text-white/80 lg:text-[30px] text-[20px] charm 2xl:leading-18 lg:leading-14 leading-8  lg:text-current text-center'
          >
            Un espace où les mots prennent vie. Ici, proverbes, citations et expériences personnelles se partagent pour nourrir l'âme et éclairer le chemin de chacun. Chaque partage devient une source d'inspiration, un écho de sagesse traversant le temps et les cultures. Chaque histoire, leçon et pensée a le pouvoir de transformer et d’enrichir la vie des autres.
          </motion.div>
          <motion.div className='flex justify-center'
            variants={fadeIn('up', 0.5)}
            initial="hidden" whileInView={'show'}
            viewport={{ once: false, amount: 0.7 }}
          >
            <button className='border-gradient hover:border-white/45 lg:px-6 lg:py-4 px-4 py-2 text-xl lg:text-2xl font-bold cursor-pointer background-gradient '><LinkRouter to='/login'>Se Connecter</LinkRouter></button>
          </motion.div>
        </div>
        <motion.div
          variants={fadeIn('down', 0.5)}
          initial="hidden" whileInView={'show'}
          className='flex-1 justify-items-end items-center '
        >
          <img src={brain} alt="" className='xl:h-150 xl:max-h-max h-auto max-h-90 sm:h-100 sm:max-h-max rounded-full border-3 border-gradient' />
        </motion.div>
      </div>
    </section>
  )
}

export default Accueil
