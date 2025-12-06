import { useState } from 'react';
import { registerUser } from '../services/api.js';
import { useNavigate } from 'react-router-dom';

function RegisterForm({ onClose }) {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    auctionPersonNumber: "",
    role: "USER", // default role
  });

  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await registerUser(formData);

      // Optionally auto-login after registration
      const token = await loginUser({
        email: formData.email,
        password: formData.password,
      });
      localStorage.setItem("token", token);

      if (onClose) onClose();
      navigate("/profile");
    } catch (err) {
      setError(err.message || "Registration failed");
    }
  };

  return (
    <div className="auth-form">
      <h2>Register</h2>
      {error && <p className="auth-error">{error}</p>}
      <form onSubmit={handleRegister}>
        <input
          type="text"
          name="name"
          placeholder="Full Name"
          value={formData.name}
          onChange={handleChange}
          required
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="auctionPersonNumber"
          placeholder="Auction Person Number"
          value={formData.auctionPersonNumber}
          onChange={handleChange}
          required
        />

        <button type="submit" className="auth-button">
          Register
        </button>
      </form>
    </div>
  );
}

export default RegisterForm;
