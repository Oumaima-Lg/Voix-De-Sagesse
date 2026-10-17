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

const DisplayPosts = async () => {
    return axiosInstance.get(`${base_url}posts`)
        .then(result => result.data)
        .catch(error => { throw error; });
}

const likeArticle = async (id) => {
    return axiosInstance.post(`${base_url}${id}/like`)
        .then(result => result.data)
        .catch(error => { throw error; });
}

const unlikeArticle = async (id) => {
    return axiosInstance.post(`${base_url}${id}/unlike`)
        .then(result => result.data)
        .catch(error => { throw error; });
}

const getSavedArticles = async () => {
    return axiosInstance.get(`${base_url}saved`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getMyPosts = async () => {
    return axiosInstance.get(`${base_url}my-posts`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getUserPosts = async (userId) => {
    return axiosInstance.get(`${base_url}user-posts/${userId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const deleteArticle = async (articleId) => {
    return axiosInstance.delete(`${base_url}${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const searchArticles = async (searchParams) => {
    return axiosInstance.get(`${base_url}search`, {
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
    deleteArticle,
    searchArticles
};