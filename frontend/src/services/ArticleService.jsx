import axios from "axios";

const base_url = "http://localhost:8080/articles/";

const createSagesseArticle = async (articleSagesse) => {
    return axios.post(`${base_url}createSaggesseArticla`, articleSagesse)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const createHistoireArticle = async (articleHistoire) => {
    return axios.post(`${base_url}createHistoireArticla`, articleHistoire)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const DisplayArticles= async () => {
    return axios.get(`${base_url}`)
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const DisplayPersoArticle= async (userId) => {
    return axios.get(`${base_url}persoArticle/${userId}`)
    .then(result => result.data)
    .catch(error =>{throw error;});
}

const DisplayPosts= async (currentUserId) => {
    return axios.get(`${base_url}posts/${currentUserId}`)
    .then(result => result.data)
    .catch(error =>{throw error;});
}

export { createSagesseArticle, createHistoireArticle, DisplayArticles, DisplayPersoArticle, DisplayPosts };