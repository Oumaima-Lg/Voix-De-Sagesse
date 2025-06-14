import React from 'react'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ArticlaPage from './pages/ArticlaPage'
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const App = () => {
  return (
    <BrowserRouter>
        <Routes>
          <Route path="*" element={<HomePage />} />
          <Route path="/auth/login" element={<LoginPage />} />
          <Route path="/auth/register" element={<RegisterPage />} />
          <Route path="/articla" element={<ArticlaPage />} />
          <Route path="/articla/article" element={<ArticlaPage />} />
          <Route path="/articla/nouveauArticle" element={<ArticlaPage />} />
        </Routes>
      </BrowserRouter>
    
  )
}

export default App
