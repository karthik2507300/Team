import { useEffect, useState } from 'react';
import * as certificateService from '../../services/certificateService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

export default function ValidityTracker() {
  const [certificates, setCertificates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadExpiring();
  }, []);

  async function loadExpiring() {
    setLoading(true);
    setError('');
    try {
      const rows = await certificateService.expiring();
      setCertificates(rows ?? []);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load expiring certificates'));
    } finally {
      setLoading(false);
    }
  }

  const columns = [
    { key: 'certificateId', header: 'ID' },
    { key: 'certificateNumber', header: 'Certificate Number' },
    { key: 'candidateId', header: 'Candidate ID' },
    { key: 'programId', header: 'Program ID' },
    { key: 'validUntil', header: 'Valid Until' },
    { key: 'status', header: 'Status', render: (row) => <Badge>{row.status}</Badge> },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Validity Tracker</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <div className="card space-y-4">
        <h2 className="text-lg font-semibold text-gray-800">
          Certificates Expiring Within 90 Days
        </h2>

        {loading ? (
          <p className="text-gray-500">Loading…</p>
        ) : (
          <Table columns={columns} rows={certificates} empty="No certificates expiring soon." />
        )}
      </div>
    </div>
  );
}
