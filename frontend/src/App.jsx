import React from 'react'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const App = () => {
  return (
    <BrowserRouter>
        <Routes>
          <Route path="*" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </BrowserRouter>
    
  )
}

export default App
