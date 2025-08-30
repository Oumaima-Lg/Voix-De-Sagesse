
import { createSlice } from "@reduxjs/toolkit";
import { getItem, setItem, removeItem } from "../services/LocalStorageService";

// L'état initial : on récupère l'utilisateur depuis le localStorage
const initialState = localStorage.getItem("token") || "";

const JwtSlice = createSlice({
  name: "jwt",
  initialState,
  reducers: {
    setJwt: (state, action) => {
      localStorage.setItem("token", action.payload); // effet de bord
      return action.payload; // met à jour l'état avec le nouvel utilisateur
    },
    removeJwt: () => {
      localStorage.removeItem("token"); // effet de bord
      return ""; // réinitialise l'état
    },
  },
});

export const { setJwt, updateJwt, removeJwt } = JwtSlice.actions;
export default JwtSlice.reducer;
