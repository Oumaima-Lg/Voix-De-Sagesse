import { useSelector } from 'react-redux';
import { Navigate, useLocation } from 'react-router-dom';

export const AdminRoute = ({ children }) => {
  const user = useSelector((state) => state.user);
  const location = useLocation();
  
  const isAdmin = user && user.id && user.accountType === 'ADMIN' && localStorage.getItem('token');
  
  if (!isAdmin) {
    let message = "Accès non autorisé. Cette page est réservée aux administrateurs.";

    if (user && user.id) {
      return <Navigate to="/articla" state={{ 
        from: location,
        message: "Vous n'avez pas les droits d'administration nécessaires."
      }} replace />;
    }
    
    return <Navigate to="/auth/login" state={{ 
      from: location,
      message: message
    }} replace />;
  }
  
  return children;
};