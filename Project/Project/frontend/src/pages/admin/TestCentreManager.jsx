import { useEffect, useState } from 'react';
import * as testCentreService from '../../services/testCentreService';
import { errorMessage } from '../../services/http';
import Table from '../../components/Table';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const STATUSES = ['Active', 'Inactive', 'Blacklisted'];

const EMPTY = {
  centreName: '',
  city: '',
  address: '',
  capacity: '',
  contactPerson: '',
  status: 'Active',
};

export default function TestCentreManager() {
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
      const res = await testCentreService.list({ page, limit: 10 });
      setData(res);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load test centres'));
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

  function openEdit(c) {
    setEditId(c.centreId);
    setForm({
      centreName: c.centreName || '',
      city: c.city || '',
      address: c.address || '',
      capacity: c.capacity ?? '',
      contactPerson: c.contactPerson || '',
      status: c.status || 'Active',
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
      centreName: form.centreName,
      city: form.city,
      address: form.address,
      capacity: form.capacity === '' ? null : Number(form.capacity),
      contactPerson: form.contactPerson,
    };
    try {
      if (editId) {
        body.status = form.status;
        await testCentreService.update(editId, body);
        setSuccess('Test centre updated.');
      } else {
        await testCentreService.create(body);
        setSuccess('Test centre created.');
      }
      setModalOpen(false);
      load();
    } catch (err) {
      setError(errorMessage(err, 'Failed to save test centre'));
    } finally {
      setSaving(false);
    }
  }

  const columns = [
    { key: 'centreId', header: 'ID' },
    { key: 'centreName', header: 'Name' },
    { key: 'city', header: 'City' },
    { key: 'capacity', header: 'Capacity' },
    { key: 'contactPerson', header: 'Contact' },
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
        <h1 className="text-2xl font-bold text-gray-800">Test Centre Manager</h1>
        <button className="btn-primary" onClick={openNew}>New Centre</button>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {success && <div className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">{success}</div>}

      <div className="card">
        <Table columns={columns} rows={loading ? [] : data.content} empty={loading ? 'Loading…' : 'No test centres found.'} />
        <Pagination page={data.page ?? page} totalPages={data.totalPages} onChange={setPage} />
      </div>

      <Modal
        open={modalOpen}
        title={editId ? 'Edit Test Centre' : 'New Test Centre'}
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
            <label className="label">Centre Name</label>
            <input className="input" value={form.centreName}
              onChange={(e) => setForm({ ...form, centreName: e.target.value })} required />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">City</label>
              <input className="input" value={form.city}
                onChange={(e) => setForm({ ...form, city: e.target.value })} />
            </div>
            <div>
              <label className="label">Capacity</label>
              <input className="input" type="number" value={form.capacity}
                onChange={(e) => setForm({ ...form, capacity: e.target.value })} />
            </div>
          </div>
          <div>
            <label className="label">Address</label>
            <textarea className="input" rows={2} value={form.address}
              onChange={(e) => setForm({ ...form, address: e.target.value })} />
          </div>
          <div>
            <label className="label">Contact Person</label>
            <input className="input" value={form.contactPerson}
              onChange={(e) => setForm({ ...form, contactPerson: e.target.value })} />
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
