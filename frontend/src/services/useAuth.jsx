// hooks/useAuth.js
import { useSelector, useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { removeUser } from '../Slices/UserSlice'
import { removeJwt } from '../Slices/JwtSlice'

export const useAuth = () => {
  const user = useSelector((state) => state.user)
  const jwt = useSelector((state) => state.jwt)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  
  const isAuthenticated = () => {
    const token = localStorage.getItem('token')
    const userData = localStorage.getItem('user')
    return !!(user?.id && token && userData)
  }
  
  // ✅ Fonction de déconnexion avec message personnalisé
  const logout = (customMessage = 'Vous avez été déconnecté avec succès') => {
    console.log('Logout initiated with message:', customMessage)
    
    // Nettoyer toutes les données d'authentification
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    dispatch(removeUser())
    dispatch(removeJwt())
    
    // Naviguer avec le message personnalisé
    navigate(`/auth/login?message=${encodeURIComponent(customMessage)}`, { 
      replace: true,
      state: { 
        message: customMessage,
        fromLogout: true // ✅ Indicateur que c'est une déconnexion volontaire
      }
    })
  }
  
  // ✅ Fonction pour nettoyer l'authentification (pour les erreurs système)
  const clearAuth = (reason = 'Session expirée, veuillez vous reconnecter') => {
    console.log('ClearAuth initiated:', reason)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    dispatch(removeUser())
    dispatch(removeJwt())
  }
  
  const validateAuth = () => {
    const token = localStorage.getItem('token')
    const userData = localStorage.getItem('user')
    
    if (!token && userData && user?.id) {
      clearAuth('Token manquant, veuillez vous reconnecter')
      return false
    }
    
    if (token && !userData && !user?.id) {
      clearAuth('Données utilisateur manquantes, veuillez vous reconnecter')
      return false
    }
    
    if (token && userData && !user?.id) {
      try {
        const parsedUser = JSON.parse(userData)
        dispatch({ type: 'user/setUser', payload: parsedUser })
        return true
      } catch (error) {
        clearAuth('Données corrompues, veuillez vous reconnecter')
        return false
      }
    }
    
    return isAuthenticated()
  }
  
  return {
    user,
    jwt,
    isAuthenticated: isAuthenticated(),
    logout,
    clearAuth,
    validateAuth
  }
}