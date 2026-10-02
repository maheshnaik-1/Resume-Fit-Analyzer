import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Signup.css";
import "../styles/themes.css";
import { API_BASE_URL } from "../utils/api";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState(
      () => localStorage.getItem("loginEmail") || ""
  );

  useEffect(() => {
      localStorage.removeItem("loginEmail");
  }, []);
  const [password, setPassword] = useState("");

  async function loginUser() {
    if (!email || !password) {
      alert("Please fill all fields");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email,
          password,
        }),
      });

      const data = await response.json();

      if (data.success) {
        localStorage.setItem("loggedIn", "true");

        localStorage.setItem("loggedIn", "true");
        localStorage.setItem("name", data.name);

        localStorage.setItem("userEmail", data.email);

        // optional
        localStorage.removeItem("email");

        alert(data.message);

        navigate("/dashboard");
      } else {
      alert(data.message);
    }
  } catch (error) {
    console.error(error);
    alert("Backend connection failed");
  }
}

  return (
    <div className="signup-container">
      <div className="signup-card">
        <h1>Resume Fit Analyzer</h1>

        <h2>Login</h2>

        <form
            onSubmit={(e) => {
                e.preventDefault();
                loginUser();
            }}
        >

          <label className="input-label">
              📧 Email Address
          </label>

        <input
          type="email"
          placeholder="Enter your email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <label className="input-label">
            🔒 Password
        </label>

        <input
          type="password"
          placeholder="Enter your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button type="submit">
            Login
        </button>
        </form>
        <p>
          Don't have an account?{" "}
          <Link to="/signup">Signup</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;