import { useEffect, useState } from 'react';
import * as questionService from '../../services/questionService';
import * as programService from '../../services/programService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const DIFFICULTIES = ['Easy', 'Medium', 'Hard'];
const TYPES = ['MCQ', 'Descriptive', 'CaseStudy', 'Practical'];

const emptyForm = {
  programId: '',
  topicTag: '',
  difficulty: 'Easy',
  questionText: '',
  type: 'MCQ',
  marks: '',
};

export default function QuestionBank() {
  const [programs, setPrograms] = useState([]);
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [programId, setProgramId] = useState('');
  const [difficulty, setDifficulty] = useState('All');
  const [type, setType] = useState('All');

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [modalOpen, setModalOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    programService
      .list({ limit: 100 })
      .then((d) => setPrograms(d.content ?? []))
      .catch((err) => setError(errorMessage(err, 'Failed to load programs')));
  }, []);

  useEffect(() => {
    load(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, programId, difficulty, type]);

  async function load(p) {
    setLoading(true);
    setError('');
    try {
      const params = { page: p, limit: 10 };
      if (programId) params.programId = programId;
      if (difficulty !== 'All') params.difficulty = difficulty;
      if (type !== 'All') params.type = type;
      const data = await questionService.list(params);
      setRows(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load questions'));
    } finally {
      setLoading(false);
    }
  }

  function openNew() {
    setEditId(null);
    setForm(emptyForm);
    setModalOpen(true);
  }

  function openEdit(row) {
    setEditId(row.questionId);
    setForm({
      programId: row.programId ?? '',
      topicTag: row.topicTag ?? '',
      difficulty: row.difficulty ?? 'Easy',
      questionText: row.questionText ?? '',
      type: row.type ?? 'MCQ',
      marks: row.marks ?? '',
    });
    setModalOpen(true);
  }

  function setField(key, value) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      const body = {
        programId: Number(form.programId),
        topicTag: form.topicTag,
        difficulty: form.difficulty,
        questionText: form.questionText,
        type: form.type,
        marks: Number(form.marks),
      };
      if (editId) {
        await questionService.update(editId, body);
        setSuccess('Question updated successfully.');
      } else {
        await questionService.create(body);
        setSuccess('Question created successfully.');
      }
      setModalOpen(false);
      load(page);
    } catch (err) {
      setError(errorMessage(err, 'Failed to save question'));
    } finally {
      setSaving(false);
    }
  }

  const columns = [
    { key: 'questionId', header: 'ID' },
    { key: 'topicTag', header: 'Topic' },
    { key: 'difficulty', header: 'Difficulty', render: (r) => <Badge>{r.difficulty}</Badge> },
    { key: 'type', header: 'Type' },
    { key: 'marks', header: 'Marks' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: '',
      render: (r) => (
        <button className="btn-secondary" onClick={() => openEdit(r)}>
          Edit
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Question Bank</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card flex flex-wrap items-end gap-4">
        <div>
          <label className="label">Program</label>
          <select
            className="input"
            value={programId}
            onChange={(e) => {
              setPage(0);
              setProgramId(e.target.value);
            }}
          >
            <option value="">All Programs</option>
            {programs.map((p) => (
              <option key={p.programId} value={p.programId}>
                {p.programName}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="label">Difficulty</label>
          <select
            className="input"
            value={difficulty}
            onChange={(e) => {
              setPage(0);
              setDifficulty(e.target.value);
            }}
          >
            <option value="All">All</option>
            {DIFFICULTIES.map((d) => (
              <option key={d} value={d}>
                {d}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="label">Type</label>
          <select
            className="input"
            value={type}
            onChange={(e) => {
              setPage(0);
              setType(e.target.value);
            }}
          >
            <option value="All">All</option>
            {TYPES.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </div>
        <button className="btn-primary ml-auto" onClick={openNew}>
          New Question
        </button>
      </div>

      {loading ? (
        <p className="text-gray-500">Loading…</p>
      ) : (
        <>
          <Table columns={columns} rows={rows} empty="No questions found." />
          <Pagination page={page} totalPages={totalPages} onChange={setPage} />
        </>
      )}

      <Modal
        open={modalOpen}
        title={editId ? 'Edit Question' : 'New Question'}
        onClose={() => setModalOpen(false)}
        footer={
          <>
            <button className="btn-secondary" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button className="btn-primary" form="question-form" type="submit" disabled={saving}>
              {saving ? 'Saving…' : 'Save'}
            </button>
          </>
        }
      >
        <form id="question-form" onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="label">Program</label>
            <select
              className="input"
              value={form.programId}
              onChange={(e) => setField('programId', e.target.value)}
              required
            >
              <option value="">Select a program…</option>
              {programs.map((p) => (
                <option key={p.programId} value={p.programId}>
                  {p.programName}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Topic Tag</label>
            <input
              className="input"
              value={form.topicTag}
              onChange={(e) => setField('topicTag', e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label">Difficulty</label>
            <select
              className="input"
              value={form.difficulty}
              onChange={(e) => setField('difficulty', e.target.value)}
            >
              {DIFFICULTIES.map((d) => (
                <option key={d} value={d}>
                  {d}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Question Text</label>
            <textarea
              className="input"
              rows={4}
              value={form.questionText}
              onChange={(e) => setField('questionText', e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label">Type</label>
            <select className="input" value={form.type} onChange={(e) => setField('type', e.target.value)}>
              {TYPES.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Marks</label>
            <input
              className="input"
              type="number"
              value={form.marks}
              onChange={(e) => setField('marks', e.target.value)}
              required
            />
          </div>
        </form>
      </Modal>
    </div>
  );
}
