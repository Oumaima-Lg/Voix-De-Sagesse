import React from 'react'
import Header from '../homePage/Header'
import Register from './Register'
import Footer from '../homePage/Footer'

const RegisterPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.webp)] bg-cover min-h-svh flex flex-col'>
            <Header isHidden={true} />
            <Register />
            <Footer></Footer>
        </div>
    )
}

export default RegisterPage



