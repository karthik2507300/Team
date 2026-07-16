import { useEffect, useState } from 'react';
import * as resultService from '../../services/resultService';
import * as examWindowService from '../../services/examWindowService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function ResultComputation() {
  const [windows, setWindows] = useState([]);
  const [windowId, setWindowId] = useState('');
  const [results, setResults] = useState([]);
  const [computing, setComputing] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    examWindowService
      .list({ limit: 100 })
      .then((d) => setWindows(d.content ?? []))
      .catch((err) => setError(errorMessage(err, 'Failed to load exam windows')));
  }, []);

  async function handleCompute() {
    if (!windowId) return;
    setComputing(true);
    setError('');
    setSuccess('');
    try {
      const data = await resultService.compute(Number(windowId));
      const list = Array.isArray(data) ? data : [];
      setResults(list);
      setSuccess(`Computed ${list.length} draft result(s).`);
    } catch (err) {
      setError(errorMessage(err, 'Failed to compute results'));
    } finally {
      setComputing(false);
    }
  }

  const columns = [
    { key: 'resultId', header: 'Result ID' },
    { key: 'candidateId', header: 'Candidate ID' },
    { key: 'marksObtained', header: 'Marks Obtained' },
    { key: 'totalMarks', header: 'Total Marks' },
    { key: 'percentage', header: 'Percentage' },
    { key: 'grade', header: 'Grade' },
    { key: 'outcome', header: 'Outcome', render: (r) => <Badge>{r.outcome}</Badge> },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Result Computation</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card flex flex-wrap items-end gap-4">
        <div>
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
        <button className="btn-primary" disabled={computing || !windowId} onClick={handleCompute}>
          {computing ? 'Computing…' : 'Compute Results'}
        </button>
      </div>

      <Table columns={columns} rows={results} empty="No results computed yet." />
    </div>
  );
}
