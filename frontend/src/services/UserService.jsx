import axios from "axios";

const base_url = "http://localhost:8080/users/";

const registerUser = async (user) => {
    return axios.post(`${base_url}auth/register`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const loginUser = async (user) => {
    return axios.post(`${base_url}auth/login`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const sendOtp= async (email) => {
    return axios.post(`${base_url}sendOtp/${email}`)
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const verifyOtp= async (email, otp) => {
    return axios.get(`${base_url}verifyOtp/${email}/${otp}`)
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const changePass=async(email, motdepasse) => {
    return axios.post(`${base_url}auth/changePass`, {email, motdepasse})
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const updateUserProfile = async (userProfile) => {
    return axios.put(`${base_url}updateProfile`, userProfile)
    .then(res => res.data)
    .catch(error => { throw error; });
}

export { registerUser, loginUser, sendOtp, verifyOtp, changePass, updateUserProfile };