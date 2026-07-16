import { useEffect, useState } from 'react';
import * as programService from '../../services/programService';
import * as enrolmentService from '../../services/enrolmentService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

export default function ProgramCatalog() {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load(page);
  }, [page]);

  async function load(p) {
    setLoading(true);
    setError('');
    try {
      const data = await programService.list({ page: p, limit: 10 });
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load programs'));
    } finally {
      setLoading(false);
    }
  }

  async function enrol(row) {
    setError('');
    setSuccess('');
    try {
      await enrolmentService.create({ programId: row.programId });
      setSuccess(`Enrolled in ${row.programName} successfully.`);
    } catch (err) {
      setError(errorMessage(err, 'Enrolment failed'));
    }
  }

  const visible = filter
    ? rows.filter((r) => (r.programName ?? '').toLowerCase().includes(filter.toLowerCase()))
    : rows;

  const columns = [
    { key: 'programName', header: 'Program' },
    { key: 'body', header: 'Body' },
    { key: 'level', header: 'Level' },
    { key: 'examFee', header: 'Exam Fee' },
    { key: 'validityYears', header: 'Validity (yrs)' },
    { key: 'maxAttempts', header: 'Max Attempts' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: '',
      render: (r) => (
        <button className="btn-primary" onClick={() => enrol(r)}>
          Enrol
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Program Catalog</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="max-w-sm">
        <label className="label">Filter by name</label>
        <input
          className="input"
          placeholder="Search programs…"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        />
      </div>

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={visible} empty="No programs found." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
