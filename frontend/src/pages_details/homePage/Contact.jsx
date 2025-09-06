import React from 'react'
import Form from '../../semiComponents/CardContact';
import robot from '../../assets/robot.png';
import { motion } from 'framer-motion';
import { fadeIn } from '../../Motions/variants';

// import Spline from '@splinetool/react-spline';

// export function Robot() {
//   return (
//     <Spline scene="https://prod.spline.design/cR5-Bt1zqpwklbuh/scene.splinecode" />
//   );
// }

// import Spline from '@splinetool/react-spline';

// export function AppSpline() {
//   return (
//     <Spline scene="https://prod.spline.design/tArGKfoMGH3aCNk3/scene.splinecode" />
//   );
// }

import Spline from '@splinetool/react-spline';

export function AppSpline() {
  return (
    <Spline scene="https://prod.spline.design/VbFDnYNrBVOXKv9u/scene.splinecode" />
  );
}




const Contact = () => {
  return (
    <section id='Contact' className='section text-white border-1 border-white/0'>
      <div className='sm:container mx-auto p-[20px] pt-35 flex flex-col gap-y-18 mb-20'>
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
              <img src={robot} alt="" className='object-cover' />
            </motion.div>
            <motion.div
              variants={fadeIn('left', 0.2)}
              initial="hidden"
              whileInView={'show'}
              className='lg:absolute  right-65 top-[-20px]'
            >
              <p className='charm text-white text-xl bg-black/48 backdrop-blur-2xl px-4 py-5 rounded-3xl' >I Hate You All</p>
            </motion.div>
          </div>
        </div>
        {/* <div className='hidden xl:block border-7 border-amber-600 w-1/4 h-[400px]  overflow-hidden'>
          <AppSpline />
        </div> */}
      </div>
    </section>
  )
}

export default Contact
