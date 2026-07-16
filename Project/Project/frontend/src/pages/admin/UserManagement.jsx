import { useEffect, useState } from 'react';
import * as userService from '../../services/userService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const ROLES = ['Candidate', 'CentreAdmin', 'ExamController', 'Evaluator', 'CertificationOfficer', 'Admin'];
const STAFF_ROLES = ['CentreAdmin', 'ExamController', 'Evaluator', 'CertificationOfficer', 'Admin'];
const STATUSES = ['Active', 'Inactive', 'Suspended'];

export default function UserManagement() {
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [role, setRole] = useState('All');
  const [status, setStatus] = useState('All');
  const [page, setPage] = useState(0);

  const [createOpen, setCreateOpen] = useState(false);
  const [createForm, setCreateForm] = useState({ name: '', email: '', phone: '', role: 'CentreAdmin' });
  const [editOpen, setEditOpen] = useState(false);
  const [editUser, setEditUser] = useState(null);
  const [editForm, setEditForm] = useState({ name: '', phone: '' });
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const params = { page, limit: 10 };
      if (role !== 'All') params.role = role;
      if (status !== 'All') params.status = status;
      const res = await userService.listUsers(params);
      setData(res);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load users'));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [role, status, page]);

  async function handleCreate(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      await userService.createStaff(createForm);
      setSuccess('Staff account created.');
      setCreateOpen(false);
      setCreateForm({ name: '', email: '', phone: '', role: 'CentreAdmin' });
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to create staff'));
    } finally {
      setSaving(false);
    }
  }

  function openEdit(user) {
    setEditUser(user);
    setEditForm({ name: user.name || '', phone: user.phone || '' });
    setEditOpen(true);
  }

  async function handleEdit(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      await userService.updateUser(editUser.userId, editForm);
      setSuccess('User updated.');
      setEditOpen(false);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to update user'));
    } finally {
      setSaving(false);
    }
  }

  async function toggleStatus(user) {
    const next = user.status === 'Suspended' ? 'Active' : 'Suspended';
    setError('');
    setSuccess('');
    try {
      await userService.updateUserStatus(user.userId, next);
      setSuccess(`User ${next === 'Suspended' ? 'suspended' : 'activated'}.`);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to update status'));
    }
  }

  const columns = [
    { key: 'userId', header: 'ID' },
    { key: 'name', header: 'Name' },
    { key: 'email', header: 'Email' },
    { key: 'phone', header: 'Phone' },
    { key: 'role', header: 'Role' },
    { key: 'status', header: 'Status', render: (r) => <Badge>{r.status}</Badge> },
    {
      key: 'actions',
      header: 'Actions',
      render: (r) => (
        <div className="flex gap-2">
          <button className="btn-secondary" onClick={() => openEdit(r)}>Edit</button>
          <button
            className={r.status === 'Suspended' ? 'btn-primary' : 'btn-danger'}
            onClick={() => toggleStatus(r)}
          >
            {r.status === 'Suspended' ? 'Activate' : 'Suspend'}
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">User Management</h1>
        <button className="btn-primary" onClick={() => { setError(''); setSuccess(''); setCreateOpen(true); }}>
          Create Staff
        </button>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card flex flex-wrap items-end gap-4">
        <div>
          <label className="label">Role</label>
          <select className="input" value={role} onChange={(e) => { setPage(0); setRole(e.target.value); }}>
            <option value="All">All</option>
            {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
          </select>
        </div>
        <div>
          <label className="label">Status</label>
          <select className="input" value={status} onChange={(e) => { setPage(0); setStatus(e.target.value); }}>
            <option value="All">All</option>
            {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
        </div>
      </div>

      <div className="card">
        <Table columns={columns} rows={loading ? [] : data.content} empty={loading ? 'Loading…' : 'No users found.'} />
        <Pagination page={data.page ?? page} totalPages={data.totalPages} onChange={setPage} />
      </div>

      <Modal
        open={createOpen}
        title="Create Staff"
        onClose={() => setCreateOpen(false)}
        footer={
          <>
            <button className="btn-secondary" onClick={() => setCreateOpen(false)}>Cancel</button>
            <button className="btn-primary" onClick={handleCreate} disabled={saving}>
              {saving ? 'Saving…' : 'Create'}
            </button>
          </>
        }
      >
        <form onSubmit={handleCreate} className="space-y-4">
          <div>
            <label className="label">Name</label>
            <input className="input" value={createForm.name}
              onChange={(e) => setCreateForm({ ...createForm, name: e.target.value })} required />
          </div>
          <div>
            <label className="label">Email</label>
            <input className="input" type="email" value={createForm.email}
              onChange={(e) => setCreateForm({ ...createForm, email: e.target.value })} required />
          </div>
          <div>
            <label className="label">Phone</label>
            <input className="input" value={createForm.phone}
              onChange={(e) => setCreateForm({ ...createForm, phone: e.target.value })} />
          </div>
          <div>
            <label className="label">Role</label>
            <select className="input" value={createForm.role}
              onChange={(e) => setCreateForm({ ...createForm, role: e.target.value })}>
              {STAFF_ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
        </form>
      </Modal>

      <Modal
        open={editOpen}
        title="Edit User"
        onClose={() => setEditOpen(false)}
        footer={
          <>
            <button className="btn-secondary" onClick={() => setEditOpen(false)}>Cancel</button>
            <button className="btn-primary" onClick={handleEdit} disabled={saving}>
              {saving ? 'Saving…' : 'Save'}
            </button>
          </>
        }
      >
        <form onSubmit={handleEdit} className="space-y-4">
          <div>
            <label className="label">Name</label>
            <input className="input" value={editForm.name}
              onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} required />
          </div>
          <div>
            <label className="label">Phone</label>
            <input className="input" value={editForm.phone}
              onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })} />
          </div>
        </form>
      </Modal>
    </div>
  );
}
