export const SERVER_UNAVAILABLE =
  "Le serveur ne répond pas pour le moment : il redémarre peut-être, ou l'hébergement gratuit a atteint sa limite du mois. Réessayez dans quelques minutes.";

const getApiErrorMessage = (error, fallback = "Une erreur est survenue.") => {
  const data = error?.response?.data;
  if (data?.errorMessage || data?.message) return data.errorMessage || data.message;
  if (!error?.response || [502, 503, 504].includes(error.response.status)) return SERVER_UNAVAILABLE;
  return fallback;
};

export { getApiErrorMessage };
