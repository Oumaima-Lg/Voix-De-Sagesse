
import { createSlice } from "@reduxjs/toolkit";
import { getItem, setItem, removeItem } from "../services/LocalStorageService";

const initialState = getItem("user") || null;

const UserSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser: (state, action) => {
      setItem("user", action.payload); 
      return action.payload;
    },
    updateUser: (state, action) => {
      const updatedUser = { ...state, ...action.payload };
      setItem("user", updatedUser); 
      return updatedUser; 
    },
    removeUser: () => {
      removeItem("user");
      return null;
    },
  },
});

export const { setUser, updateUser, removeUser } = UserSlice.actions;
export default UserSlice.reducer;
