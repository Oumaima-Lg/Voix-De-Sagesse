// AxiosIntercepteur.jsx
import axios from 'axios';
import { errorNotification } from '../services/NotificationService';
import { removeUser } from '../Slices/UserSlice';
import { removeJwt } from '../Slices/JwtSlice';
import store from '../store';

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// ✅ Fonction pour nettoyer l'authentification (AMÉLIORÉE)
const clearAuth = () => {
  console.log('Axios interceptor - clearing auth due to error')
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  store.dispatch(removeUser());
  store.dispatch(removeJwt());
};

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
    if (token && !userData) {
      console.log('Token exists but no user data - clearing token')
      localStorage.removeItem("token");
      store.dispatch(removeJwt());
    } else if (!token && userData) {
      console.log('User data exists but no token - clearing user')
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

// AxiosIntercepteur.jsx - Section response interceptor mise à jour
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
            // ✅ Messages différenciés selon le contexte
            const token = localStorage.getItem('token')
            const userData = localStorage.getItem('user')

            let message = 'Session expirée, veuillez vous reconnecter'

            if (!token && userData) {
              message = 'Token manquant, veuillez vous reconnecter'
            } else if (token && !userData) {
              message = 'Données utilisateur manquantes, veuillez vous reconnecter'
            }

            // errorNotification('Session expirée', message);
            clearAuth();

            const currentPath = window.location.pathname;
            if (!currentPath.startsWith('/auth/')) {
              window.location.href = `/auth/login?message=${encodeURIComponent(message)}&redirect=${encodeURIComponent(currentPath)}`;
            }
          }
          break;

        // ... autres cas inchangés
        case 403:
          errorNotification('Accès refusé', 'Vous n\'avez pas les permissions nécessaires');
          break;

        case 401:
          if (!isPublicRoute(config.url)) {
            const token = localStorage.getItem('token')
            const userData = localStorage.getItem('user')

            // ✅ Message spécifique pour les erreurs d'authentification
            let message = 'Session expirée, veuillez vous reconnecter'

            if (!token && userData) {
              message = 'Token manquant, reconnexion nécessaire'
            } else if (token && !userData) {
              message = 'Données utilisateur corrompues, reconnexion nécessaire'
            }

            errorNotification('Session expirée', message);
            clearAuth();

            const currentPath = window.location.pathname;
            if (!currentPath.startsWith('/auth/')) {
              window.location.href = `/auth/login?message=${encodeURIComponent(message)}&redirect=${encodeURIComponent(currentPath)}`;
            }
          }
          break;

        case 500:
          errorNotification('Erreur serveur', 'Une erreur interne s\'est produite');
          break;

        default:
          if (!isPublicRoute(config.url)) {
            errorNotification('Erreur', response.data?.message || response.data?.error || 'Une erreur s\'est produite');
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