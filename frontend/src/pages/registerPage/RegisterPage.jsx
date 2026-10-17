import React from 'react'
import Header from '../homePage/Header'
import Register from './Register'
import Fouter from '../homePage/Fouter'

const RegisterPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover min-h-svh flex flex-col'>
            <Header isHidden={true} />
            <Register />
            <Fouter></Fouter>
        </div>
    )
}

export default RegisterPage



