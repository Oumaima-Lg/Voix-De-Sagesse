
import { createSlice } from "@reduxjs/toolkit";

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
