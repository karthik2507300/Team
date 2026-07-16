import { useEffect, useState } from 'react';
import * as examWindowService from '../../services/examWindowService';
import * as programService from '../../services/programService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const STATUSES = ['Upcoming', 'Open', 'Closed', 'ResultsPublished'];

const EMPTY = {
  programId: '',
  examName: '',
  startDate: '',
  endDate: '',
  registrationDeadline: '',
  resultDate: '',
  status: 'Upcoming',
};

export default function ExamWindowManager() {
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [programs, setPrograms] = useState([]);
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
      const res = await examWindowService.list({ page, limit: 10 });
      setData(res);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load exam windows'));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  useEffect(() => {
    programService.list({ limit: 100 })
      .then((res) => setPrograms(res.content || []))
      .catch(() => {});
  }, []);

  function openNew() {
    setEditId(null);
    setForm(EMPTY);
    setError('');
    setSuccess('');
    setModalOpen(true);
  }

  function openEdit(w) {
    setEditId(w.windowId);
    setForm({
      programId: w.programId ?? '',
      examName: w.examName || '',
      startDate: w.startDate || '',
      endDate: w.endDate || '',
      registrationDeadline: w.registrationDeadline || '',
      resultDate: w.resultDate || '',
      status: w.status || 'Upcoming',
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
      programId: form.programId === '' ? null : Number(form.programId),
      examName: form.examName,
      startDate: form.startDate,
      endDate: form.endDate,
      registrationDeadline: form.registrationDeadline,
      resultDate: form.resultDate,
    };
    try {
      if (editId) {
        body.status = form.status;
        await examWindowService.update(editId, body);
        setSuccess('Exam window updated.');
      } else {
        await examWindowService.create(body);
        setSuccess('Exam window created.');
      }
      setModalOpen(false);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to save exam window'));
    } finally {
      setSaving(false);
    }
  }

  const columns = [
    { key: 'windowId', header: 'ID' },
    { key: 'programId', header: 'Programme' },
    { key: 'examName', header: 'Exam' },
    { key: 'startDate', header: 'Start' },
    { key: 'endDate', header: 'End' },
    { key: 'registrationDeadline', header: 'Reg. Deadline' },
    { key: 'resultDate', header: 'Result Date' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: 'Actions',
      render: (r) => (
        <button className="btn-secondary" onClick={() => openEdit(r)}>Edit</button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Exam Window Manager</h1>
        <button className="btn-primary" onClick={openNew}>New Window</button>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card">
        <Table columns={columns} rows={loading ? [] : data.content} empty={loading ? 'Loading…' : 'No exam windows found.'} />
        <Pagination page={data.page ?? page} totalPages={data.totalPages} onChange={setPage} />
      </div>

      <Modal
        open={modalOpen}
        title={editId ? 'Edit Exam Window' : 'New Exam Window'}
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
            <label className="label">Programme</label>
            <select className="input" value={form.programId}
              onChange={(e) => setForm({ ...form, programId: e.target.value })} required>
              <option value="">Select a programme…</option>
              {programs.map((p) => (
                <option key={p.programId} value={p.programId}>{p.programName}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Exam Name</label>
            <input className="input" value={form.examName}
              onChange={(e) => setForm({ ...form, examName: e.target.value })} required />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Start Date</label>
              <input className="input" type="date" value={form.startDate}
                onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
            </div>
            <div>
              <label className="label">End Date</label>
              <input className="input" type="date" value={form.endDate}
                onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
            </div>
            <div>
              <label className="label">Registration Deadline</label>
              <input className="input" type="date" value={form.registrationDeadline}
                onChange={(e) => setForm({ ...form, registrationDeadline: e.target.value })} />
            </div>
            <div>
              <label className="label">Result Date</label>
              <input className="input" type="date" value={form.resultDate}
                onChange={(e) => setForm({ ...form, resultDate: e.target.value })} />
            </div>
          </div>
          {editId && (
            <div>
              <label className="label">Status</label>
              <select className="input" value={form.status}
                onChange={(e) => setForm({ ...form, status: e.target.value })}>
                {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          )}
        </form>
      </Modal>
    </div>
  );
}
