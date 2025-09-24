import { useSelector } from 'react-redux';
import { Navigate, useLocation } from 'react-router-dom';

export const AdminRoute = ({ children }) => {
  const user = useSelector((state) => state.user);
  const location = useLocation();
  
  // Vérifier si l'utilisateur est authentifié ET est un administrateur
  const isAdmin = user && user.id && user.accountType === 'ADMIN' && localStorage.getItem('token');
  
  if (!isAdmin) {
    // Message adapté à la situation
    let message = "Accès non autorisé. Cette page est réservée aux administrateurs.";
    
    // Si l'utilisateur est connecté mais n'est pas admin
    if (user && user.id) {
      return <Navigate to="/articla" state={{ 
        from: location,
        message: "Vous n'avez pas les droits d'administration nécessaires."
      }} replace />;
    }
    
    // Si l'utilisateur n'est pas connecté du tout
    return <Navigate to="/auth/login" state={{ 
      from: location,
      message: message
    }} replace />;
  }
  
  return children;
};