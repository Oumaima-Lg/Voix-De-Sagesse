import React from 'react'
import Form from '../Semi_Components/CardContact';
import robot from '../assets/robot.png';

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
        <div className='text-gradient lg:text-6xl text-center lg:text-left text-5xl charm'>
          Contact
        </div>
        <div className='flex place-items-center md:flex-row flex-col gap-y-16'>
          <div className='flex-1'>
            <Form />
          </div>
          <div className='flex-1 flex justify-center'>
            <img src={robot} alt="" className='object-cover' />
          </div>
        </div>
      </div>
    </section>
  )
}

export default Contact
