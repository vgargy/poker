import { useState } from "react";
import api from "../util/api";
import { toaster, Message } from "rsuite";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

export default function RegisterPage() {

  const [error, setError] = useState("");
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    password: "",
    confirmPassword: ""
  });


  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
        toaster.push(
          <Message type="error">{"Password Do not Match"}</Message>,
          { placement: "topEnd" }
        );
      return;
    }

    try {
      api.post("/poker/auth/register", {
        userName: form.username,
        password: form.password
      }).then(response => {
        navigate("/login");
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
        <h1>Register</h1>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <form onSubmit={handleSubmit}>
          <input
            name="username"
            placeholder="Username"
            onChange={handleChange}
            required
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            onChange={handleChange}
            required
          />

          <input
            type="password"
            name="confirmPassword"
            placeholder="Confirm Password"
            onChange={handleChange}
            required
          />

          {error && <p style={{ color: "red" }}>{error}</p>}

          <button type="submit">Register</button>
        </form>
              <p>
        Already have an account? <Link to="/login">Login</Link>
      </p>
      </div>
    </div>
  );
}