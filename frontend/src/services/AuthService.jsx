import axios from "axios";

// const base_url = "https://articlabackend-production.up.railway.app/auth/";
const base_url = "http://localhost:8080/auth/";


const loginUser = async (user) => {
    return axios.post(`${base_url}login`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const registerUser = async (user) => {
    return axios.post(`${base_url}register`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const sendOtp= async (email) => {
    return axios.post(`${base_url}sendOtp/${email}`)
    .then(result => result.data)
    .catch(error =>{throw error;});
}

// Returns { message, resetToken }; the token is required by changePass.
const verifyOtp= async (email, otp) => {
    return axios.post(`${base_url}verifyOtp`, { email, otp })
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const changePass=async(email, resetToken, newPassword) => {
    return axios.post(`${base_url}changePass`, { email, resetToken, newPassword })
    .then(result => result.data)
    .catch(error =>{throw error;});
}


export { loginUser, registerUser, sendOtp, verifyOtp, changePass };