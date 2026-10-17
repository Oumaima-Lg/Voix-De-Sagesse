
import { useSelector, useDispatch } from 'react-redux'
import { removeUser } from '../Slices/UserSlice'
import { removeJwt } from '../Slices/JwtSlice'

export const useAuth = () => {
  const user = useSelector((state) => state.user)
  const jwt = useSelector((state) => state.jwt)
  const dispatch = useDispatch()
  
  const isAuthenticated = () => {
    const token = localStorage.getItem('token')
    const userData = localStorage.getItem('user')
    return !!(user?.id && token && userData)
  }
  
  const logout = (customMessage = 'Vous avez été déconnecté avec succès') => {
   
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    dispatch(removeUser())
    dispatch(removeJwt())
   
    window.location.href = `/auth/login?message=${encodeURIComponent(customMessage)}&fromLogout=true`;
  }
  
  const clearAuth = () => {
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
      clearAuth('Token manquant, reconnexion nécessaire');
      return false;
    }
    
    if (token && (!userData || !user?.id)) {
      clearAuth('Données utilisateur manquantes, reconnexion nécessaire');
      return false;
    }
    
    if (token && userData && !user?.id) {
      try {
        const parsedUser = JSON.parse(userData);
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
