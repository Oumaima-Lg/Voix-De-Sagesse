import axios from 'axios';
import { errorNotification } from '../utils/notifications';
import { removeUser } from '../store/userSlice';
import { removeJwt } from '../store/jwtSlice';
import store from '../store';
import { API_URL } from '../config';

const axiosInstance = axios.create({
  baseURL: API_URL,
  timeout:  60000,
  headers: {
    'Content-Type': 'application/json',
  }
});

const isPublicRoute = (url) => url.includes('/auth/');

axiosInstance.interceptors.request.use(
  (config) => {

    const token = localStorage.getItem("token");
    const userData = localStorage.getItem("user");

    if (token && !userData) {
      localStorage.removeItem("token");
      store.dispatch(removeJwt());
    } else if (!token && userData) {
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
  (response) => response,
  (error) => {
    const { response, config } = error;

    if (response?.status === 401) {
      if (!isPublicRoute(config.url)) {
        const token = localStorage.getItem('token');
        const userData = localStorage.getItem('user');

        if (!token && !userData) {
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
    } else if (response?.status === 403 && config.url.includes('/admin/')) {
      errorNotification('Accès refusé', 'Cet espace est réservé aux administrateurs.');
      window.location.href = '/espace';
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;

