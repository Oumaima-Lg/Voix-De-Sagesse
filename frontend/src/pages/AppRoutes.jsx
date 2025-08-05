import React from 'react'
import HomePage from './HomePage'
import LoginPage from './LoginPage'
import RegisterPage from './RegisterPage'
import ArticlaPage from './ArticlaPage'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux'


const AppRoutes = () => {
    const user = useSelector((state) => state.user);
    return <BrowserRouter>
        <Routes>
            <Route path="*" element={<HomePage />} />
            <Route path="/auth/login" element={user ? <Navigate to="/" /> : <LoginPage />} />
            <Route path="/auth/register" element={user ? <Navigate to="/" /> : <RegisterPage />} />
            <Route path="/articla" element={<ArticlaPage />} />
            <Route path="/articla/article" element={<ArticlaPage />} />
            <Route path="/articla/nouveauArticle" element={<ArticlaPage />} />
            <Route path="/articla/profile" element={<ArticlaPage />} />
        </Routes>
    </BrowserRouter>
}
export default AppRoutes;