import { configureStore } from '@reduxjs/toolkit'
import userReducer from './Slices/UserSlice';
import JwtReducer from './Slices/JwtSlice';

export default configureStore({
  reducer: {
    user:userReducer,
    jwt:JwtReducer
  }
})