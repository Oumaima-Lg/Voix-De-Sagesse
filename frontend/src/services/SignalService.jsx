import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/signals/";

const reportArticle = async (signalData) => {
    return axiosInstance.post(`${base_url}report`, signalData)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getMySignals = async () => {
    return axiosInstance.get(`${base_url}mine`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

export { reportArticle, getMySignals };