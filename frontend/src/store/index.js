import { configureStore } from '@reduxjs/toolkit'
import userReducer from './userSlice';
import JwtReducer from './jwtSlice';

const store = configureStore({
  reducer: {
    user:userReducer,
    jwt:JwtReducer
  }
})

export default store