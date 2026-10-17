import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/comments/";

const addComment = async (articleId, content) => {
    return axiosInstance.post(`${base_url}article/${articleId}`, { content })
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getCommentsByArticle = async (articleId) => {
    return axiosInstance.get(`${base_url}article/${articleId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const deleteComment = async (commentId) => {
    return axiosInstance.delete(`${base_url}${commentId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

export { addComment, getCommentsByArticle, deleteComment };