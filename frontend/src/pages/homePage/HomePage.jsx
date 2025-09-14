import React from 'react'
import Header from './Header'
import Accueil from './Accueil'
import About from './About'
import Article from './Article'
import Contact from './Contact'
import Fouter from './Fouter'


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

