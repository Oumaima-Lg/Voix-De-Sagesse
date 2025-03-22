import React from 'react'
import arrow from '../assets/arrow.png';
import { Link } from 'react-scroll';

const Header = () => {
    return (
        <header className='pt-4'>
            <div className='flex justify-between items-center container p-[20px] mx-auto'>
                <div className='flex items-center gap-2'>
                    <img src={arrow} alt="arrow img" className='h-12' />
                    <span className='text-gradient text-4xl leading-tight'>
                        Voix De Sagesse
                    </span>
                </div>
                <div className='flex items-center'>
                    <nav className='flex gap-14 items-center'> 
                        <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer font-bold '>
                            Accueil
                        </Link>
                        <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer'>
                            A propos
                        </Link>
                        <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer'>
                            Article
                        </Link>
                        <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer'>
                            Contact
                        </Link>
                        <Link to='Accueil' spy={true} smooth={true} className='text-white cursor-pointer'>
                            Se connecter
                        </Link>                       
                    </nav>
                </div>
            </div>
        </header>
    )
}

export default Header
