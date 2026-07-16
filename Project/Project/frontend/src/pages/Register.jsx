import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { errorMessage } from '../services/http';
import { homeFor } from '../routes/roleConfig';

export default function Register() {
  const [form, setForm] = useState({
    name: '', email: '', phone: '', password: '',
    dateOfBirth: '', highestQualification: '', professionalExperience: '', employerName: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  function set(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const payload = { ...form, dateOfBirth: form.dateOfBirth || null };
      const data = await register(payload);
      navigate(homeFor(data.role), { replace: true });
    } catch (err) {
      setError(errorMessage(err, 'Registration failed'));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-8">
      <form onSubmit={handleSubmit} className="card w-full max-w-lg">
        <h1 className="text-2xl font-bold text-brand-700">Create your candidate account</h1>
        <p className="mb-5 text-sm text-gray-500">Register to enrol in certification programmes</p>

        {error && <div className="mb-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <label className="label">Full Name</label>
            <input className="input" value={form.name} onChange={(e) => set('name', e.target.value)} required />
          </div>
          <div>
            <label className="label">Email</label>
            <input type="email" className="input" value={form.email} onChange={(e) => set('email', e.target.value)} required />
          </div>
          <div>
            <label className="label">Phone</label>
            <input className="input" value={form.phone} onChange={(e) => set('phone', e.target.value)} required />
          </div>
          <div>
            <label className="label">Password</label>
            <input type="password" className="input" value={form.password} onChange={(e) => set('password', e.target.value)} required />
          </div>
          <div>
            <label className="label">Date of Birth</label>
            <input type="date" className="input" value={form.dateOfBirth} onChange={(e) => set('dateOfBirth', e.target.value)} />
          </div>
          <div>
            <label className="label">Highest Qualification</label>
            <input className="input" value={form.highestQualification} onChange={(e) => set('highestQualification', e.target.value)} />
          </div>
          <div className="sm:col-span-2">
            <label className="label">Professional Experience</label>
            <textarea className="input" rows={2} value={form.professionalExperience} onChange={(e) => set('professionalExperience', e.target.value)} />
          </div>
        </div>

        <button type="submit" className="btn-primary mt-5 w-full" disabled={loading}>
          {loading ? 'Creating account…' : 'Register'}
        </button>
        <p className="mt-4 text-center text-sm text-gray-500">
          Already registered? <Link to="/login" className="font-medium text-brand-600 hover:underline">Sign in</Link>
        </p>
      </form>
    </div>
  );
}
