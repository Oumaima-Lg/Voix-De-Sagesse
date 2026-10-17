import React from 'react'
import HomePage from '../pages/homePage/HomePage' 
import LoginPage from '../pages/loginPage/LoginPage'
import RegisterPage from '../pages/registerPage/RegisterPage'
import ArticlaPage from '../pages/articlaPage/ArticlaPage'
import { Routes, Route,  } from 'react-router-dom';
import ArticleTypeSelection from '../pages/articlaPage/ArticleTypeSelection'
import CreateArticleForm from '../pages/articlaPage/CreateArticle'
import ArticleFeed from '../pages/articlaPage/ArticleFeed'
import ProfileAuthor from '../pages/articlaPage/ProfileAuthor'
import SavedArticles from '../pages/articlaPage/SavedArticles'
import MyPosts from '../pages/articlaPage/MyPosts'
import NotFoundPage from './NotFoundPage'
import { AuthChecker, PublicRoute, ProtectedRoute } from '../services/AuthGuard'
import { AdminRoute } from '../services/AdminGuard'
import AdminDashboard from '../pages/adminPage/AdminDashboard'


const AppRoutes = () => {
    return (
        <AuthChecker>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/auth/login" element={
                    <PublicRoute>
                        <LoginPage />
                    </PublicRoute>
                } />
                <Route path="/auth/register" element={
                    <PublicRoute>
                        <RegisterPage />
                    </PublicRoute>
                } />
                <Route path="/articla" element={
                    <ProtectedRoute>
                        <ArticlaPage />
                    </ProtectedRoute>
                } />
                <Route path="/articla/article" element={
                    <ProtectedRoute>
                        <ArticleFeed />
                    </ProtectedRoute>
                } />
                <Route path="/articla/my-posts" element={
                    <ProtectedRoute>
                        <MyPosts />
                    </ProtectedRoute>
                } />
                <Route path="/saved-articles" element={
                    <ProtectedRoute>
                        <SavedArticles />
                    </ProtectedRoute>
                } />
                <Route path="/articla/creerArticle" element={
                    <ProtectedRoute>
                        <CreateArticleForm />
                    </ProtectedRoute>
                } />
                 <Route path="/articla/nouveauArticle" element={
                    <ProtectedRoute>
                        <ArticleTypeSelection />
                    </ProtectedRoute>
                } />
                <Route path="/articla/poste/profile/:id" element={
                    <ProtectedRoute>
                        <ProfileAuthor />
                    </ProtectedRoute>
                } />
                <Route path="/articla/topArticla" element={
                    <ProtectedRoute>
                        <ArticlaPage />
                    </ProtectedRoute>
                } />
                <Route path="/articla/profile" element={
                    <ProtectedRoute>
                        <ArticlaPage />
                    </ProtectedRoute>
                } />
                <Route path="/admin/dashboard" element={
                    <AdminRoute>
                        <AdminDashboard />
                    </AdminRoute>
                } />
                {/* ✅ Route catch-all pour 404 - DOIT ÊTRE EN DERNIER */}
                <Route path="*" element={<NotFoundPage />} />

                {/* ✅ Redirection par défaut */}

                {/* ✅ Route 404 */}
            </Routes>
        </AuthChecker>
    )
}
export default AppRoutes;