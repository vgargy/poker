import axios from 'axios';
import { toaster, Message } from "rsuite";
import React from "react"; // <- Needed for createElement


const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

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