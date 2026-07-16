import { useEffect, useState } from 'react';
import * as programService from '../../services/programService';
import * as userService from '../../services/userService';
import * as examWindowService from '../../services/examWindowService';
import { errorMessage } from '../../services/http';

export default function Dashboard() {
  const [stats, setStats] = useState({ programs: 0, users: 0, windows: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    async function load() {
      setLoading(true);
      setError('');
      try {
        const [programs, users, windows] = await Promise.all([
          programService.list({ limit: 1 }),
          userService.listUsers({ limit: 1 }),
          examWindowService.list({ limit: 1 }),
        ]);
        if (!active) return;
        setStats({
          programs: programs.totalElements ?? 0,
          users: users.totalElements ?? 0,
          windows: windows.totalElements ?? 0,
        });
      } catch (err) {
        if (active) setError(errorMessage(err, 'Failed to load dashboard'));
      } finally {
        if (active) setLoading(false);
      }
    }
    load();
    return () => {
      active = false;
    };
  }, []);

  const cards = [
    { label: 'Active Programs', value: stats.programs },
    { label: 'Total Users', value: stats.users },
    { label: 'Exam Windows', value: stats.windows },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Admin Dashboard</h1>

      {error && (
        <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>
      )}

      <div className="grid gap-4 sm:grid-cols-3">
        {cards.map((c) => (
          <div key={c.label} className="card">
            <div className="text-3xl font-bold text-brand-700">
              {loading ? '…' : c.value}
            </div>
            <div className="mt-1 text-sm font-medium text-gray-500">{c.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
