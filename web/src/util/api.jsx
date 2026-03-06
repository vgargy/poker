import axios from 'axios';
import { toaster, Message } from "rsuite";
import React from "react"; // <- Needed for createElement


const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add Authorization header to every request
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("token");
    console.log("token...", token);
    if (token && token !== 'undefined') {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Global response interceptor
api.interceptors.response.use(
  (response) => {
    const contentType = response.headers["content-type"];
    if (
      response.config.responseType === "blob" ||
      contentType?.includes("application/octet-stream")
    ) {
      return response;
    }

    const data = response.data;
    if (data.message) {
      toaster.push(
        React.createElement(Message, { type: "success" }, data.message),
        { placement: "topEnd" }
      );
    }
    return data.data;
  },
  (error) => {
     if (error.response && error.response.status === 401) {
      // Clear token from localStorage
      sessionStorage.removeItem('token');

      // Redirect to login
      window.location.href = '/login?reason=auth_failed'; // simple way
      return;
    }
    toaster.push(
      React.createElement(
        Message,
        { type: "error" },
        error.response?.data?.message || "Something went wrong"
      ),
      { placement: "topEnd" }
    );
    return Promise.reject(error);
  }
);

export default api;