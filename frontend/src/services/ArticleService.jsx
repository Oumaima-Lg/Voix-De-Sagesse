import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/articles/"; 

const createSagesseArticle = async (articleSagesse) => {
    return axiosInstance.post(`${base_url}createSaggesseArticla`, articleSagesse)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const createHistoireArticle = async (articleHistoire) => {
    return axiosInstance.post(`${base_url}createHistoireArticla`, articleHistoire)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const DisplayArticles = async () => {
    return axiosInstance.get(`${base_url}`)
        .then(result => result.data)
        .catch(error => { throw error; });
}

const DisplayPersoArticle = async (userId) => {
    return axiosInstance.get(`${base_url}persoArticle/${userId}`)
        .then(result => result.data)
        .catch(error => { throw error; });
}

const DisplayPosts = async (currentUserId) => {
    return axiosInstance.get(`${base_url}posts/${currentUserId}`)
        .then(result => result.data)
        .catch(error => { throw error; });
}

const likeArticle = async (id, currentUserId) => {
    return axiosInstance.post(`${base_url}${id}/like`, null, {
        params: { currentUserId }
    })
        .then(result => result.data)
        .catch(error => { throw error; });
}

const unlikeArticle = async (id, currentUserId) => {
    return axiosInstance.post(`${base_url}${id}/unlike`, null, {
        params: { currentUserId }
    })
        .then(result => result.data)
        .catch(error => { throw error; });
}

const getSavedArticles = async (userId) => {
    return axiosInstance.get(`${base_url}saved/${userId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getMyPosts = async (userId) => {
    return axiosInstance.get(`${base_url}my-posts/${userId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getUserPosts = async (userId) => {
    return axiosInstance.get(`${base_url}user-posts/${userId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const deleteArticleWithUserId = async (articleId, userId) => {
    return axiosInstance.delete(`${base_url}delete/${articleId}/${userId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const searchArticles = async (userId, searchParams) => {
    return axiosInstance.get(`${base_url}search/${userId}`, {
        params: {
            searchText: searchParams.searchText || '',
            type: searchParams.type || ''
        }
    })
        .then(result => result.data)
        .catch(error => { throw error; });
}


export { 
    createSagesseArticle, 
    createHistoireArticle, 
    DisplayArticles, 
    DisplayPersoArticle, 
    DisplayPosts, 
    likeArticle, 
    unlikeArticle,
    getSavedArticles,
    getMyPosts,
    getUserPosts,
    deleteArticleWithUserId,
    searchArticles
};