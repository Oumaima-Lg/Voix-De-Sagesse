import React from 'react'
import iconSagesse from '../../assets/iconSagesse.png'
import iconHistoire from '../../assets/iconHistoire.png'
import { motion } from 'framer-motion';
// import { fadeIn } from '../Motions/variants';
import { fadeIn, textVariant, staggerContainer } from "../../Motions/motion";



let fiches = [
  {
    title: 'Sagesse Partagée',
    content: 'Partagez votre proverbe ou citation inspirante.Sagesse populaire, Inspiration, Philosophie, Vie quotidienne, Citations célèbres ...',
    image: iconSagesse,
    text_button: 'Sagesse'
  },
  {
    title: 'Expériences personnelles',
    content: 'Partagez vos moments de vie, vos défis et vos triomphes. Racontez vos histoires inspirantes, drôles, émouvantes ou surprenantes, et laissez les autres découvrir les leçons que vous en avez tirées.',
    image: iconHistoire,
    text_button: 'Histoire'
  },
];


fiches = fiches.map((fiche, index) => {
  const { title, content, image, text_button } = fiche;
  return (
    <motion.div variants={fadeIn("right", "spring", index * 0.5, 0.75)} className='border-gradient border-gradient-hover flex-1 sm:px-14 sm:py-8 px-4 py-4 flex flex-col justify-between sm:gap-y-18 gap-y-8 transform transition-transform duration-600 hover:scale-105'>
      <div className='flex flex-col sm:gap-y-16 gap-y-10 items-center'>
        <h2 className='charm sm:text-4xl text-3xl text-center'>{title}</h2>
        <p className='text-center charm sm:text-2xl text-xl text-white/95'>{content}</p>
      </div>
      <div className='flex justify-between items-center xs:flex-row flex-col gap-y-6'>
        <div>
          <img src={image} alt="icon" className='sm:h-auto h-18 max-h-24' />
        </div>
        <div>
          <button className='border-4 border-gradient sm:py-2 sm:px-4 py-1 px-2  font-bold  sm:text-xl text-[16px] cursor-pointer hover:border-white/45 background-gradient' >Partager votre <br /> {text_button}</button>
        </div>
      </div>
    </motion.div>

  );
});


const About = () => {
  return (
    <motion.section id='About' className='section text-white '
      variants={staggerContainer()}
      initial='hidden'
      whileInView='show'
      viewport={{ once: false, amount: 0.25 }}
    >
      <div className='container mx-auto p-[20px] pt-22 flex flex-col gap-y-18'>
        <motion.div
         variants={textVariant()}
          className='text-gradient lg:text-6xl text-center lg:text-left text-5xl charm'>
          Leçon de vie
        </motion.div>
        <div className='flex gap-18 lg:flex-row flex-col' >
          {fiches}
        </div>
      </div>
    </motion.section>
  )
}

export default About