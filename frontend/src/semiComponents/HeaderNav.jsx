import React from 'react'
import { Link } from 'react-scroll';
import { Link as LinkRouter, useNavigate } from "react-router-dom";
import { useSelector } from 'react-redux';

const HeaderNav = ({ isHidden }) => {
  const user = useSelector((state) => state.user);
  const navigate = useNavigate();
  return (
    <>
      <Link to='Accueil' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer`}>
        Accueil
      </Link>
      <Link to='About' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer`}>
        A propos
      </Link>
      <Link to='Article' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer`}>
        Article
      </Link>
      <Link to='Contact' activeClass='font-bold border-b-2 border-[var(--color-start)]' spy={true} smooth={true} className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer`}>
        Contact
      </Link>
      {user ? <LinkRouter to='/articla' className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer border-2! border-gradient hover:border-white/45 background-gradient p-2 `}>
                Mon espace
              </LinkRouter> 
            : <LinkRouter to='/auth/login' className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer border-2! border-gradient hover:border-white/45 background-gradient p-2 `}>
                Se connecter
              </LinkRouter>
      }
      <a href="/auth/login" className={`${!isHidden ? "hidden" : "block"} text-white cursor-pointer border-2! border-gradient hover:border-white/45 background-gradient p-2 `}>
        Se connecter
      </a>
    </>
  )
}

export default HeaderNav

