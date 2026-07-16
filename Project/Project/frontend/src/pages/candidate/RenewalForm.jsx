import { useEffect, useState } from 'react';
import * as candidateService from '../../services/candidateService';
import * as certificateService from '../../services/certificateService';
import * as renewalService from '../../services/renewalService';
import { errorMessage } from '../../services/http';

export default function RenewalForm() {
  const [certificates, setCertificates] = useState([]);
  const [certificateId, setCertificateId] = useState('');
  const [cpdPointsSubmitted, setCpdPointsSubmitted] = useState('');
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
      const me = await candidateService.me();
      const data = await certificateService.byCandidate(me.candidateId);
      setCertificates(data ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load certificates'));
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
      await renewalService.submit({
        certificateId,
        cpdPointsSubmitted: Number(cpdPointsSubmitted),
      });
      setSuccess('Renewal request submitted successfully.');
      setCertificateId('');
      setCpdPointsSubmitted('');
    } catch (err) {
      setError(errorMessage(err, 'Failed to submit renewal request'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Renew Certificate</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <form onSubmit={handleSubmit} className="card max-w-lg space-y-4">
          <div>
            <label className="label">Certificate</label>
            <select className="input" value={certificateId} onChange={(e) => setCertificateId(e.target.value)} required>
              <option value="">Select a certificate…</option>
              {certificates.map((c) => (
                <option key={c.certificateId} value={c.certificateId}>
                  {c.certificateNumber} (valid until {c.validUntil})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">CPD Points Submitted</label>
            <input
              type="number"
              className="input"
              value={cpdPointsSubmitted}
              onChange={(e) => setCpdPointsSubmitted(e.target.value)}
              min="0"
              required
            />
          </div>
          <button type="submit" className="btn-primary" disabled={submitting}>
            {submitting ? 'Submitting…' : 'Submit Renewal'}
          </button>
        </form>
      )}
    </div>
  );
}
