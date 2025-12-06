import { useState } from 'react';
import { loginUser } from '../services/api.js';
import { useNavigate } from 'react-router-dom';

function LoginForm({ onClose }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const token = await loginUser({ email, password });
      localStorage.setItem('token', token);
      navigate('/profile');
      if (onClose) onClose();
    } catch (err) {
      setError(err.message || 'Login failed');
    }
  };

  return (
    <div className="auth-form">
      <h2>Login</h2>
      {error && <p className="auth-error">{error}</p>}
      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" className="auth-button">
          Login
        </button>
      </form>
    </div>
  );
}

export default LoginForm;