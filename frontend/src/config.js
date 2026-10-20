export const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export const fileUrl = (path) => (path?.startsWith("http") ? path : `${API_URL}${path}`);
