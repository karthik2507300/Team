import { useState } from 'react';
import * as certificateService from '../../services/certificateService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

const STATUSES = ['Valid', 'Expired', 'Revoked', 'Suspended'];

export default function IssuanceQueue() {
  // Issue form
  const [issueCandidateId, setIssueCandidateId] = useState('');
  const [issueProgramId, setIssueProgramId] = useState('');
  const [issuing, setIssuing] = useState(false);
  const [issueError, setIssueError] = useState('');
  const [issueSuccess, setIssueSuccess] = useState('');

  // Lookup
  const [lookupCandidateId, setLookupCandidateId] = useState('');
  const [certificates, setCertificates] = useState([]);
  const [loadedCandidateId, setLoadedCandidateId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [lookupError, setLookupError] = useState('');

  // Per-row status selection (keyed by certificateId)
  const [rowStatus, setRowStatus] = useState({});
  const [updatingId, setUpdatingId] = useState(null);

  async function handleIssue(e) {
    e.preventDefault();
    setIssuing(true);
    setIssueError('');
    setIssueSuccess('');
    try {
      const cert = await certificateService.issue({
        candidateId: Number(issueCandidateId),
        programId: Number(issueProgramId),
      });
      setIssueSuccess(`Certificate issued: ${cert.certificateNumber}`);
      setIssueCandidateId('');
      setIssueProgramId('');
      // Refresh lookup table if it shows the same candidate.
      if (loadedCandidateId != null && loadedCandidateId === cert.candidateId) {
        loadCertificates(loadedCandidateId);
      }
    } catch (err) {
      setIssueError(errorMessage(err, 'Failed to issue certificate'));
    } finally {
      setIssuing(false);
    }
  }

  async function loadCertificates(candidateId) {
    setLoading(true);
    setLookupError('');
    try {
      const rows = await certificateService.byCandidate(Number(candidateId));
      setCertificates(rows ?? []);
      setLoadedCandidateId(Number(candidateId));
    } catch (err) {
      setLookupError(errorMessage(err, 'Failed to load certificates'));
    } finally {
      setLoading(false);
    }
  }

  async function handleLookup(e) {
    e.preventDefault();
    if (!lookupCandidateId) return;
    loadCertificates(lookupCandidateId);
  }

  async function handleUpdateStatus(row) {
    const status = rowStatus[row.certificateId] ?? row.status;
    setUpdatingId(row.certificateId);
    setLookupError('');
    try {
      await certificateService.updateStatus(row.certificateId, status);
      if (loadedCandidateId != null) loadCertificates(loadedCandidateId);
    } catch (err) {
      setLookupError(errorMessage(err, 'Failed to update status'));
    } finally {
      setUpdatingId(null);
    }
  }

  const columns = [
    { key: 'certificateId', header: 'ID' },
    { key: 'certificateNumber', header: 'Certificate Number' },
    { key: 'programId', header: 'Program ID' },
    { key: 'issuedDate', header: 'Issued Date' },
    { key: 'validUntil', header: 'Valid Until' },
    { key: 'status', header: 'Status', render: (row) => <Badge>{row.status}</Badge> },
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-2">
          <select
            className="input"
            value={rowStatus[row.certificateId] ?? row.status}
            onChange={(e) =>
              setRowStatus((s) => ({ ...s, [row.certificateId]: e.target.value }))
            }
          >
            {STATUSES.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
          <button
            className="btn-secondary"
            onClick={() => handleUpdateStatus(row)}
            disabled={updatingId === row.certificateId}
          >
            {updatingId === row.certificateId ? 'Updating…' : 'Update'}
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Issuance Queue</h1>

      {/* Issue Certificate */}
      <div className="card space-y-4">
        <h2 className="text-lg font-semibold text-gray-800">Issue Certificate</h2>

        {issueError && (
          <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{issueError}</div>
        )}
        {issueSuccess && (
          <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">
            {issueSuccess}
          </div>
        )}

        <form onSubmit={handleIssue} className="flex flex-wrap items-end gap-4">
          <div className="min-w-[160px] flex-1">
            <label className="label">Candidate ID</label>
            <input
              type="number"
              className="input"
              value={issueCandidateId}
              onChange={(e) => setIssueCandidateId(e.target.value)}
              required
            />
          </div>
          <div className="min-w-[160px] flex-1">
            <label className="label">Program ID</label>
            <input
              type="number"
              className="input"
              value={issueProgramId}
              onChange={(e) => setIssueProgramId(e.target.value)}
              required
            />
          </div>
          <button className="btn-primary" type="submit" disabled={issuing}>
            {issuing ? 'Issuing…' : 'Issue Certificate'}
          </button>
        </form>
      </div>

      {/* Lookup candidate certificates */}
      <div className="card space-y-4">
        <h2 className="text-lg font-semibold text-gray-800">Lookup candidate certificates</h2>

        {lookupError && (
          <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{lookupError}</div>
        )}

        <form onSubmit={handleLookup} className="flex flex-wrap items-end gap-4">
          <div className="min-w-[160px] flex-1">
            <label className="label">Candidate ID</label>
            <input
              type="number"
              className="input"
              value={lookupCandidateId}
              onChange={(e) => setLookupCandidateId(e.target.value)}
              required
            />
          </div>
          <button className="btn-primary" type="submit" disabled={!lookupCandidateId || loading}>
            {loading ? 'Loading…' : 'Load'}
          </button>
        </form>

        {loadedCandidateId != null && (
          <Table columns={columns} rows={certificates} empty="No certificates found." />
        )}
      </div>
    </div>
  );
}
