
import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/users/";


const updateUserProfile = async (userProfile) => {
    return axiosInstance.put(`${base_url}me`, userProfile)
    .then(res => res.data)
    .catch(error => { throw error; });
}

const getUserProfile = async (userId) => {
    return axiosInstance.get(`${base_url}${userId}`)
    .then(res => res.data)
    .catch(error => { throw error; });
}

const uploadProfilePicture = async (formData) => {
    return axiosInstance.post(`${base_url}me/picture`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        }
    })
    .then(res => res.data)
    .catch(error => { throw error; });
}

const followUser = async (targetUserId) => {
    return axiosInstance.post(`${base_url}${targetUserId}/follow`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const unfollowUser = async (targetUserId) => {
    return axiosInstance.delete(`${base_url}${targetUserId}/follow`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const isFollowing = async (targetUserId) => {
    return axiosInstance.get(`${base_url}${targetUserId}/follow`)
        .then(res => res.data)
        .catch(error => { throw error; });
}


const saveArticle = async (articleId) => {
    return axiosInstance.post(`${base_url}me/saved/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const unsaveArticle = async (articleId) => {
    return axiosInstance.delete(`${base_url}me/saved/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const isArticleSaved = async (articleId) => {
    return axiosInstance.get(`${base_url}me/saved/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const deleteAccount = async () => {
    return axiosInstance.delete(`${base_url}me`)
        .then(res => res.data)
        .catch(error => { throw error; });
}


export { updateUserProfile, 
    getUserProfile, uploadProfilePicture, followUser, unfollowUser, isFollowing, 
    saveArticle, unsaveArticle, isArticleSaved, deleteAccount};