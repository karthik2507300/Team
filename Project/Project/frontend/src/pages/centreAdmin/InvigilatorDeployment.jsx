import { useEffect, useState } from 'react';
import * as examWindowService from '../../services/examWindowService';
import * as testCentreService from '../../services/testCentreService';
import * as invigilatorService from '../../services/invigilatorService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function InvigilatorDeployment() {
  const [windows, setWindows] = useState([]);
  const [centres, setCentres] = useState([]);
  const [windowId, setWindowId] = useState('');
  const [centreId, setCentreId] = useState('');
  const [userId, setUserId] = useState('');
  const [roomNumber, setRoomNumber] = useState('');
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadOptions();
  }, []);

  async function loadOptions() {
    setLoading(true);
    setError('');
    try {
      const [w, c] = await Promise.all([
        examWindowService.list({ limit: 100 }),
        testCentreService.list({ limit: 100 }),
      ]);
      setWindows(w.content ?? []);
      setCentres(c.content ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load options'));
    } finally {
      setLoading(false);
    }
  }

  async function loadAssignments() {
    if (!windowId || !centreId) return;
    setError('');
    try {
      const rows = await invigilatorService.list({ windowId, centreId });
      setAssignments(rows ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load invigilator assignments'));
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      await invigilatorService.assign({
        windowId,
        centreId,
        userId: Number(userId),
        roomNumber,
      });
      setSuccess('Invigilator assigned successfully.');
      setUserId('');
      setRoomNumber('');
      await loadAssignments();
    } catch (err) {
      setError(errorMessage(err, 'Failed to assign invigilator'));
    } finally {
      setSubmitting(false);
    }
  }

  const columns = [
    { key: 'assignmentId', header: 'Assignment ID' },
    { key: 'userId', header: 'User ID' },
    { key: 'roomNumber', header: 'Room' },
    { key: 'status', header: 'Status', render: (row) => <Badge>{row.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Invigilator Deployment</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <form onSubmit={handleSubmit} className="card grid gap-4 sm:grid-cols-2">
          <div>
            <label className="label">Exam Window</label>
            <select
              className="input"
              value={windowId}
              onChange={(e) => setWindowId(e.target.value)}
              onBlur={loadAssignments}
              required
            >
              <option value="">Select an exam window…</option>
              {windows.map((w) => (
                <option key={w.windowId} value={w.windowId}>
                  {w.examName}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Test Centre</label>
            <select
              className="input"
              value={centreId}
              onChange={(e) => setCentreId(e.target.value)}
              onBlur={loadAssignments}
              required
            >
              <option value="">Select a test centre…</option>
              {centres.map((c) => (
                <option key={c.centreId} value={c.centreId}>
                  {c.centreName} - {c.city}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">User ID</label>
            <input
              type="number"
              className="input"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label">Room Number</label>
            <input
              type="text"
              className="input"
              value={roomNumber}
              onChange={(e) => setRoomNumber(e.target.value)}
              required
            />
          </div>
          <div className="sm:col-span-2 flex gap-2">
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Assigning…' : 'Assign Invigilator'}
            </button>
            <button
              type="button"
              className="btn-secondary"
              onClick={loadAssignments}
              disabled={!windowId || !centreId}
            >
              Refresh List
            </button>
          </div>
        </form>
      )}

      <Table columns={columns} rows={assignments} empty="No invigilator assignments." />
    </div>
  );
}
