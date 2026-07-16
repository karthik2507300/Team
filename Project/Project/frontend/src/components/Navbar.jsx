import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROLE_LABEL } from '../routes/roleConfig';

export default function Navbar() {
  const { auth, role, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate('/login', { replace: true });
  }

  return (
    <header className="flex items-center justify-between border-b border-gray-200 bg-white px-6 py-3">
      <div className="text-sm text-gray-500">{ROLE_LABEL[role] || role} Portal</div>
      <div className="flex items-center gap-4">
        <div className="text-right">
          <div className="text-sm font-semibold text-gray-800">{auth?.name}</div>
          <div className="text-xs text-gray-400">{auth?.email}</div>
        </div>
        <button className="btn-danger" onClick={handleLogout}>Logout</button>
      </div>
    </header>
  );
}
