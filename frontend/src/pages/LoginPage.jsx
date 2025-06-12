import React from 'react'
import Header from '../pages_details/homePage/Header'
import Login from '../pages_details/loginPage/Login'
import Fouter from '../pages_details/homePage/Fouter'

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



