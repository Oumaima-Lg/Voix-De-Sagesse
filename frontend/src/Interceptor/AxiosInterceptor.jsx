import axios from 'axios';
import { Navigate } from 'react-router-dom';

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Intercepteur pour ajouter le token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Ne pas écraser le Content-Type si c'est déjà défini (important pour multipart/form-data)
    if (config.headers['Content-Type'] === 'multipart/form-data') {
      // Laisser axios définir automatiquement le Content-Type avec boundary
      delete config.headers['Content-Type'];
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les réponses et erreurs
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem("token");
      // Rediriger vers la page de connexion si nécessaire
      // window.location.href = '/login';
      Navigate('/login');
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;