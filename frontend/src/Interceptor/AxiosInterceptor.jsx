import axios from 'axios';
import { errorNotification } from '../services/NotificationService';
import { removeUser } from '../Slices/UserSlice';
import { removeJwt } from '../Slices/JwtSlice';
import store from '../store';

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  timeout:  60000,
  headers: {
    'Content-Type': 'application/json',
  }
});

const isPublicRoute = (url) => {
  const publicRoutes = [
    '/auth/login',
    '/auth/register',
    '/auth/verifyOtp/',
    '/auth/sendOtp/',
    '/auth/changePass'
  ];
  return publicRoutes.some(route => url.includes(route));
};

axiosInstance.interceptors.request.use(
  (config) => {

    const token = localStorage.getItem("token");
    const userData = localStorage.getItem("user");

    if (token && !userData) {
      console.log('Token exists but no user data - clearing token');
      localStorage.removeItem("token");
      store.dispatch(removeJwt());
    } else if (!token && userData) {
      console.log('User data exists but no token - clearing user');
      localStorage.removeItem("user");
      store.dispatch(removeUser());
    }

    const validToken = localStorage.getItem("token");
    if (validToken && !isPublicRoute(config.url)) {
      config.headers.Authorization = `Bearer ${validToken}`;
    }

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

            if (!token && !userData) {
              console.log('401 received but token and user already cleared. Assuming voluntary logout or already handled.');
              return Promise.reject(error);
            }

     
            let message = 'Session expirée, veuillez vous reconnecter';

            if (!token && userData) {
              message = 'Token manquant, reconnexion nécessaire';
            } else if (token && !userData) {
              message = 'Données utilisateur corrompues, reconnexion nécessaire';
            }

            errorNotification('Session expirée', message);

            localStorage.removeItem('token');
            localStorage.removeItem('user');
            store.dispatch(removeUser());
            store.dispatch(removeJwt());

            window.location.href = `/auth/login?message=${encodeURIComponent(message)}&redirect=${encodeURIComponent(window.location.pathname)}`;
          }
          break;
        
        case 403:
          errorNotification('Accès refusé', 'Vous n\'avez pas les permissions nécessaires');

          if (config.url.includes('/admin/')) {
            window.location.href = '/articla';
          }
          break;

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

