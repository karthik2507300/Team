import { useEffect, useState } from 'react';
import * as resultService from '../../services/resultService';
import * as reEvaluationService from '../../services/reEvaluationService';
import { errorMessage } from '../../services/http';

export default function ReEvaluation() {
  const [results, setResults] = useState([]);
  const [resultId, setResultId] = useState('');
  const [reason, setReason] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const data = await resultService.view({ limit: 100 });
      setResults(data.content ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load results'));
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      await reEvaluationService.submit({ resultId, reason });
      setSuccess('Re-evaluation request submitted successfully.');
      setReason('');
      setResultId('');
    } catch (err) {
      setError(errorMessage(err, 'Failed to submit re-evaluation request'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Request Re-Evaluation</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <form onSubmit={handleSubmit} className="card max-w-lg space-y-4">
          <div>
            <label className="label">Result</label>
            <select className="input" value={resultId} onChange={(e) => setResultId(e.target.value)} required>
              <option value="">Select a result…</option>
              {results.map((r) => (
                <option key={r.resultId} value={r.resultId}>
                  Result #{r.resultId} - {r.outcome}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Reason</label>
            <textarea
              className="input"
              rows={4}
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Explain why you are requesting a re-evaluation…"
              required
            />
          </div>
          <button type="submit" className="btn-primary" disabled={submitting}>
            {submitting ? 'Submitting…' : 'Submit Request'}
          </button>
        </form>
      )}
    </div>
  );
}
