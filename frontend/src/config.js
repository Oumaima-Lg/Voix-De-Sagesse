// Backend URL for the current environment, set in frontend/.env (see .env.example).
export const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

// Profile pictures are stored as paths on the API ("/uploads/..."); absolute URLs are kept as they are.
export const fileUrl = (path) => (path?.startsWith("http") ? path : `${API_URL}${path}`);
