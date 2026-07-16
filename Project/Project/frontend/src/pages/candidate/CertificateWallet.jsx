import { useEffect, useState } from 'react';
import * as candidateService from '../../services/candidateService';
import * as certificateService from '../../services/certificateService';
import { errorMessage } from '../../services/http';
import Badge from '../../components/Badge';

export default function CertificateWallet() {
  const [certificates, setCertificates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

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

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Certificate Wallet</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : certificates.length === 0 ? (
        <p className="text-gray-400">No certificates issued yet.</p>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {certificates.map((c) => (
            <div key={c.certificateId} className="card">
              <div className="mb-2 flex items-center justify-between">
                <span className="font-semibold text-gray-800">{c.certificateNumber}</span>
                <Badge>{c.status}</Badge>
              </div>
              <dl className="space-y-1 text-sm text-gray-600">
                <div className="flex justify-between">
                  <dt className="text-gray-500">Program</dt>
                  <dd>{c.programId}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-gray-500">Issued</dt>
                  <dd>{c.issuedDate}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-gray-500">Valid Until</dt>
                  <dd>{c.validUntil}</dd>
                </div>
              </dl>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
