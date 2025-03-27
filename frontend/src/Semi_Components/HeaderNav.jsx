import React from 'react'
import { Link } from 'react-scroll';

const HeaderNav = () => {
  return (
    <>
      <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer font-bold '>
        Accueil
      </Link>
      <Link to='About' spy={true} smooth={true} className='text-white cursor-pointer'>
        A propos
      </Link>
      <Link to='Article' spy={true} smooth={true} className='text-white cursor-pointer'>
        Article
      </Link>
      <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer'>
        Contact
      </Link>
      <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer'>
        Se connecter
      </Link>
    </>
  )
}

export default HeaderNav

