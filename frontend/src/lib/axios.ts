// 예: src/lib/axios.ts
import axios from "axios";
import { getAccessToken } from "./tokenStorage"; // 전역 상태나 localStorage 등에서 access token 가져오는 함수

const axiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080",
  withCredentials: true,
});

// 요청 인터셉터
axiosInstance.interceptors.request.use((config) => {
  const token = getAccessToken(); // 혹은 useAuth().accessToken
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
