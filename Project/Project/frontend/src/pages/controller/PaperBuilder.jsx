import { useEffect, useState } from 'react';
import * as paperService from '../../services/paperService';
import * as examWindowService from '../../services/examWindowService';
import * as programService from '../../services/programService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';

const emptyCreate = {
  windowId: '',
  programId: '',
  paperCode: '',
  duration: '',
  instructionsRef: '',
};

const emptyQuestion = {
  questionId: '',
  sequenceOrder: '',
  marksAllocated: '',
};

export default function PaperBuilder() {
  const [windows, setWindows] = useState([]);
  const [programs, setPrograms] = useState([]);

  const [createForm, setCreateForm] = useState(emptyCreate);
  const [paperId, setPaperId] = useState(null);
  const [paper, setPaper] = useState(null);
  const [qForm, setQForm] = useState(emptyQuestion);

  const [creating, setCreating] = useState(false);
  const [adding, setAdding] = useState(false);
  const [loadingPaper, setLoadingPaper] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    Promise.all([examWindowService.list({ limit: 100 }), programService.list({ limit: 100 })])
      .then(([w, p]) => {
        setWindows(w.content ?? []);
        setPrograms(p.content ?? []);
      })
      .catch((err) => setError(errorMessage(err, 'Failed to load options')));
  }, []);

  function setCreateField(key, value) {
    setCreateForm((f) => ({ ...f, [key]: value }));
  }
  function setQField(key, value) {
    setQForm((f) => ({ ...f, [key]: value }));
  }

  async function loadPaper(id) {
    setLoadingPaper(true);
    setError('');
    try {
      const data = await paperService.get(id);
      setPaper(data);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load paper'));
    } finally {
      setLoadingPaper(false);
    }
  }

  async function handleCreate(e) {
    e.preventDefault();
    setCreating(true);
    setError('');
    setSuccess('');
    try {
      const body = {
        windowId: Number(createForm.windowId),
        programId: Number(createForm.programId),
        paperCode: createForm.paperCode,
        duration: Number(createForm.duration),
        instructionsRef: createForm.instructionsRef,
      };
      const created = await paperService.create(body);
      setPaperId(created.paperId);
      setSuccess(`Paper created — ID ${created.paperId}.`);
      await loadPaper(created.paperId);
    } catch (err) {
      setError(errorMessage(err, 'Failed to create paper'));
    } finally {
      setCreating(false);
    }
  }

  async function handleAddQuestion(e) {
    e.preventDefault();
    if (!paperId) return;
    setAdding(true);
    setError('');
    setSuccess('');
    try {
      const item = {
        questionId: Number(qForm.questionId),
        sequenceOrder: Number(qForm.sequenceOrder),
        marksAllocated: Number(qForm.marksAllocated),
      };
      await paperService.addQuestions(paperId, [item]);
      setSuccess('Question added to paper.');
      setQForm(emptyQuestion);
      await loadPaper(paperId);
    } catch (err) {
      setError(errorMessage(err, 'Failed to add question'));
    } finally {
      setAdding(false);
    }
  }

  const questionColumns = [
    { key: 'paperQuestionId', header: 'Paper Q ID' },
    { key: 'questionId', header: 'Question ID' },
    { key: 'sequenceOrder', header: 'Sequence' },
    { key: 'marksAllocated', header: 'Marks' },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Paper Builder</h1>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <form onSubmit={handleCreate} className="card max-w-lg space-y-4">
        <h2 className="text-lg font-semibold text-gray-700">Create Paper</h2>
        <div>
          <label className="label">Exam Window</label>
          <select
            className="input"
            value={createForm.windowId}
            onChange={(e) => setCreateField('windowId', e.target.value)}
            required
          >
            <option value="">Select an exam window…</option>
            {windows.map((w) => (
              <option key={w.windowId} value={w.windowId}>
                {w.examName}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="label">Program</label>
          <select
            className="input"
            value={createForm.programId}
            onChange={(e) => setCreateField('programId', e.target.value)}
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
          <label className="label">Paper Code</label>
          <input
            className="input"
            value={createForm.paperCode}
            onChange={(e) => setCreateField('paperCode', e.target.value)}
            required
          />
        </div>
        <div>
          <label className="label">Duration (minutes)</label>
          <input
            className="input"
            type="number"
            value={createForm.duration}
            onChange={(e) => setCreateField('duration', e.target.value)}
            required
          />
        </div>
        <div>
          <label className="label">Instructions Reference</label>
          <textarea
            className="input"
            rows={3}
            value={createForm.instructionsRef}
            onChange={(e) => setCreateField('instructionsRef', e.target.value)}
          />
        </div>
        <button type="submit" className="btn-primary" disabled={creating}>
          {creating ? 'Creating…' : 'Create Paper'}
        </button>
      </form>

      {loadingPaper && <p className="text-gray-500">Loading paper…</p>}

      {paper && (
        <div className="card space-y-4">
          <div className="flex flex-wrap items-center gap-6">
            <h2 className="text-lg font-semibold text-gray-700">
              Paper {paper.paperCode} (ID {paper.paperId})
            </h2>
            <span className="text-sm text-gray-600">
              Total Marks: <strong>{paper.totalMarks}</strong>
            </span>
            <span className="text-sm text-gray-600">
              Status: <Badge>{paper.status}</Badge>
            </span>
          </div>

          <Table columns={questionColumns} rows={paper.questions} empty="No questions added yet." />

          <form onSubmit={handleAddQuestion} className="flex flex-wrap items-end gap-4 border-t border-gray-200 pt-4">
            <div>
              <label className="label">Question ID</label>
              <input
                className="input"
                type="number"
                value={qForm.questionId}
                onChange={(e) => setQField('questionId', e.target.value)}
                required
              />
            </div>
            <div>
              <label className="label">Sequence Order</label>
              <input
                className="input"
                type="number"
                value={qForm.sequenceOrder}
                onChange={(e) => setQField('sequenceOrder', e.target.value)}
                required
              />
            </div>
            <div>
              <label className="label">Marks Allocated</label>
              <input
                className="input"
                type="number"
                value={qForm.marksAllocated}
                onChange={(e) => setQField('marksAllocated', e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn-primary" disabled={adding}>
              {adding ? 'Adding…' : 'Add Question'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
