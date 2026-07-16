import { useEffect, useState } from 'react';
import * as programService from '../../services/programService';
import { errorMessage } from '../../services/http';

const EMPTY_BAND = { gradeLetter: '', minPercentage: '', maxPercentage: '', isPassing: false };

export default function GradingScale() {
  const [programs, setPrograms] = useState([]);
  const [programId, setProgramId] = useState('');
  const [bands, setBands] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    async function loadPrograms() {
      setError('');
      try {
        const res = await programService.list({ limit: 100 });
        setPrograms(res.content || []);
      } catch (err) {
        setError(errorMessage(err, 'Failed to load programmes'));
      }
    }
    loadPrograms();
  }, []);

  async function handleSelect(id) {
    setProgramId(id);
    setSuccess('');
    setError('');
    setBands([]);
    if (!id) return;
    setLoading(true);
    try {
      const scale = await programService.getGradingScale(id);
      setBands(
        (scale || []).map((b) => ({
          gradeLetter: b.gradeLetter ?? '',
          minPercentage: b.minPercentage ?? '',
          maxPercentage: b.maxPercentage ?? '',
          isPassing: !!b.isPassing,
        }))
      );
    } catch (err) {
      setError(errorMessage(err, 'Failed to load grading scale'));
    } finally {
      setLoading(false);
    }
  }

  function updateBand(idx, key, value) {
    setBands((prev) => prev.map((b, i) => (i === idx ? { ...b, [key]: value } : b)));
  }

  function addBand() {
    setBands((prev) => [...prev, { ...EMPTY_BAND }]);
  }

  function removeBand(idx) {
    setBands((prev) => prev.filter((_, i) => i !== idx));
  }

  async function handleSave() {
    setSaving(true);
    setError('');
    setSuccess('');
    const payload = bands.map((b) => ({
      gradeLetter: b.gradeLetter,
      minPercentage: b.minPercentage === '' ? null : Number(b.minPercentage),
      maxPercentage: b.maxPercentage === '' ? null : Number(b.maxPercentage),
      isPassing: !!b.isPassing,
    }));
    try {
      await programService.setGradingScale(programId, payload);
      setSuccess('Grading scale saved.');
    } catch (err) {
      setError(errorMessage(err, 'Failed to save grading scale'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Grading Scale</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card">
        <label className="label">Programme</label>
        <select className="input max-w-md" value={programId} onChange={(e) => handleSelect(e.target.value)}>
          <option value="">Select a programme…</option>
          {programs.map((p) => (
            <option key={p.programId} value={p.programId}>{p.programName}</option>
          ))}
        </select>
      </div>

      {programId && (
        <div className="card space-y-4">
          {loading ? (
            <p className="text-gray-400">Loading…</p>
          ) : (
            <>
              <div className="table-wrap">
                <table className="min-w-full divide-y divide-gray-200 text-sm">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-3 text-left font-semibold text-gray-600">Grade</th>
                      <th className="px-4 py-3 text-left font-semibold text-gray-600">Min %</th>
                      <th className="px-4 py-3 text-left font-semibold text-gray-600">Max %</th>
                      <th className="px-4 py-3 text-left font-semibold text-gray-600">Passing</th>
                      <th className="px-4 py-3 text-left font-semibold text-gray-600"></th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {bands.length === 0 ? (
                      <tr>
                        <td colSpan={5} className="px-4 py-8 text-center text-gray-400">No bands yet.</td>
                      </tr>
                    ) : (
                      bands.map((b, idx) => (
                        <tr key={idx}>
                          <td className="px-4 py-2">
                            <input className="input" value={b.gradeLetter}
                              onChange={(e) => updateBand(idx, 'gradeLetter', e.target.value)} />
                          </td>
                          <td className="px-4 py-2">
                            <input className="input" type="number" value={b.minPercentage}
                              onChange={(e) => updateBand(idx, 'minPercentage', e.target.value)} />
                          </td>
                          <td className="px-4 py-2">
                            <input className="input" type="number" value={b.maxPercentage}
                              onChange={(e) => updateBand(idx, 'maxPercentage', e.target.value)} />
                          </td>
                          <td className="px-4 py-2 text-center">
                            <input type="checkbox" checked={b.isPassing}
                              onChange={(e) => updateBand(idx, 'isPassing', e.target.checked)} />
                          </td>
                          <td className="px-4 py-2">
                            <button className="btn-danger" onClick={() => removeBand(idx)}>Remove</button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              <div className="flex gap-2">
                <button className="btn-secondary" onClick={addBand}>Add band</button>
                <button className="btn-primary" onClick={handleSave} disabled={saving}>
                  {saving ? 'Saving…' : 'Save Grading Scale'}
                </button>
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}
