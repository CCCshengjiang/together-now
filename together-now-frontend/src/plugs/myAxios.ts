import axios, {AxiosInstance} from "axios";

const isDev = process.env.NODE_ENV === 'development';

const myAxios: AxiosInstance = axios.create({
    baseURL: isDev ? 'http://localhost:8080/api' : 'https://together-now-backend.cwblue.top/api',
})

myAxios.defaults.withCredentials = true; // 允许携带cookie



// Add a request interceptor
myAxios.interceptors.request.use(function (config) {
    // Do something before request is sent
    // console.log("我要发送请求", config);
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
myAxios.interceptors.response.use(function (response) {
    // Do something with response data
    // console.log("我收到响应", response);
    if (response.data?.code === 40100) {
        const redirectUrl = window.location.href;
        window.location.href = `/user/login?redirect=${redirectUrl}`;
    }
    return response.data;
}, function (error) {
    // Do something with response error
    return Promise.reject(error);
});

export default myAxios;