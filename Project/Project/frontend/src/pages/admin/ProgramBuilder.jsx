import { useEffect, useState } from 'react';
import * as programService from '../../services/programService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const LEVELS = ['Foundation', 'Associate', 'Professional', 'Fellow'];

const EMPTY = {
  programName: '',
  body: '',
  level: 'Foundation',
  eligibilityCriteria: '',
  examFee: '',
  validityYears: '',
  maxAttempts: '',
};

export default function ProgramBuilder() {
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [page, setPage] = useState(0);

  const [modalOpen, setModalOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const res = await programService.list({ page, limit: 10 });
      setData(res);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load programmes'));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  function openNew() {
    setEditId(null);
    setForm(EMPTY);
    setError('');
    setSuccess('');
    setModalOpen(true);
  }

  function openEdit(p) {
    setEditId(p.programId);
    setForm({
      programName: p.programName || '',
      body: p.body || '',
      level: p.level || 'Foundation',
      eligibilityCriteria: p.eligibilityCriteria || '',
      examFee: p.examFee ?? '',
      validityYears: p.validityYears ?? '',
      maxAttempts: p.maxAttempts ?? '',
    });
    setError('');
    setSuccess('');
    setModalOpen(true);
  }

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    const body = {
      programName: form.programName,
      body: form.body,
      level: form.level,
      eligibilityCriteria: form.eligibilityCriteria,
      examFee: form.examFee === '' ? null : Number(form.examFee),
      validityYears: form.validityYears === '' ? null : Number(form.validityYears),
      maxAttempts: form.maxAttempts === '' ? null : Number(form.maxAttempts),
    };
    try {
      if (editId) {
        await programService.update(editId, body);
        setSuccess('Programme updated.');
      } else {
        await programService.create(body);
        setSuccess('Programme created.');
      }
      setModalOpen(false);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to save programme'));
    } finally {
      setSaving(false);
    }
  }

  async function toggleStatus(p) {
    const next = p.status === 'Discontinued' ? 'Active' : 'Discontinued';
    setError('');
    setSuccess('');
    try {
      await programService.update(p.programId, { status: next });
      setSuccess(`Programme ${next === 'Discontinued' ? 'discontinued' : 'activated'}.`);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to update status'));
    }
  }

  const columns = [
    { key: 'programName', header: 'Programme' },
    { key: 'body', header: 'Body' },
    { key: 'level', header: 'Level' },
    { key: 'examFee', header: 'Exam Fee' },
    { key: 'validityYears', header: 'Validity (yrs)' },
    { key: 'maxAttempts', header: 'Max Attempts' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: 'Actions',
      render: (r) => (
        <div className="flex gap-2">
          <button className="btn-secondary" onClick={() => openEdit(r)}>Edit</button>
          <button
            className={r.status === 'Discontinued' ? 'btn-primary' : 'btn-danger'}
            onClick={() => toggleStatus(r)}
          >
            {r.status === 'Discontinued' ? 'Activate' : 'Discontinue'}
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Programme Builder</h1>
        <button className="btn-primary" onClick={openNew}>New Programme</button>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card">
        <Table columns={columns} rows={loading ? [] : data.content} empty={loading ? 'Loading…' : 'No programmes found.'} />
        <Pagination page={data.page ?? page} totalPages={data.totalPages} onChange={setPage} />
      </div>

      <Modal
        open={modalOpen}
        title={editId ? 'Edit Programme' : 'New Programme'}
        onClose={() => setModalOpen(false)}
        footer={
          <>
            <button className="btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button className="btn-primary" onClick={handleSave} disabled={saving}>
              {saving ? 'Saving…' : 'Save'}
            </button>
          </>
        }
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="label">Programme Name</label>
            <input className="input" value={form.programName}
              onChange={(e) => setForm({ ...form, programName: e.target.value })} required />
          </div>
          <div>
            <label className="label">Body</label>
            <input className="input" value={form.body}
              onChange={(e) => setForm({ ...form, body: e.target.value })} />
          </div>
          <div>
            <label className="label">Level</label>
            <select className="input" value={form.level}
              onChange={(e) => setForm({ ...form, level: e.target.value })}>
              {LEVELS.map((l) => <option key={l} value={l}>{l}</option>)}
            </select>
          </div>
          <div>
            <label className="label">Eligibility Criteria</label>
            <textarea className="input" rows={3} value={form.eligibilityCriteria}
              onChange={(e) => setForm({ ...form, eligibilityCriteria: e.target.value })} />
          </div>
          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="label">Exam Fee</label>
              <input className="input" type="number" value={form.examFee}
                onChange={(e) => setForm({ ...form, examFee: e.target.value })} />
            </div>
            <div>
              <label className="label">Validity (yrs)</label>
              <input className="input" type="number" value={form.validityYears}
                onChange={(e) => setForm({ ...form, validityYears: e.target.value })} />
            </div>
            <div>
              <label className="label">Max Attempts</label>
              <input className="input" type="number" value={form.maxAttempts}
                onChange={(e) => setForm({ ...form, maxAttempts: e.target.value })} />
            </div>
          </div>
        </form>
      </Modal>
    </div>
  );
}
