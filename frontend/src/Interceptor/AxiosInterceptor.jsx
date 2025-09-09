import axios from 'axios';
import { Navigate } from 'react-router-dom';
import { errorNotification } from '../services/NotificationService'; 

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  // baseURL: "https://articlabackend-production.up.railway.app",
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


// ✅ Intercepteur pour gérer les réponses et erreurs (CORRIGÉ)
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const { response, config } = error;

    if (response) {
      switch (response.status) {
        case 404:
          // ✅ Gestion spécifique des 404 API
          if (config?.url?.includes('/api/') || config?.url?.includes('/users/') || config?.url?.includes('/articles/')) {
            errorNotification('Ressource introuvable', 'L\'élément demandé n\'existe pas ou a été supprimé');
          }
          break;

        case 401:
          errorNotification('Non autorisé', 'Veuillez vous reconnecter');
          localStorage.removeItem("token");
          // ✅ Redirection correcte
          window.location.href = '/login';
          break;

        case 403:
          errorNotification('Accès refusé', 'Vous n\'avez pas les permissions nécessaires');
          break;

        case 500:
          errorNotification('Erreur serveur', 'Une erreur interne s\'est produite');
          break;

        default:
          errorNotification('Erreur', response.data?.message || response.data?.error || 'Une erreur s\'est produite');
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