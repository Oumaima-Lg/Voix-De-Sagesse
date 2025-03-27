import React from 'react'
import Header from './Components/Header'
import Accueil from './Components/Accueil'
import About from './Components/About'
import Article from './Components/Article'

const App = () => {
  return (
    <div className='bg-[url(/src/assets/diego.jpg)] bg-cover h-[5000px]'>
      <Header />
      <Accueil />
      <About />
      <Article />
    </div>
    
  )
}

export default App
