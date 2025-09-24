import axios from "axios";

import axiosInstance from "../Interceptor/AxiosInterceptor";

// const base_url = "https://articlabackend-production.up.railway.app/users/";
const base_url = "/users/";


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

const uploadProfilePicture = async (formData) => {
    return axiosInstance.post(`${base_url}upload-profile-picture`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        }
    })
    .then(res => res.data)
    .catch(error => { throw error; });
}

const followUser = async (targetUserId) => {
    return axiosInstance.post(`${base_url}follow/${targetUserId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const unfollowUser = async (targetUserId) => {
    return axiosInstance.delete(`${base_url}unfollow/${targetUserId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const isFollowing = async (targetUserId) => {
    return axiosInstance.get(`${base_url}is-following/${targetUserId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getFollowing = async () => {
    return axiosInstance.get(`${base_url}/following`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getFollowers = async (userId) => {
    return axiosInstance.get(`${base_url}followers/${userId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}


const saveArticle = async (articleId) => {
    return axiosInstance.post(`${base_url}save-article/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const unsaveArticle = async (articleId) => {
    return axiosInstance.delete(`${base_url}unsave-article/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const isArticleSaved = async (articleId) => {
    return axiosInstance.get(`${base_url}is-article-saved/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}


export { updateUserProfile, 
    getUserProfile, uploadProfilePicture, followUser, unfollowUser, isFollowing, 
    getFollowing, getFollowers, saveArticle, unsaveArticle, isArticleSaved };