import React from 'react'
import Header from '../homePage/Header'
import Accueil from '../homePage/Accueil'
import About from '../homePage/About'
import Article from '../homePage/Article'
import Contact from '../homePage/Contact'
import Fouter from '../homePage/Fouter'


const HomePage = () => {
    return (
        <div className='bg-[url(/src/assets/diego.jpg)] bg-cover relative '>
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

