import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { errorMessage } from '../services/http';
import { homeFor } from '../routes/roleConfig';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await login(email, password);
      navigate(homeFor(data.role), { replace: true });
    } catch (err) {
      setError(errorMessage(err, 'Login failed'));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <form onSubmit={handleSubmit} className="card w-full max-w-sm">
        <h1 className="text-2xl font-bold text-brand-700">CertifyPro</h1>
        <p className="mb-6 text-sm text-gray-500">Sign in to your account</p>

        {error && (
          <div className="mb-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>
        )}

        <label className="label" htmlFor="email">Email</label>
        <input id="email" type="email" className="input mb-4" value={email}
          onChange={(e) => setEmail(e.target.value)} placeholder="you@certifypro.com" required />

        <label className="label" htmlFor="password">Password</label>
        <input id="password" type="password" className="input mb-5" value={password}
          onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" required />

        <button type="submit" className="btn-primary w-full" disabled={loading}>
          {loading ? 'Signing in…' : 'Login'}
        </button>

        <p className="mt-4 text-center text-sm text-gray-500">
          New candidate? <Link to="/register" className="font-medium text-brand-600 hover:underline">Register</Link>
        </p>
      </form>
    </div>
  );
}
