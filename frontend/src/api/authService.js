import axios from "axios";
import { API_URL } from "../config";

const base_url = `${API_URL}/auth/`;


const loginUser = async (user) => {
    return axios.post(`${base_url}login`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

// Sign-up step 1: emails a 6-digit code. The account only exists once the code is confirmed.
const registerUser = async (user) => {
    return axios.post(`${base_url}register`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

// Sign-up step 2: creates the account.
const verifyRegistration = async (email, otp) => {
    return axios.post(`${base_url}register/verify`, { email, otp })
        .then(res => res.data)
        .catch(error => { throw error; });
};

const sendOtp= async (email) => {
    return axios.post(`${base_url}password/otp`, { email })
    .then(result => result.data)
    .catch(error =>{throw error;});
}

// Returns { message, resetToken }; the token is required by changePass.
const verifyOtp= async (email, otp) => {
    return axios.post(`${base_url}password/otp/verify`, { email, otp })
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const changePass=async(email, resetToken, newPassword) => {
    return axios.post(`${base_url}password/reset`, { email, resetToken, newPassword })
    .then(result => result.data)
    .catch(error =>{throw error;});
}


export { loginUser, registerUser, verifyRegistration, sendOtp, verifyOtp, changePass };