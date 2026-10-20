const getApiErrorMessage = (error, fallback = "Une erreur est survenue.") => {
  if (!error?.response) return "Impossible de contacter le serveur. Réessayez plus tard.";
  const data = error.response.data;
  return data?.errorMessage || data?.message || fallback;
};

export { getApiErrorMessage };
