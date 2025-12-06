
import { Navigate } from 'react-router-dom';
import { isAuthenticated, isAdmin } from '../services/auth.js';

function ProtectedRoute({ children }) {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (!isAdmin()) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default ProtectedRoute;