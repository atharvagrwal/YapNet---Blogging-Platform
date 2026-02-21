import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const authService = {
    login: async (username, password) => {
        const response = await axios.post(`${API_URL}/login`, {
            username,
            password
        });
        return response.data;
    },

    register: async (username, password, email) => {
        const response = await axios.post(`${API_URL}/register`, {
            username,
            password,
            email
        });
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
    },

    getCurrentUser: () => {
        return localStorage.getItem('username');
    },

    getToken: () => {
        return localStorage.getItem('token');
    }
};
