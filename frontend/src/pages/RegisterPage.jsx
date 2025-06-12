import React from 'react'
import Header from '../pages_details/homePage/Header'
import Register from '../pages_details/registerPage/Register'
import Fouter from '../pages_details/homePage/Fouter'

const RegisterPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover min-h-screen relative'>
            <Header isHidden={true} />
            <Register />
            <Fouter></Fouter>
        </div>
    )
}

export default RegisterPage



