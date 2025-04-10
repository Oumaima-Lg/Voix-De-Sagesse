import React from 'react'
import Header from '../homePage/Header'
import Login from '../loginPage/Login'
import Fouter from '../homePage/Fouter'

const LoginPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover h-screen'>
            <Header isHidden={true} />
            <Login />
            <Fouter></Fouter>
        </div>
    )
}

export default LoginPage



