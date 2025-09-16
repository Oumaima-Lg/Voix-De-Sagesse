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
    // Utiliser window.location.href pour une réinitialisation complète de l'application
    // et s'assurer que le message est affiché correctement.
    window.location.href = `/auth/login?message=${encodeURIComponent(customMessage)}&fromLogout=true`;
  }
  
  // ✅ Fonction pour nettoyer l'authentification (pour les erreurs système ou incohérences)
  // Cette fonction ne doit PAS naviguer, elle nettoie juste l'état.
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
    
    // Si le token et les données utilisateur sont absents, et que Redux est vide, c'est OK (déconnecté)
    if (!token && !userData && !user?.id && !jwt) {
      return false; // Non authentifié
    }

    // Cas 1: Token supprimé mais utilisateur encore présent (dans localStorage ou Redux)
    if (!token && (userData || user?.id)) {
      console.log('Token removed but user data exists - clearing user');
      clearAuth('Token manquant, reconnexion nécessaire');
      return false;
    }
    
    // Cas 2: Utilisateur supprimé mais token encore présent (dans localStorage ou Redux)
    if (token && (!userData || !user?.id)) {
      console.log('User removed but token exists - clearing token');
      clearAuth('Données utilisateur manquantes, reconnexion nécessaire');
      return false;
    }
    
    // Cas 3: Token et userData existent, mais pas d'utilisateur dans Redux
    if (token && userData && !user?.id) {
      try {
        const parsedUser = JSON.parse(userData);
        console.log('Restoring user to Redux from localStorage');
        dispatch({ type: 'user/setUser', payload: parsedUser });
        dispatch({ type: 'jwt/setJwt', payload: token }); // Assurer que le JWT est aussi dans Redux
        return true;
      } catch (error) {
        console.error('Error parsing user data:', error);
        clearAuth('Données corrompues, veuillez vous reconnecter');
        return false;
      }
    }
    
    return isAuthenticated();
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
