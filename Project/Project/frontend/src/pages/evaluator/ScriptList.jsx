import { useEffect, useState } from 'react';
import * as scriptAllocationService from '../../services/scriptAllocationService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

export default function ScriptList() {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    load(page);
  }, [page]);

  async function load(p) {
    setLoading(true);
    setError('');
    try {
      const data = await scriptAllocationService.list({ page: p, limit: 10 });
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load assigned scripts'));
    } finally {
      setLoading(false);
    }
  }

  const columns = [
    { key: 'scriptId', header: 'Script' },
    { key: 'allocationId', header: 'Allocation' },
    { key: 'paperId', header: 'Paper' },
    { key: 'allocationDate', header: 'Allocation Date' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">My Assigned Scripts</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={rows} empty="No scripts assigned to you yet." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
