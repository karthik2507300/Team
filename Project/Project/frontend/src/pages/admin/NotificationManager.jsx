import { useState } from 'react';
import * as notificationService from '../../services/notificationService';
import { errorMessage } from '../../services/http';

const ROLES = ['Candidate', 'CentreAdmin', 'ExamController', 'Evaluator', 'CertificationOfficer', 'Admin'];
const CATEGORIES = ['Registration', 'Exam', 'Result', 'Certificate', 'Renewal'];

export default function NotificationManager() {
  const [targetType, setTargetType] = useState('role');
  const [role, setRole] = useState('Candidate');
  const [userId, setUserId] = useState('');
  const [category, setCategory] = useState('Registration');
  const [message, setMessage] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    const body = { category, message };
    if (targetType === 'role') body.role = role;
    else body.userId = userId === '' ? null : Number(userId);
    try {
      await notificationService.create(body);
      setSuccess('Notification sent.');
      setMessage('');
      setUserId('');
    } catch (err) {
      setError(errorMessage(err, 'Failed to send notification'));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Notification Manager</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <p className="text-sm text-gray-500">
        Notifications are delivered in-app to the recipients' notification centre.
      </p>

      <form onSubmit={handleSubmit} className="card max-w-xl space-y-4">
        <div>
          <label className="label">Target</label>
          <div className="flex gap-4 text-sm">
            <label className="flex items-center gap-2">
              <input type="radio" name="targetType" value="role"
                checked={targetType === 'role'} onChange={() => setTargetType('role')} />
              By Role
            </label>
            <label className="flex items-center gap-2">
              <input type="radio" name="targetType" value="user"
                checked={targetType === 'user'} onChange={() => setTargetType('user')} />
              By User
            </label>
          </div>
        </div>

        {targetType === 'role' ? (
          <div>
            <label className="label">Role</label>
            <select className="input" value={role} onChange={(e) => setRole(e.target.value)}>
              {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
        ) : (
          <div>
            <label className="label">User ID</label>
            <input className="input" type="number" value={userId}
              onChange={(e) => setUserId(e.target.value)} required />
          </div>
        )}

        <div>
          <label className="label">Category</label>
          <select className="input" value={category} onChange={(e) => setCategory(e.target.value)}>
            {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>

        <div>
          <label className="label">Message</label>
          <textarea className="input" rows={4} value={message}
            onChange={(e) => setMessage(e.target.value)} required />
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Sending…' : 'Send Notification'}
        </button>
      </form>
    </div>
  );
}
