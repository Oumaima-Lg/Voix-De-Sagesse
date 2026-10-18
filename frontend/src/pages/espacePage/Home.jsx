import React from 'react'

import Loader from '../../components/Loader'
import fireHuman from '../../assets/fireHuman.webp'
import { PiArticleLight, PiStarLight, PiArchiveLight } from "react-icons/pi";
import { IoCreateOutline } from "react-icons/io5";
import ShinyText from '../../blocks/TextAnimations/ShinyText/ShinyText';
import { Link as LinkRouter } from "react-router-dom";
import { BsLayoutSidebarInset } from "react-icons/bs";
import { useDisclosure } from '@mantine/hooks';
import { Drawer } from '@mantine/core';
import { IconXboxX } from '@tabler/icons-react';


import SplineScene from '../../components/SplineScene';
import { useSelector } from 'react-redux'


// Links of the logged-in area, shown in the sidebar (desktop) and in the drawer (phones).
const MENU = [
    { to: '/espace/fil', label: 'Article', Icon: PiArticleLight },
    { to: '/espace/publier', label: 'Nouvel article', Icon: IoCreateOutline },
    { to: '/espace/mes-publications', label: 'Vos articles personnels', Icon: PiArchiveLight },
    { to: '/espace/top-articles', label: 'Top articles', Icon: PiStarLight },
]

const MenuLinks = ({ iconClassName, textClassName }) => (
    <>
        {MENU.map(({ to, label, Icon }) => (
            <LinkRouter key={to} to={to} className='flex flex-row items-center gap-3 hover:text-[#A09F87] hover:scale-105 transform-gpu'>
                <Icon className={`${iconClassName} shrink-0`} />
                <span className={`font-medium ${textClassName}`}>{label}</span>
            </LinkRouter>
        ))}
    </>
)

const Home = () => {
    const [opened, { open, close }] = useDisclosure(false);
    const user = useSelector((state) => state.user);

    return (

        <div className='flex sm:flex-row flex-col   mx-auto lg:pt-26 pt-22 min-h-screen lg:h-svh bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100%'>
            <div className=' w-xs pt-12 sm:block hidden'>
                <div className=' border-x-3 border-b-3 px-4  border-[#202020]  md:h-3/4 h-2/3 md:w-3/4 w-2/3 bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100%  rounded-lg shadow-lg text-white/85 flex flex-col justify-center gap-6 lg:gap-8 py-8'>
                    <MenuLinks iconClassName='md:text-3xl text-2xl' textClassName='md:text-base text-sm' />
                </div>
            </div>

            <div onClick={open} className='sm:hidden fixed bottom-0 z-20 flex items-center justify-center rounded-se-full bg-gradient-to-tr from-[var(--color-end)] from-20%  to-[var(--color-start)] to-100%  p-2 pt-6 pr-8 cursor-pointer  inset-shadow-sm inset-shadow-white/40' >
                <div className='text-3xl text-[var(--color-start)] ' >
                    <BsLayoutSidebarInset />
                </div>
            </div>


            <Drawer
                opened={opened}
                onClose={close}
                size="xs"
                transitionProps={{
                    transition: 'rotate-left',
                    duration: 150,
                    timingFunction: 'linear',
                }}

                withCloseButton={true}
                closeButtonProps={{
                    icon: <IconXboxX size={30} stroke={1.7} color='white' />,
                }}

                className='sm:hidden'

                styles={{
                    content: { background: 'linear-gradient(135deg, #171717 54%, #4E3F59 100%)', borderRadius: '0.5rem', },
                    header: { background: 'linear-gradient(135deg, #171717 100%, #4E3F59 100%)', borderRadius: '0.5rem', borderLeft: '3px solid #202020', borderRight: '3px solid #202020', borderTop: '3px solid #202020', },

                }}
            >
                <div className="border-x-3 absolute bottom-0 left-0 border-b-3 px-6 size-full border-[#202020] bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] to-100% rounded-lg shadow-lg text-white/85 flex flex-col justify-center gap-10 items-start">
                    <MenuLinks iconClassName='text-3xl' textClassName='text-lg' />
                </div>
            </Drawer>

            <div className='w-full pt-10 sm:pt-0  sm:w-5/6 flex flex-col  gap-6 lg:gap-8 px-5'>
                <div className='pt-5 text-center  text-gradient charm xl:text-6xl text-3xl leading-tight '>
                    <ShinyText text={"BIENVENUE " + user.nom.toUpperCase() + " " + user.prenom.toUpperCase()} disabled={false} speed={3} className='' />
                </div>
                <div className='flex lg:flex-row flex-col justify-between size-full lg:h-auto lg:flex-1 lg:min-h-0'>
                    <div className='flex flex-col lg:items-end lg:justify-end justify-center items-center lg:shrink-0'>
                        <img src={fireHuman} alt="" className='lg:h-full lg:w-auto size-100 object-contain' />
                    </div>
                    <div className='flex-1 flex flex-row justify-between items-center gap-0 pr-4 '>
                        <div className='flex flex-col justify-center items-center lg:gap-6 gap-0 '>
                            <div className='flex 2xl:space-x-[-50px] gap-2'>
                                <div className='bg-[#4C3163] size-6 self-end origin-center rotate-24 '></div>
                                <div className='text-center text-wrap xl:w-xl  text-[#A09F87] charm xl:text-4xl text-2xl font-medium leading-relaxed mx-auto pr-6'>
                                    Dans cette vie, chacun porte ses imperfections, et c'est ce qui fait de nous des êtres humains
                                </div>
                                <div className='bg-[#4C3163] size-6 self-start origin-center rotate-24 '></div>
                            </div>
                            <div>
                                <Loader />
                            </div>
                        </div>
                        <div className='hidden 2xl:block h-full flex-1  overflow-hidden '>
                            <SplineScene scene="https://prod.spline.design/VbFDnYNrBVOXKv9u/scene.splinecode" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Home
