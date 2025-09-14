import React from 'react'
import HomePage from '../homePage/HomePage'
import LoginPage from '../loginPage/LoginPage'
import RegisterPage from '../registerPage/RegisterPage'
import ArticlaPage from '../articlaPage/ArticlaPage'
import { Routes, Route,  } from 'react-router-dom';
import { useSelector } from 'react-redux'
import ArticleTypeSelection from '../articlaPage/ArticleTypeSelection'
import CreateArticleForm from '../articlaPage/CreateArticle'
import ArticleFeed from '../articlaPage/ArticleFeed'
import ProfileAuthor from '../articlaPage/ProfileAuthor'
import SavedArticles from '../articlaPage/SavedArticles'
import MyPosts from '../articlaPage/MyPosts'
import NotFoundPage from './NotFoundPage'
import { AuthChecker, PublicRoute, ProtectedRoute } from '../../services/AuthGuard'


const AppRoutes = () => {
    const user = useSelector((state) => state.user);
    // const location = useLocation()
    // console.log(location.pathname)
    return (
        <AuthChecker>
            <Routes>
                <Route path="/" element={<HomePage />} />
                {/* <Route path="/auth/login" element={user ? <Navigate to="/articla" /> : <LoginPage />} /> */}
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
                {/* <Route path="/auth/register" element={user ? <Navigate to="/articla" /> : <RegisterPage />} /> */}
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
                {/* <Route path="/articla" element={<ArticlaPage />} /> */}
                {/* <Route path="/articla/topArticla" element={<ArticlaPage />} /> */}
                {/* <Route path="/articla/article" element={<ArticleFeed />} /> */}
                {/* <Route path="/articla/nouveauArticle" element={<ArticleTypeSelection />} /> */}
                {/* <Route path="/articla/creerArticle" element={<CreateArticleForm />} /> */}
                {/* <Route path="/articla/profile" element={<ArticlaPage />} /> */}
                {/* <Route path="/articla/poste/profile/:id" element={<ProfileAuthor />} /> */}
                {/* <Route path="/saved-articles" element={<SavedArticles />} /> */}
                {/* <Route path="/articla/my-posts" element={<MyPosts />} /> */}
                {/* ✅ Route catch-all pour 404 - DOIT ÊTRE EN DERNIER */}
                <Route path="*" element={<NotFoundPage />} />

                {/* ✅ Redirection par défaut */}
                {/* <Route path="/" element={<Navigate to="/articla" replace />} /> */}

                {/* ✅ Route 404 */}
                {/* <Route path="*" element={
                    <ProtectedRoute>
                        <NotFound />
                    </ProtectedRoute>
                } /> */}
            </Routes>
        </AuthChecker>
    )
}
export default AppRoutes;