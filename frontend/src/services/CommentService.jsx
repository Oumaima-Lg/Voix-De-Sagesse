import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/comments/";

// ✅ Ajouter un commentaire
const addComment = async (articleId, content) => {
    return axiosInstance.post(`${base_url}add/${articleId}`, { content })
        .then(res => res.data)
        .catch(error => { throw error; });
}

// ✅ Récupérer les commentaires d'un article
const getCommentsByArticle = async (articleId) => {
    return axiosInstance.get(`${base_url}article/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

// ✅ Supprimer un commentaire
const deleteComment = async (commentId) => {
    return axiosInstance.delete(`${base_url}delete/${commentId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

// ✅ Obtenir le nombre de commentaires
const getCommentsCount = async (articleId) => {
    return axiosInstance.get(`${base_url}count/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

export { addComment, getCommentsByArticle, deleteComment, getCommentsCount };