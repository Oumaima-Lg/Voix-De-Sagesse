
import { createSlice } from "@reduxjs/toolkit";
import { getItem, setItem, removeItem } from "../services/LocalStorageService";

const initialState = localStorage.getItem("token") || "";

const JwtSlice = createSlice({
  name: "jwt",
  initialState,
  reducers: {
    setJwt: (state, action) => {
      localStorage.setItem("token", action.payload); 
      return action.payload; 
    },
    removeJwt: () => {
      localStorage.removeItem("token"); 
      return ""; 
    },
  },
});

export const { setJwt, updateJwt, removeJwt } = JwtSlice.actions;
export default JwtSlice.reducer;
