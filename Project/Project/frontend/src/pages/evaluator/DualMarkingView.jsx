import { useState } from 'react';
import * as marksService from '../../services/marksService';
import { errorMessage } from '../../services/http';
import Badge from '../../components/Badge';

export default function DualMarkingView() {
  const [scriptId, setScriptId] = useState('');
  const [entries, setEntries] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleLoad(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await marksService.list({ scriptId: Number(scriptId) });
      setEntries(data.content ?? []);
      setLoaded(true);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load marks entries'));
      setEntries([]);
      setLoaded(false);
    } finally {
      setLoading(false);
    }
  }

  const difference =
    entries.length === 2
      ? Math.abs(Number(entries[0].marksAwarded) - Number(entries[1].marksAwarded))
      : null;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Dual Marking View</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <form onSubmit={handleLoad} className="flex items-end gap-3">
        <div className="flex-1 max-w-xs">
          <label className="label">Script ID</label>
          <input
            type="number"
            className="input"
            value={scriptId}
            onChange={(e) => setScriptId(e.target.value)}
            placeholder="Enter script ID"
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Loading…' : 'Load'}
        </button>
      </form>

      {loaded && entries.length === 0 && (
        <p className="text-gray-500">No marks entries found for this script.</p>
      )}

      {entries.length > 0 && (
        <>
          <div className="grid gap-4 md:grid-cols-2">
            {entries.map((entry, i) => (
              <div key={entry.id ?? entry.evaluatorId ?? i} className="card space-y-2">
                <div className="flex items-center justify-between">
                  <span className="label">Evaluator</span>
                  <span className="font-semibold text-gray-800">{entry.evaluatorId}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="label">Marks Awarded</span>
                  <span className="font-semibold text-gray-800">{entry.marksAwarded}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="label">Status</span>
                  <Badge>{entry.status}</Badge>
                </div>
              </div>
            ))}
          </div>

          {difference !== null && (
            <div className="rounded-lg bg-amber-50 px-3 py-2 text-sm font-semibold text-amber-800">
              Difference in marks awarded: {difference}
            </div>
          )}
        </>
      )}
    </div>
  );
}
