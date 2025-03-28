import React from 'react'
import { Link } from 'react-scroll';

const HeaderNav = () => {
  return (
    <>
      <Link to='Accueil' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className='text-white cursor-pointer'>
        Accueil
      </Link>
      <Link to='About' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className='text-white cursor-pointer'>
        A propos
      </Link>
      <Link to='Article' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className='text-white cursor-pointer'>
        Article
      </Link>
      <Link to='Contact' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className='text-white cursor-pointer'>
        Contact
      </Link>
      <Link to='' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className='text-white cursor-pointer'>
        Se connecter
      </Link>
    </>
  )
}

export default HeaderNav

