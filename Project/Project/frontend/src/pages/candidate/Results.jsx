import { useEffect, useState } from 'react';
import * as resultService from '../../services/resultService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

export default function Results() {
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
      const data = await resultService.view({ page: p, limit: 10 });
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load results'));
    } finally {
      setLoading(false);
    }
  }

  const columns = [
    { key: 'programId', header: 'Program' },
    { key: 'windowId', header: 'Window' },
    { key: 'marksObtained', header: 'Marks' },
    { key: 'totalMarks', header: 'Total' },
    { key: 'percentage', header: 'Percentage' },
    { key: 'grade', header: 'Grade' },
    { key: 'outcome', header: 'Outcome', render: (r) => <Badge>{r.outcome}</Badge> },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">My Results</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={rows} empty="No results published yet." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
