import React from 'react'
import Header from './Components/Header'
import Accueil from './Components/Accueil'
import About from './Components/About'
import Article from './Components/Article'
import Contact from './Components/Contact'
import Fouter from './Components/Fouter'

const App = () => {
  return (
    <div className='bg-[url(/src/assets/diego.jpg)] bg-cover '>
      <Header />
      <Accueil />
      <About />
      <Article />
      <Contact />
      <Fouter />
    </div>
    
  )
}

export default App
