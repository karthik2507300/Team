import { useEffect, useState } from 'react';
import * as examWindowService from '../../services/examWindowService';
import * as testCentreService from '../../services/testCentreService';
import * as seatAllocationService from '../../services/seatAllocationService';
import { errorMessage } from '../../services/http';

export default function ExamRegistration() {
  const [windows, setWindows] = useState([]);
  const [centres, setCentres] = useState([]);
  const [windowId, setWindowId] = useState('');
  const [centreId, setCentreId] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load();
  }, []);

  async function load() {
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
      setError(errorMessage(err, 'Failed to load registration options'));
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      const alloc = await seatAllocationService.allocate({ windowId, centreId });
      setSuccess(`Seat allocated — Hall Ticket: ${alloc.hallTicketNumber}`);
    } catch (err) {
      setError(errorMessage(err, 'Seat allocation failed'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Exam Registration</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <form onSubmit={handleSubmit} className="card max-w-lg space-y-4">
          <div>
            <label className="label">Exam Window</label>
            <select className="input" value={windowId} onChange={(e) => setWindowId(e.target.value)} required>
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
            <select className="input" value={centreId} onChange={(e) => setCentreId(e.target.value)} required>
              <option value="">Select a test centre…</option>
              {centres.map((c) => (
                <option key={c.centreId} value={c.centreId}>
                  {c.centreName} - {c.city}
                </option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn-primary" disabled={submitting}>
            {submitting ? 'Allocating…' : 'Register & Allocate Seat'}
          </button>
        </form>
      )}
    </div>
  );
}
