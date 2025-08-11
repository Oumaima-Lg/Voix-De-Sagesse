// import { createSlice } from "@reduxjs/toolkit";
// import { getItem } from "../services/LocalStorageService";

// const UserSlice = createSlice({
//     name: "user",
//     initialState: getItem("user"),
//     reducers: {
//         setUser: (state, action) => {
//             setItem("user", action.payload);
//             state = getItem("user");
//             return state;
//         },
//         removeUser: (state) => {
//             removeItem("user");
//             state = null;
//             return state;
//         },
//     }
// });

// export const { setUser, removeUser } = UserSlice.actions;
// export default UserSlice.reducer;

import { createSlice } from "@reduxjs/toolkit";
import { getItem, setItem, removeItem } from "../services/LocalStorageService";

// L'état initial : on récupère l'utilisateur depuis le localStorage
const initialState = getItem("user") || null;

const UserSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser: (state, action) => {
      setItem("user", action.payload); // effet de bord
      return action.payload; // met à jour l'état avec le nouvel utilisateur
    },
    updateUser: (state, action) => {
      const updatedUser = { ...state, ...action.payload };
      setItem("user", updatedUser); 
      return updatedUser; // met à jour l'état avec l'utilisateur modifié
    },
    removeUser: () => {
      removeItem("user"); // effet de bord
      return null; // réinitialise l'état
    },
  },
});

export const { setUser, updateUser, removeUser } = UserSlice.actions;
export default UserSlice.reducer;
