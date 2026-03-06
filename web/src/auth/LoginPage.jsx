import { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import api from "../util/api";
import { toaster, Message } from "rsuite";
import { useNavigate } from "react-router-dom";

import "../css/poker.css";

export default function LoginPage({setToken}) {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    username: "",
    password: ""
  });
  const location = useLocation();

useEffect(() => {
    const params = new URLSearchParams(location.search);
    const reason = params.get("reason");
    if (reason === "auth_failed") {
      setError("Authentication failed. Please log in again.");
    }
  }, [location]);


  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      api.post(`/poker/auth/login`, {
        userName: form.username,
        password: form.password
      }).then(response => {
        sessionStorage.setItem("token", response);
        setToken(response); // update App state
        navigate("/games");
      });
    } catch (err) {
        toaster.push(
          <Message type="error">{err.message}</Message>,
          { placement: "topEnd" }
        );
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h1>Login</h1>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <input
          name="username"
          placeholder="Username"
          onChange={handleChange}
          required
        /><br />
        <input
          type="password"
          name="password"
          placeholder="Password"
          onChange={handleChange}
          required
        /><br />
        <button type="submit">Login</button>
      </form>
      <p>
        Don't have an account? <Link to="/register">Register here</Link>
      </p>
      </div>
    </div>
  );
}

