import { useEffect, useState } from 'react';
import * as resultService from '../../services/resultService';
import * as examWindowService from '../../services/examWindowService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

export default function ResultPublication() {
  const [windows, setWindows] = useState([]);
  const [windowId, setWindowId] = useState('');
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [busyId, setBusyId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    examWindowService
      .list({ limit: 100 })
      .then((d) => setWindows(d.content ?? []))
      .catch((err) => setError(errorMessage(err, 'Failed to load exam windows')));
  }, []);

  useEffect(() => {
    if (windowId) load(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, windowId]);

  async function load(p) {
    setLoading(true);
    setError('');
    try {
      const data = await resultService.view({ windowId: Number(windowId), page: p, limit: 10 });
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load results'));
    } finally {
      setLoading(false);
    }
  }

  function onWindowChange(value) {
    setWindowId(value);
    setPage(0);
  }

  async function publish(row) {
    setBusyId(row.resultId);
    setError('');
    setSuccess('');
    try {
      await resultService.publish(row.resultId);
      setSuccess(`Result ${row.resultId} published.`);
      load(page);
    } catch (err) {
      setError(errorMessage(err, 'Failed to publish result'));
    } finally {
      setBusyId(null);
    }
  }

  const columns = [
    { key: 'resultId', header: 'Result ID' },
    { key: 'candidateId', header: 'Candidate ID' },
    { key: 'percentage', header: 'Percentage' },
    { key: 'grade', header: 'Grade' },
    { key: 'outcome', header: 'Outcome', render: (r) => <Badge>{r.outcome}</Badge> },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: '',
      render: (r) => {
        const canPublish = r.status === 'Draft' || r.status === 'Revised';
        return (
          <button
            className="btn-primary"
            disabled={!canPublish || busyId === r.resultId}
            onClick={() => publish(r)}
          >
            Publish
          </button>
        );
      },
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Result Publication</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card">
        <label className="label">Exam Window</label>
        <select className="input max-w-md" value={windowId} onChange={(e) => onWindowChange(e.target.value)}>
          <option value="">Select an exam window…</option>
          {windows.map((w) => (
            <option key={w.windowId} value={w.windowId}>
              {w.examName}
            </option>
          ))}
        </select>
      </div>

      {!windowId ? (
        <p className="text-gray-500">Select an exam window to view results.</p>
      ) : loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={rows} empty="No results found." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
