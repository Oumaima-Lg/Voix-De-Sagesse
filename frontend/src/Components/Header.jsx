import React, { useEffect, useState } from "react";
import arrow from '../assets/arrow.png';
import HeaderNav from '../Semi_Components/HeaderNav';
import menu from '../assets/menu.png';
import close from '../assets/close.png';

const Header = () => {
    const [toggle, setToggle] = useState(false);
    return (
        <header className='pt-4'>
            <div className='flex justify-between items-center sm:container lg:p-[20px] p-[14px] mx-auto relative'>
                <div className='flex items-center gap-2'>
                    <img src={arrow} alt="arrow img" className='lg:h-12 h-8.5' />
                    <span className='text-gradient lg:text-4xl text-2xl leading-tight'>
                        Voix De Sagesse
                    </span>
                </div>
                <div className='lg:flex items-center hidden'>
                    <nav className='flex gap-14 items-center'>
                        <HeaderNav />
                    </nav>
                </div>
                <div className='lg:hidden '>
                    <img
                        src={toggle ? close : menu}
                        alt='menu'
                        className='w-[40px] h-[40px] object-contain'
                        onClick={() => setToggle(!toggle)}
                    />
                    <div className={`${toggle ? "flex" : "hidden" } bg-linear-to-b from-[var(--color-start)]/45 to-[var(--color-end)]/80 absolute top-16 right-5 py-6 px-8 flex-col items-center rounded-xl gap-y-3 text-2xl`} >
                        <HeaderNav />
                    </div>
                </div>
            </div>
        </header>
    )
}

export default Header

