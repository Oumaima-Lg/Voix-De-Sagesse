import { useSelector, useDispatch } from 'react-redux'
import { Navigate, useLocation } from 'react-router-dom'
import { removeUser } from '../Slices/UserSlice'
import { removeJwt } from '../Slices/JwtSlice'

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
  
  const token = localStorage.getItem('token')
  const userData = localStorage.getItem('user')
  
  // ✅ Cas 1: Token supprimé mais utilisateur encore présent
  if (!token && userData && user?.id) {
    console.log('Token removed but user data exists - clearing user and redirecting')
    localStorage.removeItem('user')
    dispatch(removeUser())
    dispatch(removeJwt())
    
    // Redirection immédiate avec message approprié
    window.location.href = '/auth/login?message=' + encodeURIComponent('Session expirée, veuillez vous reconnecter')
    return null
  }
  
  // ✅ Cas 2: Utilisateur supprimé mais token encore présent  
  if (token && !userData && !user?.id) {
    console.log('User removed but token exists - clearing token and redirecting')
    localStorage.removeItem('token')
    dispatch(removeJwt())
    dispatch(removeUser())
    
    // Redirection avec message de session expirée
    window.location.href = '/auth/login?message=' + encodeURIComponent('Session expirée, veuillez vous reconnecter')
    return null
  }
  
  // ✅ Cas 3: Token existe, userData existe, mais pas d'utilisateur dans Redux
  if (token && userData && !user?.id) {
    try {
      const parsedUser = JSON.parse(userData)
      console.log('Restoring user to Redux from localStorage')
      dispatch({ type: 'user/setUser', payload: parsedUser })
      return children
    } catch (error) {
      console.error('Error parsing user data:', error)
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      dispatch(removeUser())
      dispatch(removeJwt())
      window.location.href = '/auth/login?message=' + encodeURIComponent('Données corrompues, veuillez vous reconnecter')
      return null
    }
  }
  
  // ✅ Cas 4: Utilisateur dans Redux mais pas de token
  if (user?.id && !token) {
    console.log('User in Redux but no token - clearing user')
    dispatch(removeUser())
    return children
  }
  
  // ✅ Cas 5: Token dans Redux mais pas dans localStorage
  if (jwt && !token) {
    console.log('JWT in Redux but not in localStorage - clearing JWT')
    dispatch(removeJwt())
    return children
  }
  
  // ✅ Cas 6: Données complètement incohérentes - tout nettoyer
  if ((token && userData && user?.id && !jwt) || 
      (!token && !userData && (user?.id || jwt))) {
    console.log('Inconsistent auth state - clearing everything')
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    dispatch(removeUser())
    dispatch(removeJwt())
    return children
  }
  
  return children
}