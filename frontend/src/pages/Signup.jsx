import { useState } from "react";
import "../styles/Signup.css";
import { useNavigate } from "react-router-dom";
import "../styles/themes.css";
import { API_BASE_URL } from "../utils/api";

function Signup() {
  const navigate = useNavigate();
  const [theme] = useState(
      localStorage.getItem("theme") || "ocean"
  );
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: ""
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSignup = async () => {
    if (form.password !== form.confirmPassword) {
        alert("Passwords do not match!");
        return;
    }
    if (
        !form.name.trim() ||
        !form.email.trim() ||
        !form.password.trim() ||
        !form.confirmPassword.trim()
    ) {
        alert("Please fill all fields!");
        return;
    }
    try {
        const response = await fetch(`${API_BASE_URL}/signup`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            name: form.name,
            email: form.email,
            password: form.password,
        }),
        });

        const data = await response.json();

          if (!response.ok) {

              if (data.detail === "Email already exists") {

                  alert("This account already exists. Redirecting to Login...");

                  localStorage.setItem("loginEmail", form.email);

                  navigate("/login");

                  return;
              }

              alert(data.detail || data.message || "Signup failed");
              return;
          }

          alert(data.message);

          localStorage.setItem("loggedIn", "true");
          localStorage.setItem("userEmail", form.email);
          localStorage.setItem("name", form.name);

          navigate("/dashboard");
          
    } catch (error) {
        console.error(error);
        alert("Server Error!");
    }
    };

  return (
    <div className={`signup-container theme-${theme}`}>
      <div className="signup-card">
        <h1>Resume Fit Analyzer</h1>
        <h2>Create Account</h2>

        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSignup();
          }}
        >

          <label className="input-label">
              👤 Full Name
          </label>

        <input
          type="text"
          name="name"
          placeholder="Enter your full name"
          value={form.name}
          onChange={handleChange}
        />

        <label className="input-label">
            📧 Email Address
        </label>

        <input
          type="email"
          name="email"
          placeholder="Enter your email"
          value={form.email}
          onChange={handleChange}
        />

        <label className="input-label">
            🔒 Password
        </label>

        <input
          type="password"
          name="password"
          placeholder="Enter your password"
          value={form.password}
          onChange={handleChange}
        />

        <label className="input-label">
            🔐 Confirm Password
        </label>

        <input
          type="password"
          name="confirmPassword"
          placeholder="Re-enter your password"
          value={form.confirmPassword}
          onChange={handleChange}
        />

        <button type="submit">
            Create Account
        </button>
        </form>

        <p>
          Already have an account?{" "}
          <span
            onClick={() => navigate("/login")}
            style={{
              color: "#2563eb",
              cursor: "pointer",
              fontWeight: "bold"
            }}
          >
            Login
          </span>
        </p>
      </div>
    </div>
  );
}

export default Signup;