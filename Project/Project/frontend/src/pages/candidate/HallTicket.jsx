import { useEffect, useState } from 'react';
import * as candidateService from '../../services/candidateService';
import * as seatAllocationService from '../../services/seatAllocationService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function HallTicket() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const me = await candidateService.me();
      const data = await seatAllocationService.byCandidate(me.candidateId);
      setRows(data ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load hall tickets'));
    } finally {
      setLoading(false);
    }
  }

  async function download(row) {
    setError('');
    setSuccess('');
    try {
      const blob = await seatAllocationService.downloadHallTicket(row.allocationId);
      window.open(URL.createObjectURL(blob));
      setSuccess(`Hall ticket ${row.hallTicketNumber} opened.`);
    } catch (err) {
      setError(errorMessage(err, 'Failed to download hall ticket'));
    }
  }

  const columns = [
    { key: 'hallTicketNumber', header: 'Hall Ticket #' },
    { key: 'windowId', header: 'Window' },
    { key: 'centreId', header: 'Centre' },
    { key: 'roomNumber', header: 'Room' },
    { key: 'seatNumber', header: 'Seat' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: '',
      render: (r) => (
        <button className="btn-secondary" onClick={() => download(r)}>
          Download PDF
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Hall Tickets</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <Table columns={columns} rows={rows} empty="No seat allocations found." />
      )}
    </div>
  );
}
