
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
  
  const logout = (customMessage = 'Vous avez été déconnecté avec succès') => {
    console.log('Logout initiated with message:', customMessage)
   
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    dispatch(removeUser())
    dispatch(removeJwt())
   
    window.location.href = `/auth/login?message=${encodeURIComponent(customMessage)}&fromLogout=true`;
  }
  
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
    if (!token && !userData && !user?.id && !jwt) {
      return false; 
    }

    if (!token && (userData || user?.id)) {
      console.log('Token removed but user data exists - clearing user');
      clearAuth('Token manquant, reconnexion nécessaire');
      return false;
    }
    
    if (token && (!userData || !user?.id)) {
      console.log('User removed but token exists - clearing token');
      clearAuth('Données utilisateur manquantes, reconnexion nécessaire');
      return false;
    }
    
    if (token && userData && !user?.id) {
      try {
        const parsedUser = JSON.parse(userData);
        console.log('Restoring user to Redux from localStorage');
        dispatch({ type: 'user/setUser', payload: parsedUser });
        dispatch({ type: 'jwt/setJwt', payload: token });
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
