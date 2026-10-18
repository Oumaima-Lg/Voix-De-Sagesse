import axiosInstance from "./axiosInstance";

const base_url = "/notifications";

// The 50 most recent notifications, newest first.
const getNotifications = async () => {
    return axiosInstance.get(base_url).then(res => res.data);
};

const getUnreadCount = async () => {
    return axiosInstance.get(`${base_url}/unread-count`).then(res => res.data.count);
};

const markAllNotificationsRead = async () => {
    return axiosInstance.post(`${base_url}/read-all`);
};

export { getNotifications, getUnreadCount, markAllNotificationsRead };
