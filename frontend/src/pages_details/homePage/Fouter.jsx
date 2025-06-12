import React from 'react'
import arrow from '../../assets/arrow.png';

const Fouter = () => {
  return (
    <fouter id='' className=' text-white absolute bottom-0! right-0 left-0 '>
      <div className='sm:container mx-auto p-[10px] flex justify-evenly gap-x-2 items-center '>
        <img src={arrow} alt="arrow img" className='lg:h-8 h-4.5' />
        <p className='charm lg:text-xl sm:text-lg xs:text-md text-sm text-center'>&copy; {new Date().getFullYear()} Voix De Sagesse. Tous droits réservés.</p>
        <img src={arrow} alt="arrow img" className='lg:h-8 h-4.5' />
      </div>
    </fouter>
  )
}

export default Fouter
