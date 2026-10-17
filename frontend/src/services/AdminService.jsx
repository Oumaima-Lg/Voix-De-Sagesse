import axios from "axios";
import axiosInstance from "../Interceptor/AxiosInterceptor";

const base_url = "/admin/";

const getAllUsers = async () => {
    return axiosInstance.get(`${base_url}users`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getDashboardStats = async () => {
    return axiosInstance.get(`${base_url}dashboard`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const getAllSignals = async () => {
    return axiosInstance.get(`${base_url}signals`)
        .then(res => res.data)
        .catch(error => { throw error; });
}

const processSignal = async (signalId, status, adminComment = "") => {
    return axiosInstance.put(`${base_url}signals/${signalId}/process`, null, {
        params: {
            status: status,
            adminComment: adminComment
        }
    })
        .then(res => res.data)
        .catch(error => { throw error; });
}

export { 
    getAllUsers,
    getDashboardStats,
    processSignal,
    getAllSignals
};