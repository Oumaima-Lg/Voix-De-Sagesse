import React from 'react'

import Loader from '../../semiComponents/Loader'
import fireHuman from '../../assets/fireHuman.png'
import Cube from '../../semiComponents/Cube'
import Article from '../../assets/Article.png'
import nvArticle from '../../assets/nvArticle.png'
import articlePerso from '../../assets/articlePerso.png'
import { PiArticleLight } from "react-icons/pi";
import { IoCreateOutline } from "react-icons/io5";
import { PiArchiveLight } from "react-icons/pi";
import ShinyText from '../../blocks/TextAnimations/ShinyText/ShinyText';
import { Link as LinkRouter } from "react-router-dom";

import Spline from '@splinetool/react-spline';

export function AppSpline() {
  return (
    <Spline scene="https://prod.spline.design/VbFDnYNrBVOXKv9u/scene.splinecode" />
  );
}




const Home = () => {
    return (

        <div className='flex mx-auto lg:pt-34 pt-22 min-h-screen bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%'>
            <div className=' w-xs pt-12 '>
                <div className=' border-x-3 border-b-3 px-4  border-[#202020]  md:h-3/4 h-2/3 md:w-3/4 w-2/3 bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100%  rounded-lg shadow-lg    text-white flex flex-col justify-between  py-20 '>

                    <div className='flex flex-row items-center gap-3 cursor-pointer hover:text-[#A09F87] hover:scale-105 transform-gpu' >
                        <div>
                            <PiArticleLight className='md:text-5xl text-2xl' />
                        </div>
                        <LinkRouter to = '/articla/article' className='font-bold md:text-xl text-sm'>
                            Article
                        </LinkRouter>
                    </div>
                    <div className='flex flex-row items-center gap-3 cursor-pointer  hover:text-[#A09F87] hover:scale-105 transform-gpu' >
                        <div>
                            <IoCreateOutline className='md:text-5xl text-2xl' />
                        </div>
                        <div className='font-bold md:text-xl text-sm'>
                            Nouveau <br /> Article
                        </div>
                    </div>
                    <div className='flex flex-row items-center gap-3 cursor-pointer  hover:text-[#A09F87] hover:scale-105 transform-gpu' >
                        <div>
                            <PiArchiveLight className='md:text-5xl text-2xl' />
                        </div>
                        <div className='font-bold md:text-xl text-sm'>
                            Vos articles <br /> personnels
                        </div>
                    </div>
                </div>
            </div>
            <div className='  w-5/6 flex flex-col  gap-6 lg:gap-10 '>
                <div className='pt-5 text-center  text-gradient charm xl:text-6xl text-3xl leading-tight '>
                    {/* BIENVENUE OUMAIMA LAGHJIBI */}
                    <ShinyText text=" BIENVENUE OUMAIMA LAGHJIBI" disabled={false} speed={3} className='' />
                </div>
                <div className='flex lg:flex-row flex-col justify-between size-full'>
                    <div className='flex flex-col lg:items-end lg:justify-end justify-center items-center  '>
                        <img src={fireHuman} alt="" className='lg:h-6/7  lg:w-auto size-100 ' />
                    </div>
                    <div className='flex-1 flex flex-row justify-between items-center gap-0 pr-4 '>
                        <div className='flex flex-col justify-center items-center lg:gap-10 gap-0 '>
                            <div className='flex 2xl:space-x-[-50px] gap-2'>
                                <div className='bg-[#4C3163] size-6 self-end origin-center rotate-24 '></div>
                                <div className='text-center text-wrap xl:w-xl  text-[#A09F87] charm xl:text-4xl text-2xl font-bold leading-relaxed mx-auto pr-6'>
                                    Dans cette vie, chacun porte ses imperfections, et c'est ce qui fait de nous des êtres humains
                                </div>
                                <div className='bg-[#4C3163] size-6 self-start origin-center rotate-24 '></div>
                            </div>
                            <div>
                                <Loader />
                            </div>
                        </div>
                        <div className='hidden 2xl:block h-130 flex-1  overflow-hidden '>
                            <AppSpline />
                        </div>
                    </div>
                </div>
            </div>
        </div>


    )
}

export default Home
