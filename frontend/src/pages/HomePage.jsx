import React from 'react'
import Header from '../pages_details/homePage/Header'
import Accueil from '../pages_details/homePage/Accueil'
import About from '../pages_details/homePage/About'
import Article from '../pages_details/homePage/Article'
import Contact from '../pages_details/homePage/Contact'
import Fouter from '../pages_details/homePage/Fouter'


const HomePage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover relative'>
            <Header isHidden={false}  />
            <Accueil />
            <About />
            <Article />
            <Contact />
            <Fouter />
        </div>
    )
}

export default HomePage

