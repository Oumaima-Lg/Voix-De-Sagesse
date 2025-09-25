import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/signals/";

const reportArticle = async (signalData) => {
    return axiosInstance.post(`${base_url}report`, signalData)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getPendingSignals = async () => {
    return axiosInstance.get(`${base_url}pending`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getSignalsByReporter = async (reporterId) => {
    return axiosInstance.get(`${base_url}by-reporter/${reporterId}`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const processSignal = async (signalId, status, adminId, adminComment = "") => {
    return axiosInstance.put(`${base_url}process/${signalId}`, null, {
        params: {
            status: status,
            adminId: adminId,
            adminComment: adminComment
        }
    })
        .then(res => res.data)
        .catch(error => { throw error; });
}

export { reportArticle, getPendingSignals, getSignalsByReporter, processSignal };