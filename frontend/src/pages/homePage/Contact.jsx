import React from 'react'
import Form from '../../Components/CardContact';
import robot from '../../assets/robot.png';
import { motion } from 'framer-motion';
import { fadeIn } from '../../Motions/variants';

const Contact = () => {
  return (
    <section id='Contact' className='section text-white border-1 border-white/0'>
      <div className='sm:container mx-auto p-[20px] flex flex-col gap-y-6'>
        <motion.div
          variants={fadeIn('right', 0.3)}
          initial="hidden"
          whileInView={'show'}
          viewport={{ once: false, amount: 0.3 }} className='text-gradient lg:text-6xl text-center lg:text-left text-5xl charm'
        >
          Contact
        </motion.div>
        <div className='flex place-items-center justify-between lg:flex-row flex-col gap-y-16 relative'>
          <motion.div
            variants={fadeIn('right', 0.4)}
            initial="hidden"
            whileInView={'show'}
            className='flex-2'
          >
            <Form />
          </motion.div>
          <div
            className='flex-1 flex flex-col-reverse gap-y-4 justify-center items-center'
          >
            <motion.div
              variants={fadeIn('left', 0.3)}
              initial="hidden"
              whileInView={'show'}
              className='lg:absolute right-30'
            >
              <img src={robot} alt="" className='h-64 lg:h-72 w-auto' />
            </motion.div>
            <motion.div
              variants={fadeIn('left', 0.2)}
              initial="hidden"
              whileInView={'show'}
              className='lg:absolute  right-65 top-[-20px]'
            >
              <p className='charm text-white text-xl bg-black/48 backdrop-blur-2xl px-4 py-5 rounded-3xl' >I hate this feeling</p>
            </motion.div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default Contact
