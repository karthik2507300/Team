import { useEffect, useState } from 'react';
import * as resultService from '../../services/resultService';
import * as reEvaluationService from '../../services/reEvaluationService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function ReEvaluationManager() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  const [requestId, setRequestId] = useState('');
  const [resolving, setResolving] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const data = await resultService.view({ page: 0, limit: 50 });
      const all = data.content ?? [];
      setRows(all.filter((r) => r.status === 'UnderReEvaluation'));
    } catch (err) {
      setError(errorMessage(err, 'Failed to load results'));
    } finally {
      setLoading(false);
    }
  }

  async function handleResolve(e) {
    e.preventDefault();
    setResolving(true);
    setError('');
    setSuccess('');
    try {
      await reEvaluationService.resolve(Number(requestId));
      setSuccess(`Re-evaluation request ${requestId} resolved.`);
      setRequestId('');
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to resolve re-evaluation request'));
    } finally {
      setResolving(false);
    }
  }

  const columns = [
    { key: 'resultId', header: 'Result ID' },
    { key: 'candidateId', header: 'Candidate ID' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Re-Evaluation Manager</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <p className="rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-600">
        Results currently under re-evaluation are listed below (scanned from recent results). Use the
        form to resolve a specific re-evaluation request by its ID; the matching result returns to a
        Revised state once resolved.
      </p>

      <div className="space-y-2">
        <h2 className="text-lg font-semibold text-gray-700">Results Under Re-Evaluation</h2>
        {loading ? (
          <p className="text-gray-500">Loading…</p>
        ) : (
          <Table columns={columns} rows={rows} empty="No results currently under re-evaluation." />
        )}
      </div>

      <form onSubmit={handleResolve} className="card flex flex-wrap items-end gap-4">
        <h2 className="w-full text-lg font-semibold text-gray-700">Resolve a Request</h2>
        <div>
          <label className="label">Request ID</label>
          <input
            className="input"
            type="number"
            value={requestId}
            onChange={(e) => setRequestId(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={resolving}>
          {resolving ? 'Resolving…' : 'Resolve'}
        </button>
      </form>
    </div>
  );
}
