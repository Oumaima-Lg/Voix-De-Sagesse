import axios from "axios";

import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "http://localhost:8080/users/";

const base_url2 = "/users/";

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
    return axiosInstance.put(`${base_url}updateProfile`, userProfile)
    .then(res => res.data)
    .catch(error => { throw error; });
}

const getUserProfile = async (userId) => {
    return axiosInstance.get(`${base_url}profile/${userId}`)
    .then(res => res.data)
    .catch(error => { throw error; });
}

// Fonction corrigée pour l'upload de la photo de profil
const uploadProfilePicture = async (formData) => {
    // Créer une instance axios spécifique pour l'upload de fichiers
    return axiosInstance.post(`${base_url}upload-profile-picture`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        }
    })
    .then(res => res.data)
    .catch(error => { throw error; });
}

export { registerUser, loginUser, sendOtp, verifyOtp, changePass, updateUserProfile, getUserProfile, uploadProfilePicture };