import React from 'react'
import HeaderArticla from './HeaderArticla'
import Home from './Home'
import Article from './Article'
import { useLocation } from 'react-router-dom'
import Profile from './Profile'
import ProfileAuthor from './ProfileAuthor'


const Articla = () => {
    const location = useLocation()

    const renderContent = () => {
        switch(location.pathname) {
            case '/articla':
                return <Home />
            case '/articla/topArticla':
                return <Article />
            case '/articla/profile':
                return <Profile />
            case '/articla/poste/profile/:id':
                return <ProfileAuthor />
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
