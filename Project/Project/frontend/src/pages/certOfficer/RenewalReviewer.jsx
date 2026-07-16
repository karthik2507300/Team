import { useState } from 'react';
import * as renewalService from '../../services/renewalService';
import { errorMessage } from '../../services/http';
import Badge from '../../components/Badge';

export default function RenewalReviewer() {
  const [renewalId, setRenewalId] = useState('');
  const [renewal, setRenewal] = useState(null);
  const [extendYears, setExtendYears] = useState(3);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function loadRenewal(id) {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const data = await renewalService.get(Number(id));
      setRenewal(data);
    } catch (err) {
      setRenewal(null);
      setError(errorMessage(err, 'Failed to load renewal'));
    } finally {
      setLoading(false);
    }
  }

  async function handleLoad(e) {
    e.preventDefault();
    if (!renewalId) return;
    loadRenewal(renewalId);
  }

  async function handleReview(decision) {
    if (!renewal) return;
    setSubmitting(true);
    setError('');
    setSuccess('');
    try {
      const body =
        decision === 'Approved'
          ? { decision: 'Approved', extendYears: Number(extendYears) }
          : { decision: 'Rejected' };
      await renewalService.review(renewal.renewalId, body);
      setSuccess(`Renewal ${decision.toLowerCase()} successfully.`);
      loadRenewal(renewal.renewalId);
    } catch (err) {
      setError(errorMessage(err, 'Failed to submit review'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Renewal Reviewer</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && (
        <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>
      )}

      <div className="card">
        <form onSubmit={handleLoad} className="flex flex-wrap items-end gap-4">
          <div className="min-w-[160px] flex-1">
            <label className="label">Renewal ID</label>
            <input
              type="number"
              className="input"
              value={renewalId}
              onChange={(e) => setRenewalId(e.target.value)}
              required
            />
          </div>
          <button className="btn-primary" type="submit" disabled={!renewalId || loading}>
            {loading ? 'Loading…' : 'Load'}
          </button>
        </form>
      </div>

      {renewal && (
        <div className="card space-y-4">
          <div className="grid gap-3 sm:grid-cols-2">
            <Detail label="Renewal ID" value={renewal.renewalId} />
            <Detail label="Certificate ID" value={renewal.certificateId} />
            <Detail label="Candidate ID" value={renewal.candidateId} />
            <Detail label="CPD Points Submitted" value={renewal.cpdPointsSubmitted} />
            <Detail label="Application Date" value={renewal.applicationDate} />
            <Detail label="New Valid Until" value={renewal.newValidUntil} />
            <div>
              <p className="text-sm text-gray-500">Status</p>
              <Badge>{renewal.status}</Badge>
            </div>
          </div>

          <div className="flex flex-wrap items-end gap-4 border-t border-gray-200 pt-4">
            <div className="min-w-[140px]">
              <label className="label">Extend Years</label>
              <input
                type="number"
                className="input"
                value={extendYears}
                onChange={(e) => setExtendYears(e.target.value)}
              />
            </div>
            <button
              className="btn-primary"
              onClick={() => handleReview('Approved')}
              disabled={submitting}
            >
              {submitting ? 'Working…' : 'Approve'}
            </button>
            <button
              className="btn-danger"
              onClick={() => handleReview('Rejected')}
              disabled={submitting}
            >
              {submitting ? 'Working…' : 'Reject'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function Detail({ label, value }) {
  return (
    <div>
      <p className="text-sm text-gray-500">{label}</p>
      <p className="font-medium text-gray-800">{value ?? '—'}</p>
    </div>
  );
}
