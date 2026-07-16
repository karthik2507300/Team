import { useEffect, useState } from 'react';
import * as enrolmentService from '../../services/enrolmentService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';

export default function EligibilityVerifier() {
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [page, setPage] = useState(0);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const res = await enrolmentService.list({
        eligibilityStatus: 'PendingVerification',
        page,
        limit: 10,
      });
      setData(res);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load enrolments'));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  async function decide(enrolmentId, status) {
    setError('');
    setSuccess('');
    try {
      await enrolmentService.updateEligibility(enrolmentId, status);
      setSuccess(`Enrolment marked ${status}.`);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to update eligibility'));
    }
  }

  const columns = [
    { key: 'enrolmentId', header: 'Enrolment' },
    { key: 'candidateId', header: 'Candidate' },
    { key: 'programId', header: 'Programme' },
    { key: 'attemptsUsed', header: 'Attempts Used' },
    { key: 'maxAttempts', header: 'Max Attempts' },
    { key: 'eligibilityStatus', header: 'Eligibility', render: (r) => <Badge>{r.eligibilityStatus}</Badge> },
    {
      key: 'actions',
      header: 'Actions',
      render: (r) => (
        <div className="flex gap-2">
          <button className="btn-primary" onClick={() => decide(r.enrolmentId, 'Eligible')}>Approve</button>
          <button className="btn-danger" onClick={() => decide(r.enrolmentId, 'Ineligible')}>Reject</button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Eligibility Verifier</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card">
        <Table
          columns={columns}
          rows={loading ? [] : data.content}
          empty={loading ? 'Loading…' : 'No enrolments pending verification.'}
        />
        <Pagination page={data.page ?? page} totalPages={data.totalPages} onChange={setPage} />
      </div>
    </div>
  );
}
