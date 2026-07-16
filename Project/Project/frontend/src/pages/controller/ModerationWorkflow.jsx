import { useEffect, useState } from 'react';
import * as marksService from '../../services/marksService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

export default function ModerationWorkflow() {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  async function load(p) {
    setLoading(true);
    setError('');
    try {
      const data = await marksService.list({ status: 'Moderated', page: p, limit: 10 });
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load marks entries'));
    } finally {
      setLoading(false);
    }
  }

  async function runAction(id, action, label) {
    setBusyId(id);
    setError('');
    setSuccess('');
    try {
      await action(id);
      setSuccess(`${label} successful for marks entry ${id}.`);
      load(page);
    } catch (err) {
      setError(errorMessage(err, `${label} failed`));
    } finally {
      setBusyId(null);
    }
  }

  const columns = [
    { key: 'marksId', header: 'Marks ID' },
    { key: 'scriptId', header: 'Script ID' },
    { key: 'evaluatorId', header: 'Evaluator ID' },
    { key: 'marksAwarded', header: 'Marks Awarded' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: '',
      render: (r) => (
        <div className="flex gap-2">
          <button
            className="btn-secondary"
            disabled={busyId === r.marksId}
            onClick={() => runAction(r.marksId, marksService.moderate, 'Moderate')}
          >
            Moderate
          </button>
          <button
            className="btn-primary"
            disabled={busyId === r.marksId}
            onClick={() => runAction(r.marksId, marksService.verify, 'Verify')}
          >
            Verify
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Moderation Workflow</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={rows} empty="No marks entries to moderate." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
