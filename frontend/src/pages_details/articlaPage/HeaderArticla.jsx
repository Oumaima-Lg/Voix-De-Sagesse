import React, {useState} from 'react'

import arrow from '../../assets/arrow.png';
import { Link as LinkRouter } from "react-router-dom";
import vector from '../../assets/vector.png';
import oumi from '../../assets/oumi.png';
import PermIdentityIcon from '@mui/icons-material/PermIdentity';
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder';
import LogoutIcon from '@mui/icons-material/Logout';



const HeaderArticla = () => {
     const [toggle, setToggle] = useState(false);
    return (
        <div>
            <header className='pt-4 fixed z-50 right-0 left-0 top-0 bg-black/1 backdrop-blur-2xl'>
                <div className='flex justify-between items-center lg:p-[20px] p-[14px] mx-auto relative '>
                    <div className='flex items-center gap-2'>
                        <img src={arrow} alt="arrow img" className='lg:h-12 h-8.5' />
                        <LinkRouter to='/' >
                            <span className='text-gradient lg:text-4xl text-2xl leading-tight'>
                                Voix De Sagesse
                            </span>
                        </LinkRouter>
                    </div>
                    <div className='text-white flex  items-center sm:gap-4 gap-2 bg-[#202020] pl-4 rounded-l-[8px] relative'>
                        <div>
                            <img src={vector} alt="" className='cursor-pointer hover:scale-110 transform-gpu'
                                onClick={() => setToggle(!toggle)}
                             />
                        </div>
                        <div className='sm:text-sm text-xs text-center'>Oumaima laghjibi</div>
                        <div className=''>
                            <img src={oumi} alt="" />
                        </div>

                        <div className={`${ toggle ? "flex" : "hidden"} absolute top-13 right-0 left-0 h-50 py-6 px-2 flex-col justify-between bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] border-3 border-[#202020] rounded-lg`}>
                                <div className='flex gap-2 items-center cursor-pointer hover:scale-105 transform-gpu hover:text-[#A09F87]'>
                                    <div>
                                        <PermIdentityIcon fontSize='large'  />
                                    </div>
                                    <div className='text-md font-medium'>Votre Profil</div>
                                </div>
                                <div className='flex gap-2 items-center cursor-pointer hover:scale-105 transform-gpu hover:text-[#A09F87]'>
                                    <div>
                                        <BookmarkBorderIcon fontSize='large'  />
                                    </div>
                                    <div className='text-md font-medium'>Vos Favoris</div>
                                </div>
                                <div className='flex gap-2 items-center cursor-pointer hover:scale-105 transform-gpu hover:text-[#A09F87]'>
                                    <div>
                                        <LogoutIcon fontSize='large'  />
                                    </div>
                                    <div className='text-md font-medium'>Se Déconnecter</div>
                                </div>                               
                        </div>
                    </div>
                </div>
            </header>

        </div>
    )
}

export default HeaderArticla
