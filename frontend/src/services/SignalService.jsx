import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/signals/";

const reportArticle = async (signalData) => {
    return axiosInstance.post(`/signals`, signalData)
        .then(res => res.data)
        .catch(error => { throw error; });
}

export { reportArticle };