import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { homeFor } from '../routes/roleConfig';

/**
 * Guards a route subtree. Requires authentication and (optionally) one of `roles`.
 * Unauthenticated -> /login; wrong role -> that user's own home.
 */
export default function ProtectedRoute({ roles, children }) {
  const { isAuthenticated, role } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  if (roles && !roles.includes(role)) {
    return <Navigate to={homeFor(role)} replace />;
  }
  return children;
}
