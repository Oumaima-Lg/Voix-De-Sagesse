import React from 'react'
import HeaderArticla from './HeaderArticla'
import Home from './Home'
import Article from './Article'
import { useLocation } from 'react-router-dom'
import Profile from './Profile'


const Articla = () => {
    const location = useLocation()

    const renderContent = () => {
        switch(location.pathname) {
            case '/articla':
                return <Home />
            case '/articla/article':
                return <Article />
            case '/articla/nouveauArticle':
                return <nvArticle />
            case '/articla/profile':
                return <Profile />
            default:
                return <Home />
        }
    }
    
    return (
        <div className=''>
            <HeaderArticla />
            {renderContent()}
        </div>
    )
}

export default Articla
