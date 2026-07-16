import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { NAV } from '../routes/navConfig';

export default function Sidebar() {
  const { role } = useAuth();
  const items = NAV[role] || [];

  return (
    <aside className="hidden w-60 shrink-0 border-r border-gray-200 bg-white md:block">
      <div className="px-5 py-4 text-xl font-bold text-brand-700">CertifyPro</div>
      <nav className="flex flex-col gap-1 px-3">
        {items.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            end={item.path === '/admin' || item.path === '/evaluator' || item.path === '/officer'}
            className={({ isActive }) =>
              `rounded-lg px-3 py-2 text-sm font-medium ${
                isActive ? 'bg-brand-50 text-brand-700' : 'text-gray-600 hover:bg-gray-100'
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
