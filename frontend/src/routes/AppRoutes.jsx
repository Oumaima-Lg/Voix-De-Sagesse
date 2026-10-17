import React, { lazy, Suspense } from 'react'
import HomePage from '../pages/homePage/HomePage' 
import { Routes, Route,  } from 'react-router-dom';
import { AuthChecker, PublicRoute, ProtectedRoute } from './AuthGuard'
import { AdminRoute } from './AdminGuard'

// Pages other than the landing page are downloaded when they are first visited.
const LoginPage = lazy(() => import('../pages/loginPage/LoginPage'))
const RegisterPage = lazy(() => import('../pages/registerPage/RegisterPage'))
const EspacePage = lazy(() => import('../pages/espacePage/EspacePage'))
const ArticleTypeSelection = lazy(() => import('../pages/espacePage/ArticleTypeSelection'))
const CreateArticleForm = lazy(() => import('../pages/espacePage/CreateArticle'))
const ArticleFeed = lazy(() => import('../pages/espacePage/ArticleFeed'))
const ProfileAuthor = lazy(() => import('../pages/espacePage/ProfileAuthor'))
const SavedArticles = lazy(() => import('../pages/espacePage/SavedArticles'))
const MyPosts = lazy(() => import('../pages/espacePage/MyPosts'))
const NotFoundPage = lazy(() => import('./NotFoundPage'))
const AdminDashboard = lazy(() => import('../pages/adminPage/AdminDashboard'))


const AppRoutes = () => {
    return (
        <AuthChecker>
            <Suspense fallback={<div className='min-h-svh bg-[#171717]' />}>
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
            </Suspense>
        </AuthChecker>
    )
}
export default AppRoutes;