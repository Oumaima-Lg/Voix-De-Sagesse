import React from 'react'
import Header from '../homePage/Header'
import Login from './Login'
import Fouter from '../homePage/Fouter'

const LoginPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover min-h-screen relative'>
            <Header isHidden={true} />
            <Login />
            <Fouter></Fouter>
        </div>
    )
}

export default LoginPage



