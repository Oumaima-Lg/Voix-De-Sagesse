import React from 'react'
import HomePage from './HomePage'
import LoginPage from './LoginPage'
import RegisterPage from './RegisterPage'
import ArticlaPage from './ArticlaPage'
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux'
import ArticleTypeSelection from '../pages_details/articlaPage/ArticleTypeSelection'
import CreateArticleForm from '../pages_details/articlaPage/CreateArticle'
import ArticleFeed from '../pages_details/articlaPage/ArticleFeed'


const AppRoutes = () => {
    const user = useSelector((state) => state.user);
    const location = useLocation()
    console.log(location.pathname)
    return (
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/auth/login" element={user ? <Navigate to="/articla" /> : <LoginPage />} />
            <Route path="/auth/register" element={user ? <Navigate to="/articla" /> : <RegisterPage />} />
            <Route path="/articla" element={<ArticlaPage />} />
            <Route path="/articla/favorite" element={<ArticlaPage />} />
            <Route path="/articla/article" element={<ArticleFeed />} />
            <Route path="/articla/nouveauArticle" element={<ArticleTypeSelection />} />
            <Route path="/articla/creerArticle" element={<CreateArticleForm />} />
            <Route path="/articla/profile" element={<ArticlaPage />} />
        </Routes>)
}
export default AppRoutes;