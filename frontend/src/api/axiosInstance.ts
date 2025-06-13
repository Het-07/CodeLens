import axios from "axios";
import { CONFIG } from "../config/dev.config";
// const BASE_URL = "http://localhost:8080";
const axiosInstance = axios.create({
  baseURL: CONFIG.BASE_URL, // Replace with your API URL
  headers: {
    "Content-Type": "application/json",
  },
});
 
// Add a request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    // Example: Attach token if available
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
 
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle global errors
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        // Handle unauthorized error
        console.warn("Unauthorized! Redirecting to login...");
        
        // Clear token from local storage
        localStorage.removeItem("token");

        // Redirect to the login page
        window.location.href = "/"; // Using window.location ensures redirect even if not inside React context
      } else {
        console.error("Axios Error:", error.response.data.message || "An error occurred");
      }
    } else {
      console.error("Network Error:", error.message);
    }

    return Promise.reject(error);
}
);
 
export default axiosInstance; 