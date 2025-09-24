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


export { 
    getAllUsers,
    getDashboardStats
 };