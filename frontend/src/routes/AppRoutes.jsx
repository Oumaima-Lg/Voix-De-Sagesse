import React from 'react'
import HomePage from '../pages/homePage/HomePage' 
import LoginPage from '../pages/loginPage/LoginPage'
import RegisterPage from '../pages/registerPage/RegisterPage'
import EspacePage from '../pages/espacePage/EspacePage'
import { Routes, Route,  } from 'react-router-dom';
import ArticleTypeSelection from '../pages/espacePage/ArticleTypeSelection'
import CreateArticleForm from '../pages/espacePage/CreateArticle'
import ArticleFeed from '../pages/espacePage/ArticleFeed'
import ProfileAuthor from '../pages/espacePage/ProfileAuthor'
import SavedArticles from '../pages/espacePage/SavedArticles'
import MyPosts from '../pages/espacePage/MyPosts'
import NotFoundPage from './NotFoundPage'
import { AuthChecker, PublicRoute, ProtectedRoute } from './AuthGuard'
import { AdminRoute } from './AdminGuard'
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
                <Route path="/espace" element={
                    <ProtectedRoute>
                        <EspacePage />
                    </ProtectedRoute>
                } />
                <Route path="/espace/fil" element={
                    <ProtectedRoute>
                        <ArticleFeed />
                    </ProtectedRoute>
                } />
                <Route path="/espace/mes-publications" element={
                    <ProtectedRoute>
                        <MyPosts />
                    </ProtectedRoute>
                } />
                <Route path="/espace/favoris" element={
                    <ProtectedRoute>
                        <SavedArticles />
                    </ProtectedRoute>
                } />
                <Route path="/espace/publier/formulaire" element={
                    <ProtectedRoute>
                        <CreateArticleForm />
                    </ProtectedRoute>
                } />
                 <Route path="/espace/publier" element={
                    <ProtectedRoute>
                        <ArticleTypeSelection />
                    </ProtectedRoute>
                } />
                <Route path="/espace/auteur/:id" element={
                    <ProtectedRoute>
                        <ProfileAuthor />
                    </ProtectedRoute>
                } />
                <Route path="/espace/top-articles" element={
                    <ProtectedRoute>
                        <EspacePage />
                    </ProtectedRoute>
                } />
                <Route path="/espace/profil" element={
                    <ProtectedRoute>
                        <EspacePage />
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