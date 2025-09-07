import axios from "axios";

// const base_url = "https://articlabackend-production.up.railway.app/auth/";
const base_url = "http://localhost:8080/auth/";


const loginUser = async (user) => {
    return axios.post(`${base_url}login`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};



export { loginUser };