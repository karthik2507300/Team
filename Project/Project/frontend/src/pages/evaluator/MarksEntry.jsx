import { useState } from 'react';
import * as marksService from '../../services/marksService';
import { errorMessage } from '../../services/http';

export default function MarksEntry() {
  const [scriptId, setScriptId] = useState('');
  const [marksAwarded, setMarksAwarded] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      const entry = await marksService.submit({
        scriptId: Number(scriptId),
        marksAwarded: Number(marksAwarded),
      });
      let msg = `Marks submitted (status: ${entry?.status ?? 'Unknown'})`;
      if (entry?.status === 'Moderated') {
        msg += ' — the script was flagged for moderation.';
      }
      setSuccess(msg);
      setScriptId('');
      setMarksAwarded('');
    } catch (err) {
      if (err?.response?.status === 409) {
        setError(errorMessage(err, 'Dual marking already complete for this script'));
      } else {
        setError(errorMessage(err, 'Failed to submit marks'));
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Marks Entry</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <form onSubmit={handleSubmit} className="card max-w-lg space-y-4">
        <div>
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
        <div>
          <label className="label">Marks Awarded</label>
          <input
            type="number"
            className="input"
            value={marksAwarded}
            onChange={(e) => setMarksAwarded(e.target.value)}
            placeholder="Enter marks awarded"
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={submitting}>
          {submitting ? 'Submitting…' : 'Submit Marks'}
        </button>
      </form>
    </div>
  );
}
