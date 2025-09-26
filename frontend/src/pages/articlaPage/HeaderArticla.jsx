import React, { useState } from 'react'

import arrow from '../../assets/arrow.png';
import { Link as LinkRouter, useNavigate } from "react-router-dom";
import vector from '../../assets/Vector.png';
import oumi from '../../assets/oumi.png';
import PermIdentityIcon from '@mui/icons-material/PermIdentity';
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder';
import LogoutIcon from '@mui/icons-material/Logout';
import { useDispatch, useSelector } from 'react-redux';
import { removeUser } from '../../Slices/UserSlice';
import MenuBar from "./MenuBar";
import profileAvatar from "../../assets/profileAvatar.jpg"
import { useAuth } from '../../services/useAuth';




const HeaderArticla = () => {
    const navigate = useNavigate();
    const [toggle, setToggle] = useState(false);
    const dispatch = useDispatch();
    const user = useSelector((state) => state.user);
    const { logout } = useAuth();

   
    const handleLogout = () => {
        setToggle(false); 
        logout('Vous avez été déconnecté avec succès');
    }

    const handleProfile = () => {
        navigate("/articla/profile");
        setToggle(!toggle);
    }

    const handleFavoris = () => {
        navigate("/saved-articles");
        setToggle(!toggle);
    }

    return (
        <div>
            <header className='pt-4 fixed z-50 right-0 left-0 top-0 bg-black/1 backdrop-blur-2xl'>
                <div className='flex justify-between items-center lg:p-[20px] p-[14px] mx-auto relative '>
                    <div className='flex items-center gap-2'>
                        <img src={arrow} alt="arrow img" className='lg:h-12 h-8.5' />
                        <LinkRouter to='/' >
                            <span className='text-gradient lg:text-4xl x:text-2xl text-xl leading-tight'>
                                Voix De Sagesse
                            </span>
                        </LinkRouter>
                    </div>
                    {/* <div>
                        <MenuBar />
                    </div> */}
                    <div className='text-white flex  items-center xs:gap-6 gap-4 bg-[#202020] pl-4 rounded-l-[8px] relative'>
                        <div>
                            <img src={vector} alt="" className='cursor-pointer hover:scale-110 transform-gpu'
                                onClick={() => setToggle(!toggle)}
                            />
                        </div>
                        <div className='sm:text-sm text-xs text-center'>{user.nom + " " + user.prenom}</div>
                        <div className=''>
                            <img src={user.profilePicture ?
                                (user.profilePicture.startsWith('http') ?
                                    user.profilePicture :
                                    `http://localhost:8080${user.profilePicture}` 
                                ) : profileAvatar} alt="" 
                                className="size-10 border-[#A09F87] object-cover"/>
                        </div>

                        <div className={`${toggle ? "flex" : "hidden"} absolute top-13 right-0 left-0 h-50 py-6 px-2 flex-col justify-between bg-gradient-to-br from-[#171717] from-54% to-[#4E3F59] border-3 border-[#202020] rounded-lg`}>
                            <div className='flex gap-2 items-center cursor-pointer hover:scale-105 transform-gpu hover:text-[#A09F87]'>
                                <div className='text-xl'>
                                    <PermIdentityIcon fontSize='inherit' />
                                </div>
                                <div className='text-md font-medium ' onClick={handleProfile}>Votre Profil</div>
                            </div>
                            <div className='flex gap-2 items-center  cursor-pointer hover:scale-105 transform-gpu hover:text-[#A09F87]'>
                                <div className='text-xl'>
                                    <BookmarkBorderIcon fontSize='inherit' />
                                </div>
                                <div className='text-md font-medium' onClick={handleFavoris}>Vos Favoris</div>
                            </div>
                            <div className='flex gap-2 items-center cursor-pointer  text-orange-600 hover:scale-105 transform-gpu hover:text-[#A09F87]'>
                                <div className='pl-1 text-xl' >
                                    <LogoutIcon fontSize='inherit' />
                                </div>
                                <div className='text-md xs:font-medium font-normal' onClick={handleLogout}>Déconnexion</div>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

        </div>
    )
}

export default HeaderArticla

