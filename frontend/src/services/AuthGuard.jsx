import { useSelector, useDispatch } from 'react-redux'
import { Navigate, useLocation } from 'react-router-dom'
import { removeUser } from '../Slices/UserSlice'
import { removeJwt } from '../Slices/JwtSlice'
import { useAuth } from '../services/useAuth'
import { useEffect } from 'react'


export const ProtectedRoute = ({ children }) => {
  const user = useSelector((state) => state.user)
  const location = useLocation()
  
  const isAuthenticated = user && user.id && localStorage.getItem('token')
  
  if (!isAuthenticated) {
    // ✅ Message différent selon le contexte
    let message = "Veuillez vous connecter pour accéder à cette fonctionnalité"
    
    // Si c'est une redirection depuis AuthChecker (session expirée)
    if (location.pathname.startsWith('/articla') || location.pathname.startsWith('/saved-articles')) {
      message = "Session expirée, veuillez vous reconnecter"
    }
    
    return <Navigate to="/auth/login" state={{ 
      from: location,
      message: message
    }} replace />
  }
  
  return children
}

export const PublicRoute = ({ children }) => {
  const user = useSelector((state) => state.user)
  
  const isAuthenticated = user && user.id && localStorage.getItem('token')
  
  if (isAuthenticated) {
    return <Navigate to="/articla" replace />
  }
  
  return children
}

// ✅ AuthChecker CORRIGÉ pour gérer tous les cas
export const AuthChecker = ({ children }) => {
  const user = useSelector((state) => state.user)
  const jwt = useSelector((state) => state.jwt)
  const dispatch = useDispatch()
  const { clearAuth } = useAuth() // Utiliser le hook useAuth
  const location = useLocation();
  
  const token = localStorage.getItem('token')
  const userData = localStorage.getItem('user')

  useEffect(() => {
    let needsRedirect = false;
    let redirectMessage = '';

    // ✅ Cas 1: Token supprimé mais utilisateur encore présent
    if (!token && userData) {
      console.log('AuthChecker: Token removed but user data exists - clearing user and redirecting');
      clearAuth('Token manquant, reconnexion nécessaire');
      redirectMessage = 'Session expirée, veuillez vous reconnecter';
      needsRedirect = true;
    }
    // ✅ Cas 2: Utilisateur supprimé mais token encore présent  
    else if (token && !userData) {
      console.log('AuthChecker: User removed but token exists - clearing token and redirecting');
      clearAuth('Données utilisateur manquantes, reconnexion nécessaire');
      redirectMessage = 'Session expirée, veuillez vous reconnecter';
      needsRedirect = true;
    }
    // ✅ Cas 3: Token et userData existent, mais pas d'utilisateur dans Redux
    else if (token && userData && !user?.id) {
      try {
        const parsedUser = JSON.parse(userData);
        console.log('AuthChecker: Restoring user to Redux from localStorage');
        dispatch({ type: 'user/setUser', payload: parsedUser });
        dispatch({ type: 'jwt/setJwt', payload: token }); // Assurer que le JWT est aussi dans Redux
      } catch (error) {
        console.error('AuthChecker: Error parsing user data:', error);
        clearAuth('Données corrompues, veuillez vous reconnecter');
        redirectMessage = 'Données corrompues, veuillez vous reconnecter';
        needsRedirect = true;
      }
    }
    // ✅ Cas 4: Utilisateur dans Redux mais pas de token (incohérence)
    else if (user?.id && !token) {
      console.log('AuthChecker: User in Redux but no token - clearing user');
      clearAuth('Incohérence: Utilisateur en Redux sans token');
      redirectMessage = 'Session expirée, veuillez vous reconnecter';
      needsRedirect = true;
    }
    // ✅ Cas 5: Token dans Redux mais pas dans localStorage (incohérence)
    else if (jwt && !token) {
      console.log('AuthChecker: JWT in Redux but not in localStorage - clearing JWT');
      clearAuth('Incohérence: JWT en Redux sans token dans localStorage');
      redirectMessage = 'Session expirée, veuillez vous reconnecter';
      needsRedirect = true;
    }

    if (needsRedirect && !location.pathname.startsWith('/auth/login')) {
      window.location.href = `/auth/login?message=${encodeURIComponent(redirectMessage)}&redirect=${encodeURIComponent(location.pathname)}`;
    }
  }, [token, userData, user?.id, jwt, dispatch, clearAuth, location.pathname]);
  
  return children
}
