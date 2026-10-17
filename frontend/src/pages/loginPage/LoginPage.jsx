import React from 'react'
import Header from '../homePage/Header'
import Login from './Login'
import Footer from '../homePage/Footer'

const LoginPage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover min-h-svh flex flex-col'>
            <Header isHidden={true} />
            <Login />
            <Footer></Footer>
        </div>
    )
}

export default LoginPage



