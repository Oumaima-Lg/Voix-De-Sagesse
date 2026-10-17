import React from 'react'
import HeaderEspace from './HeaderEspace'
import Home from './Home'
import Article from './Article'
import { useLocation } from 'react-router-dom'
import Profile from './Profile'


const Espace = () => {
    const location = useLocation()

    const renderContent = () => {
        switch(location.pathname) {
            case '/espace':
                return <Home />
            case '/espace/top-articles':
                return <Article />
            case '/espace/profil':
                return <Profile />
            default:
                return <Home />
        }
    }
    
    return (
        <div className=''>
            <HeaderEspace />
            {renderContent()}
        </div>
    )
}

export default Espace
