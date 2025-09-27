import React from 'react'
import Input from '../../Components/InputSearch'
import { IoArrowBackCircleOutline } from "react-icons/io5";
import { Link as LinkRouter } from "react-router-dom";
import LoaderPyramid from '../../Components/Pyramid'
import {Card2}  from '../../Components/CardArticle';
import  Card from '../../Components/CardArticle';
import Marquee from "react-fast-marquee";
import { Carousel } from '@mantine/carousel';




const Article = () => {
    return (
        <div className='flex flex-col gap-2 pt-30  mx-auto text-white'>
            <div>
                <Input />
            </div>
            {/* <div className='flex items-center justify-center px-10 py-6 cursor-pointer text-white size-10 mt-20 sm:mt-auto  ml-6 bg-gradient-to-r from-[#4C3163] from-0% via-[#05071B] via-50% to-[#4C3163] to-100% border-1 border-[#000000] rounded-lg shadow-lg'> */}
            <div className='flex items-center justify-center px-10 py-6 size-10 mt-20 sm:mt-auto  ml-6   '>
                <LinkRouter to='/articla'>
                    <IoArrowBackCircleOutline className='text-4xl text-white/40 cursor-pointer' />
                </LinkRouter>
            </div>
            <div className='flex flex-col gap-1 pt-10 '>
                <div className='flex sm:flex-row flex-col justify-center items-center lg:space-x-0 md:space-x-[-50px] sm:space-x-[-80px] self-center'>
                    <div className=''> <LoaderPyramid /> </div>
                    <div className='text-gradient sm:text-5xl text-3xl charm text-center leading-22'>Expériences Personnelles</div>
                    <div className=''> <LoaderPyramid /> </div>
                </div>
                <div>
                    <div className='lg:px-40 sm:px-20 px-0'>
                        <Card2 isToggled={true} />
                    </div>
                </div>
            </div>
            <div className='flex flex-col gap-1 pb-20 pt-10 '>
                <div className='flex sm:flex-row flex-col justify-center items-center lg:space-x-0 md:space-x-[-50px] sm:space-x-[-80px] self-center'>
                    <div> <LoaderPyramid /> </div>
                    <div className='text-gradient sm:text-5xl text-3xl charm text-center leading-22'>Sagesses Partagées</div>
                    <div> <LoaderPyramid /> </div>
                </div>
                <div className='lg:px-40 sm:px-20 px-0'>
                    <Card2 isToggled={false} />
                </div>
            </div>
        </div>

        
    )
}

export default Article
