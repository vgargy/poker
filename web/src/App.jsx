import React, { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Games from "./components/Games";
import RegisterPage from "./auth/RegisterPage";
import LoginPage from "./auth/LoginPage";

function App() {


  const [token, setToken] = useState(() => {
  const savedToken = sessionStorage.getItem("token");
    return (savedToken && 
            savedToken.trim() !== "" && 
            savedToken !== 'undefined' && 
            savedToken.split(".").length === 3) ? savedToken : null;
});


  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to={token ? "/games" : "/login"} />}/>
        <Route path="/login" element={!token ? <LoginPage setToken={setToken}/> : <Navigate to="/games" />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/games" element={token ? <Games setToken={setToken}/> : <Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;