import { useEffect, useState } from 'react';
import * as examWindowService from '../../services/examWindowService';
import * as testCentreService from '../../services/testCentreService';
import * as seatAllocationService from '../../services/seatAllocationService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function RoomAssignment() {
  const [windows, setWindows] = useState([]);
  const [centres, setCentres] = useState([]);
  const [windowId, setWindowId] = useState('');
  const [centreId, setCentreId] = useState('');
  const [allocations, setAllocations] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingList, setLoadingList] = useState(false);
  const [error, setError] = useState('');

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
    try {
      const rows = await seatAllocationService.listByWindowCentre({ windowId, centreId });
      setAllocations(rows ?? []);
      setLoaded(true);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load room assignments'));
    } finally {
      setLoadingList(false);
    }
  }

  const groups = allocations.reduce((acc, a) => {
    const room = a.roomNumber ?? 'Unassigned';
    (acc[room] = acc[room] || []).push(a);
    return acc;
  }, {});

  const columns = [
    { key: 'candidateId', header: 'Candidate ID' },
    { key: 'seatNumber', header: 'Seat' },
    { key: 'hallTicketNumber', header: 'Hall Ticket' },
    { key: 'status', header: 'Status', render: (row) => <Badge>{row.status}</Badge> },
  ];

  const roomKeys = Object.keys(groups);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Room Assignment</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

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

      {loaded && roomKeys.length === 0 && (
        <p className="text-gray-400">No allocations found for the selected window and centre.</p>
      )}

      <div className="grid gap-4 sm:grid-cols-2">
        {roomKeys.map((room) => (
          <div key={room} className="card space-y-3">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-800">Room {room}</h2>
              <span className="text-sm text-gray-500">{groups[room].length} candidate(s)</span>
            </div>
            <Table columns={columns} rows={groups[room]} empty="No candidates." />
          </div>
        ))}
      </div>
    </div>
  );
}
