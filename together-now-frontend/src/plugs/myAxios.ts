import axios, {AxiosInstance} from "axios";
import {showConfirmDialog} from "vant";
import {useRouter} from "vue-router";

const isDev = process.env.NODE_ENV === 'development';

const router = useRouter();

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
        showConfirmDialog({
            title: '请登录后查看',
            message:
                '这个页面的内容需要登录后查看哦😘',
        })
            .then(() => {
                // on confirm
                const redirectUrl = window.location.href;
                window.location.href = `/user/login?redirect=${redirectUrl}`;
            })
            .catch(() => {
                // on cancel
                window.location.href = '/';
            });

    }
    return response.data;
}, function (error) {
    // Do something with response error
    return Promise.reject(error);
});

export default myAxios;