import React from 'react'
import Input from '../../semiComponents/InputSearch'
import { IoArrowBackCircleOutline } from "react-icons/io5";
import { Link as LinkRouter } from "react-router-dom";
import LoaderPyramid from '../../semiComponents/Pyramid'
import Card from '../../semiComponents/CardArticle';



const Article = () => {
  return (
    <div className='flex flex-col gap-2 pt-30  mx-auto text-white'>
        <div>
             <Input />
        </div>
        <div className='flex items-center justify-center px-10 py-6 cursor-pointer text-white size-10  ml-6 bg-gradient-to-r from-[#4C3163] from-0% via-[#05071B] via-50% to-[#4C3163] to-100% border-1 border-[#000000] rounded-lg shadow-lg'>
           <LinkRouter to='/articla'>
                <IoArrowBackCircleOutline className='text-4xl' />
            </LinkRouter> 
        </div>
        <div className='flex flex-col gap-10 pt-10 '>
            <div className='flex items-center gap-1 self-center'>
                <div> <LoaderPyramid/> </div>
                <div className='text-gradient text-5xl charm'>Histoires Personnelles</div>
                <div> <LoaderPyramid/> </div>
            </div>
            <div>
                <div className='flex px-40'>
                    <Card isToggled={true} />
                </div>
            </div>
        </div>
        <div className='flex flex-col gap-10 py-40 '>
           <div className='flex items-center gap-1 self-center'>
                <div> <LoaderPyramid/> </div>
                <div className='text-gradient text-5xl charm'>Sagesse Partagée</div>
                <div> <LoaderPyramid/> </div>
            </div>
            <div>
                <div className='flex px-40'>
                    <Card isToggled={false} />
                </div>
            </div>
        </div>
    </div>
  )
}

export default Article
