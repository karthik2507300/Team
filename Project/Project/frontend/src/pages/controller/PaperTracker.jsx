import { useState } from 'react';
import * as paperService from '../../services/paperService';
import { errorMessage } from '../../services/http';
import Badge from '../../components/Badge';

const TARGETS = ['Finalised', 'Distributed', 'Archived'];

export default function PaperTracker() {
  const [paperIdInput, setPaperIdInput] = useState('');
  const [paper, setPaper] = useState(null);

  const [loading, setLoading] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function handleLoad(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const data = await paperService.get(Number(paperIdInput));
      setPaper(data);
    } catch (err) {
      setPaper(null);
      setError(errorMessage(err, 'Failed to load paper'));
    } finally {
      setLoading(false);
    }
  }

  async function advance(target) {
    if (!paper) return;
    setUpdating(true);
    setError('');
    setSuccess('');
    try {
      const updated = await paperService.updateStatus(paper.paperId, target);
      setPaper(updated && updated.paperId ? updated : { ...paper, status: target });
      setSuccess(`Paper status updated to ${target}.`);
      // Reload to reflect authoritative state.
      const fresh = await paperService.get(paper.paperId);
      setPaper(fresh);
    } catch (err) {
      setError(errorMessage(err, 'Failed to update paper status'));
    } finally {
      setUpdating(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Paper Tracker</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <form onSubmit={handleLoad} className="card flex flex-wrap items-end gap-4">
        <div>
          <label className="label">Paper ID</label>
          <input
            className="input"
            type="number"
            value={paperIdInput}
            onChange={(e) => setPaperIdInput(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Loading…' : 'Load'}
        </button>
      </form>

      {paper && (
        <div className="card space-y-4">
          <div className="flex flex-wrap items-center gap-6">
            <span className="text-sm text-gray-600">
              Paper Code: <strong>{paper.paperCode}</strong>
            </span>
            <span className="text-sm text-gray-600">
              Total Marks: <strong>{paper.totalMarks}</strong>
            </span>
            <span className="text-sm text-gray-600">
              Status: <Badge>{paper.status}</Badge>
            </span>
          </div>

          <div className="flex flex-wrap gap-2 border-t border-gray-200 pt-4">
            {TARGETS.map((t) => (
              <button key={t} className="btn-secondary" disabled={updating} onClick={() => advance(t)}>
                Advance to {t}
              </button>
            ))}
          </div>
          <p className="text-xs text-gray-400">
            Backward status moves are rejected by the backend; the error will be shown above.
          </p>
        </div>
      )}
    </div>
  );
}
