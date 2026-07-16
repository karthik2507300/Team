import { useEffect, useState } from 'react';
import * as scriptAllocationService from '../../services/scriptAllocationService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

const emptyAssign = { allocationId: '', evaluatorId: '', paperId: '' };

export default function ScriptAllocation() {
  const [assignForm, setAssignForm] = useState(emptyAssign);
  const [assigning, setAssigning] = useState(false);

  const [filterEvaluatorId, setFilterEvaluatorId] = useState('');
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  function setField(key, value) {
    setAssignForm((f) => ({ ...f, [key]: value }));
  }

  async function load(p) {
    setLoading(true);
    setError('');
    try {
      const params = { page: p, limit: 10 };
      if (filterEvaluatorId) params.evaluatorId = Number(filterEvaluatorId);
      const data = await scriptAllocationService.list(params);
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load allocations'));
    } finally {
      setLoading(false);
    }
  }

  async function handleAssign(e) {
    e.preventDefault();
    setAssigning(true);
    setError('');
    setSuccess('');
    try {
      await scriptAllocationService.assign({
        allocationId: Number(assignForm.allocationId),
        evaluatorId: Number(assignForm.evaluatorId),
        paperId: Number(assignForm.paperId),
      });
      setSuccess('Script assigned successfully.');
      setAssignForm(emptyAssign);
      load(page);
    } catch (err) {
      setError(errorMessage(err, 'Failed to assign script'));
    } finally {
      setAssigning(false);
    }
  }

  function applyFilter(e) {
    e.preventDefault();
    if (page === 0) load(0);
    else setPage(0);
  }

  const columns = [
    { key: 'scriptId', header: 'Script ID' },
    { key: 'allocationId', header: 'Allocation ID' },
    { key: 'evaluatorId', header: 'Evaluator ID' },
    { key: 'paperId', header: 'Paper ID' },
    { key: 'allocationDate', header: 'Allocation Date' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Script Allocation</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <form onSubmit={handleAssign} className="card flex flex-wrap items-end gap-4">
        <h2 className="w-full text-lg font-semibold text-gray-700">Assign Script</h2>
        <div>
          <label className="label">Allocation ID</label>
          <input
            className="input"
            type="number"
            value={assignForm.allocationId}
            onChange={(e) => setField('allocationId', e.target.value)}
            required
          />
        </div>
        <div>
          <label className="label">Evaluator ID</label>
          <input
            className="input"
            type="number"
            value={assignForm.evaluatorId}
            onChange={(e) => setField('evaluatorId', e.target.value)}
            required
          />
        </div>
        <div>
          <label className="label">Paper ID</label>
          <input
            className="input"
            type="number"
            value={assignForm.paperId}
            onChange={(e) => setField('paperId', e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={assigning}>
          {assigning ? 'Assigning…' : 'Assign Script'}
        </button>
      </form>

      <form onSubmit={applyFilter} className="flex flex-wrap items-end gap-4">
        <div>
          <label className="label">Filter by Evaluator ID</label>
          <input
            className="input"
            type="number"
            value={filterEvaluatorId}
            onChange={(e) => setFilterEvaluatorId(e.target.value)}
          />
        </div>
        <button type="submit" className="btn-secondary">
          Apply
        </button>
      </form>

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={rows} empty="No allocations found." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
