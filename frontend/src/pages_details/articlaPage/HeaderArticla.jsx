import React from 'react'

import arrow from '../../assets/arrow.png';
import { Link as LinkRouter } from "react-router-dom";

const HeaderArticla = () => {
    return (
        <div>
            <header className='pt-4 bg-black/1 z-30 backdrop-blur-2xl'>
                <div className='flex justify-between items-center lg:p-[20px] p-[14px] mx-auto relative '>
                    <div className='flex items-center gap-2'>
                        <img src={arrow} alt="arrow img" className='lg:h-12 h-8.5' />
                        <LinkRouter to='/' >
                            <span className='text-gradient lg:text-4xl text-2xl leading-tight'>
                                Voix De Sagesse
                            </span>
                        </LinkRouter>
                    </div>
                </div>
            </header>

        </div>
    )
}

export default HeaderArticla
