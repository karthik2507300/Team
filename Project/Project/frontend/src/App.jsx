import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import ProtectedRoute from './components/ProtectedRoute';
import { useAuth } from './context/AuthContext';
import { homeFor } from './routes/roleConfig';
import CandidateRoutes from './routes/CandidateRoutes';
import AdminRoutes from './routes/AdminRoutes';
import CentreAdminRoutes from './routes/CentreAdminRoutes';
import ControllerRoutes from './routes/ControllerRoutes';
import EvaluatorRoutes from './routes/EvaluatorRoutes';
import CertOfficerRoutes from './routes/CertOfficerRoutes';

export default function App() {
  const { isAuthenticated, role } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={isAuthenticated ? <Navigate to={homeFor(role)} replace /> : <Login />} />
      <Route path="/register" element={isAuthenticated ? <Navigate to={homeFor(role)} replace /> : <Register />} />

      <Route path="/candidate/*" element={<ProtectedRoute roles={['Candidate']}><CandidateRoutes /></ProtectedRoute>} />
      <Route path="/admin/*" element={<ProtectedRoute roles={['Admin']}><AdminRoutes /></ProtectedRoute>} />
      <Route path="/centre/*" element={<ProtectedRoute roles={['CentreAdmin']}><CentreAdminRoutes /></ProtectedRoute>} />
      <Route path="/controller/*" element={<ProtectedRoute roles={['ExamController']}><ControllerRoutes /></ProtectedRoute>} />
      <Route path="/evaluator/*" element={<ProtectedRoute roles={['Evaluator']}><EvaluatorRoutes /></ProtectedRoute>} />
      <Route path="/officer/*" element={<ProtectedRoute roles={['CertificationOfficer']}><CertOfficerRoutes /></ProtectedRoute>} />

      <Route path="/" element={<Navigate to={isAuthenticated ? homeFor(role) : '/login'} replace />} />
      <Route path="*" element={<Navigate to={isAuthenticated ? homeFor(role) : '/login'} replace />} />
    </Routes>
  );
}
