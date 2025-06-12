import axios from "axios";

const base_url = "http://localhost:8080/users/auth/";

const registerUser = async (user) => {
    console.log("Corps de la requête:", user); // Ajoutez cette ligne pour voir les données envoyées
    return axios.post(`${base_url}register`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

const loginUser = async (user) => {
    return axios.post(`${base_url}login`, user)
        .then(res => res.data)
        .catch(error => { throw error; });
};

export { registerUser, loginUser };