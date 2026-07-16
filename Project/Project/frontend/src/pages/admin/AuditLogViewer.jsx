import { useEffect, useState } from 'react';
import * as auditService from '../../services/auditService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Pagination from '../../components/Pagination';

export default function AuditLogViewer() {
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);

  const [filters, setFilters] = useState({ module: '', userId: '', from: '', to: '' });

  async function load() {
    setLoading(true);
    setError('');
    try {
      const params = { page, limit: 10 };
      if (filters.module) params.module = filters.module;
      if (filters.userId) params.userId = Number(filters.userId);
      if (filters.from) params.from = filters.from;
      if (filters.to) params.to = filters.to;
      const res = await auditService.list(params);
      setData(res);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load audit logs'));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  function applyFilters(e) {
    e.preventDefault();
    if (page === 0) load();
    else setPage(0);
  }

  function exportCsv() {
    const rows = data.content || [];
    const headers = ['auditId', 'userId', 'action', 'module', 'entityId', 'timestamp'];
    const escape = (v) => {
      const s = v == null ? '' : String(v);
      return /[",\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s;
    };
    const csv = [
      headers.join(','),
      ...rows.map((r) => headers.map((h) => escape(r[h])).join(',')),
    ].join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `audit-logs-page-${(data.page ?? page) + 1}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  const columns = [
    { key: 'auditId', header: 'ID' },
    { key: 'userId', header: 'User' },
    { key: 'action', header: 'Action' },
    { key: 'module', header: 'Module' },
    { key: 'entityId', header: 'Entity' },
    { key: 'timestamp', header: 'Timestamp' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Audit Log Viewer</h1>
        <button className="btn-secondary" onClick={exportCsv} disabled={!data.content?.length}>
          Export CSV
        </button>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <form onSubmit={applyFilters} className="card flex flex-wrap items-end gap-4">
        <div>
          <label className="label">Module</label>
          <input className="input" value={filters.module}
            onChange={(e) => setFilters({ ...filters, module: e.target.value })} />
        </div>
        <div>
          <label className="label">User ID</label>
          <input className="input" type="number" value={filters.userId}
            onChange={(e) => setFilters({ ...filters, userId: e.target.value })} />
        </div>
        <div>
          <label className="label">From</label>
          <input className="input" type="date" value={filters.from}
            onChange={(e) => setFilters({ ...filters, from: e.target.value })} />
        </div>
        <div>
          <label className="label">To</label>
          <input className="input" type="date" value={filters.to}
            onChange={(e) => setFilters({ ...filters, to: e.target.value })} />
        </div>
        <button type="submit" className="btn-primary">Apply</button>
      </form>

      <div className="card">
        <Table columns={columns} rows={loading ? [] : data.content} empty={loading ? 'Loading…' : 'No audit logs found.'} />
        <Pagination page={data.page ?? page} totalPages={data.totalPages} onChange={setPage} />
      </div>
    </div>
  );
}
