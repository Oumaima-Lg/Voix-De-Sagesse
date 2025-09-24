// AxiosIntercepteur.jsx
import axios from 'axios';
import { errorNotification } from '../services/NotificationService';
import { removeUser } from '../Slices/UserSlice';
import { removeJwt } from '../Slices/JwtSlice';
import store from '../store';

// Nous n'importons plus useAuth ici pour éviter les problèmes de hook dans un contexte non-React
// et pour centraliser la logique de redirection dans useAuth.logout

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// ✅ Fonction pour vérifier si la route est publique
const isPublicRoute = (url) => {
  const publicRoutes = [
    '/auth/login',
    '/users/auth/register',
    '/users/verifyOtp/',
    '/users/sendOtp/',
    '/users/auth/changePass'
  ];
  return publicRoutes.some(route => url.includes(route));
};

// ✅ Intercepteur pour ajouter le token (AMÉLIORÉ)
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    const userData = localStorage.getItem("user");

    // ✅ Vérification de cohérence avant chaque requête
    // Ces vérifications sont importantes pour maintenir la cohérence avant d'envoyer la requête
    if (token && !userData) {
      console.log('Token exists but no user data - clearing token');
      localStorage.removeItem("token");
      store.dispatch(removeJwt());
    } else if (!token && userData) {
      console.log('User data exists but no token - clearing user');
      localStorage.removeItem("user");
      store.dispatch(removeUser());
    }

    // Ajouter le token seulement si présent et route non publique
    const validToken = localStorage.getItem("token");
    if (validToken && !isPublicRoute(config.url)) {
      config.headers.Authorization = `Bearer ${validToken}`;
    }

    // Ne pas écraser le Content-Type si c'est déjà défini
    if (config.headers['Content-Type'] === 'multipart/form-data') {
      delete config.headers['Content-Type'];
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const { response, config } = error;

    if (response) {
      switch (response.status) {

        case 401:
          if (!isPublicRoute(config.url)) {
            const token = localStorage.getItem('token');
            const userData = localStorage.getItem('user');

            // Si le token et les données utilisateur sont déjà absents, cela signifie
            // qu'une déconnexion volontaire a probablement déjà eu lieu ou que l'état est déjà nettoyé.
            // Dans ce cas, nous ne faisons rien pour éviter d'écraser le message de déconnexion.
            if (!token && !userData) {
              console.log('401 received but token and user already cleared. Assuming voluntary logout or already handled.');
              return Promise.reject(error);
            }

            // Si nous arrivons ici, c'est un 401 inattendu (session expirée, token invalide, etc.)
            // Nous nettoyons l'état et redirigeons avec un message d'expiration.
            let message = 'Session expirée, veuillez vous reconnecter';

            if (!token && userData) {
              message = 'Token manquant, reconnexion nécessaire';
            } else if (token && !userData) {
              message = 'Données utilisateur corrompues, reconnexion nécessaire';
            }

            errorNotification('Session expirée', message);

            // Nettoyer l'état global via Redux et localStorage
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            store.dispatch(removeUser());
            store.dispatch(removeJwt());

            // Redirection complète pour réinitialiser l'application React
            window.location.href = `/auth/login?message=${encodeURIComponent(message)}&redirect=${encodeURIComponent(window.location.pathname)}`;
          }
          break;
        
        // Dans la section case 403:
        case 403:
          errorNotification('Accès refusé', 'Vous n\'avez pas les permissions nécessaires');

          // Si l'utilisateur tentait d'accéder à une route admin mais n'est pas admin
          if (config.url.includes('/admin/')) {
            // Rediriger vers la page principale utilisateur
            window.location.href = '/articla';
          }
          break;

        // case 403:
        //   errorNotification('Accès refusé', 'Vous n\'avez pas les permissions nécessaires');
        //   break;

        case 500:
          errorNotification('Erreur serveur', 'Une erreur interne s\'est produite');
          break;

        default:
          if (!isPublicRoute(config.url)) {
            errorNotification('Erreur', response.data?.errorMessage || response.data?.error || 'Une erreur s\'est produite');
          }
      }
    } else if (error.code === 'ECONNABORTED') {
      errorNotification('Timeout', 'La requête a pris trop de temps');
    } else {
      errorNotification('Erreur réseau', 'Impossible de contacter le serveur');
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;

