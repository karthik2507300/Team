import { useEffect, useState } from 'react';
import * as examWindowService from '../../services/examWindowService';
import * as testCentreService from '../../services/testCentreService';
import * as seatAllocationService from '../../services/seatAllocationService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function AttendanceSheet() {
  const [windows, setWindows] = useState([]);
  const [centres, setCentres] = useState([]);
  const [windowId, setWindowId] = useState('');
  const [centreId, setCentreId] = useState('');
  const [allocations, setAllocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingList, setLoadingList] = useState(false);
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

  async function loadAllocations() {
    if (!windowId || !centreId) return;
    setLoadingList(true);
    setError('');
    setSuccess('');
    try {
      const rows = await seatAllocationService.listByWindowCentre({ windowId, centreId });
      setAllocations(rows ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load attendance sheet'));
    } finally {
      setLoadingList(false);
    }
  }

  async function mark(id, status) {
    setError('');
    setSuccess('');
    try {
      await seatAllocationService.updateStatus(id, status);
      setSuccess(`Marked allocation ${id} as ${status}.`);
      await loadAllocations();
    } catch (err) {
      setError(errorMessage(err, 'Failed to update status'));
    }
  }

  const columns = [
    { key: 'allocationId', header: 'Allocation ID' },
    { key: 'candidateId', header: 'Candidate ID' },
    { key: 'hallTicketNumber', header: 'Hall Ticket' },
    { key: 'roomNumber', header: 'Room' },
    { key: 'seatNumber', header: 'Seat' },
    { key: 'status', header: 'Status', render: (row) => <Badge>{row.status}</Badge> },
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex gap-2">
          <button className="btn-primary" onClick={() => mark(row.allocationId, 'Confirmed')}>
            Mark Present
          </button>
          <button className="btn-danger" onClick={() => mark(row.allocationId, 'NoShow')}>
            No Show
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Attendance Sheet</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <div className="card flex flex-wrap items-end gap-4">
          <div className="min-w-[200px] flex-1">
            <label className="label">Exam Window</label>
            <select className="input" value={windowId} onChange={(e) => setWindowId(e.target.value)}>
              <option value="">Select an exam window…</option>
              {windows.map((w) => (
                <option key={w.windowId} value={w.windowId}>
                  {w.examName}
                </option>
              ))}
            </select>
          </div>
          <div className="min-w-[200px] flex-1">
            <label className="label">Test Centre</label>
            <select className="input" value={centreId} onChange={(e) => setCentreId(e.target.value)}>
              <option value="">Select a test centre…</option>
              {centres.map((c) => (
                <option key={c.centreId} value={c.centreId}>
                  {c.centreName} - {c.city}
                </option>
              ))}
            </select>
          </div>
          <button
            className="btn-primary"
            onClick={loadAllocations}
            disabled={!windowId || !centreId || loadingList}
          >
            {loadingList ? 'Loading…' : 'Load'}
          </button>
        </div>
      )}

      <Table columns={columns} rows={allocations} empty="No allocations loaded." />
    </div>
  );
}
