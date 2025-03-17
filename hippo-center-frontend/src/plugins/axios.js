import axios from "axios";

const axiosapi = axios.create({
    baseURL: import.meta.env.VITE_API_URL
});

axiosapi.interceptors.response.use(function (response) {
        return response;
    }, function (error) {
        if(error.response && error.response.status && error.response.status===403) {
            window.location.href = "/403";
        }
        return Promise.reject(error);
    }
);

// 設置請求攔截器，動態添加 Authorization 標頭
axiosapi.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken'); // 從 localStorage 讀取 token
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`; // 設置 Authorization 標頭
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosapi;
