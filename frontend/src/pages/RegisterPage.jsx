import React from 'react'
import Header from '../homePage/Header'
import Register from '../registerPage/Register'
import Fouter from '../homePage/Fouter'

const RegisterPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover h-screen'>
            <Header isHidden={true} />
            <Register />
            <Fouter></Fouter>
        </div>
    )
}

export default RegisterPage



