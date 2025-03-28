import React from 'react'
import Form from '../Semi_Components/CardContact';
import robot from '../assets/robot.png';
import { motion } from 'framer-motion';
import { fadeIn } from '../Motions/variants';

// import Spline from '@splinetool/react-spline';

// export function Robot() {
//   return (
//     <Spline scene="https://prod.spline.design/cR5-Bt1zqpwklbuh/scene.splinecode" />
//   );
// }


const Contact = () => {
  return (
    <section id='Contact' className='section text-white'>
      <div className='sm:container mx-auto p-[20px] pt-22 flex flex-col gap-y-18'>
        <motion.div
          variants={fadeIn('right', 0.3)}
          initial="hidden"
          whileInView={'show'}
          viewport={{ once: false, amount: 0.3 }} className='text-gradient lg:text-6xl text-center lg:text-left text-5xl charm'
        >
          Contact
        </motion.div>
        <div className='flex place-items-center md:flex-row flex-col gap-y-16'>
          <motion.div
            variants={fadeIn('right', 0.4)}
            initial="hidden"
            whileInView={'show'}
            className='flex-1'
          >
            <Form />
          </motion.div>
          <motion.div
            variants={fadeIn('left', 0.3)}
            initial="hidden"
            whileInView={'show'}
            className='flex-1 lg:relative flex flex-col-reverse gap-y-4 justify-center items-center'
          >
            <div className='lg:absolute left-38 top-[-170px]'>
              <img src={robot} alt="" className='object-cover' />
            </div>
            <div className='lg:absolute left-12 top-[-180px]'>
              <p className='charm text-white text-xl bg-black/48 backdrop-blur-2xl px-4 py-5 rounded-3xl' >I Hate You All</p>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  )
}

export default Contact
