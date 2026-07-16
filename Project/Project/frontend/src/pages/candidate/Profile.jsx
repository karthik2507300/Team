import { useEffect, useState } from 'react';
import * as candidateService from '../../services/candidateService';
import { errorMessage } from '../../services/http';

const EMPTY = {
  name: '',
  dateOfBirth: '',
  gender: '',
  email: '',
  phone: '',
  address: '',
  highestQualification: '',
  professionalExperience: '',
  employerName: '',
};

export default function Profile() {
  const [candidate, setCandidate] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [isNew, setIsNew] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const data = await candidateService.me();
      setCandidate(data);
      setIsNew(false);
      setForm({
        name: data.name ?? '',
        dateOfBirth: data.dateOfBirth ?? '',
        gender: data.gender ?? '',
        email: data.email ?? '',
        phone: data.phone ?? '',
        address: data.address ?? '',
        highestQualification: data.highestQualification ?? '',
        professionalExperience: data.professionalExperience ?? '',
        employerName: data.employerName ?? '',
      });
    } catch (err) {
      if (err?.response?.status === 404) {
        setIsNew(true);
        setCandidate(null);
        setForm(EMPTY);
      } else {
        setError(errorMessage(err, 'Failed to load profile'));
      }
    } finally {
      setLoading(false);
    }
  }

  function set(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSaving(true);
    try {
      const payload = { ...form, dateOfBirth: form.dateOfBirth || null };
      if (isNew) {
        const created = await candidateService.create(payload);
        setCandidate(created);
        setIsNew(false);
        setSuccess('Profile created successfully.');
      } else {
        const updated = await candidateService.update(candidate.candidateId, payload);
        setCandidate(updated);
        setSuccess('Profile updated successfully.');
      }
    } catch (err) {
      setError(errorMessage(err, 'Failed to save profile'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">
        {isNew ? 'Create your profile' : 'My Profile'}
      </h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <form onSubmit={handleSubmit} className="card">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label className="label">Name</label>
              <input className="input" value={form.name} onChange={(e) => set('name', e.target.value)} required />
            </div>
            <div>
              <label className="label">Date of Birth</label>
              <input type="date" className="input" value={form.dateOfBirth} onChange={(e) => set('dateOfBirth', e.target.value)} />
            </div>
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender} onChange={(e) => set('gender', e.target.value)}>
                <option value="">Select…</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div>
              <label className="label">Email</label>
              <input type="email" className="input" value={form.email} onChange={(e) => set('email', e.target.value)} required />
            </div>
            <div>
              <label className="label">Phone</label>
              <input className="input" value={form.phone} onChange={(e) => set('phone', e.target.value)} />
            </div>
            <div>
              <label className="label">Highest Qualification</label>
              <input className="input" value={form.highestQualification} onChange={(e) => set('highestQualification', e.target.value)} />
            </div>
            <div>
              <label className="label">Employer Name</label>
              <input className="input" value={form.employerName} onChange={(e) => set('employerName', e.target.value)} />
            </div>
            <div className="sm:col-span-2">
              <label className="label">Address</label>
              <input className="input" value={form.address} onChange={(e) => set('address', e.target.value)} />
            </div>
            <div className="sm:col-span-2">
              <label className="label">Professional Experience</label>
              <textarea className="input" rows={3} value={form.professionalExperience} onChange={(e) => set('professionalExperience', e.target.value)} />
            </div>
          </div>

          <button type="submit" className="btn-primary mt-5" disabled={saving}>
            {saving ? 'Saving…' : isNew ? 'Create Profile' : 'Save Changes'}
          </button>
        </form>
      )}
    </div>
  );
}
