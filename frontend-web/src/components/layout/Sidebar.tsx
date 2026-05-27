import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import {
  LayoutDashboard,
  CalendarDays,
  FileText,
  Users,
  ClipboardCheck,
  LogOut,
  Vote,
  Monitor,
  BookOpen,
  GraduationCap,
  UserCircle,
  ListChecks,
  BarChart3,
} from 'lucide-react';

interface NavItem {
  label: string;
  path: string;
  icon: React.ReactNode;
  roles: string[];
}

const navItems: NavItem[] = [
  { label: 'Главная', path: '/', icon: <LayoutDashboard size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER', 'STUDENT'] },
  { label: 'Группы', path: '/groups', icon: <Users size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY'] },
  { label: 'Дисциплины', path: '/disciplines', icon: <BookOpen size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY'] },
  { label: 'Журнал', path: '/gradebook', icon: <GraduationCap size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY'] },
  { label: 'Ведомости', path: '/statements', icon: <ListChecks size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY'] },
  { label: 'Преподаватели', path: '/teachers', icon: <UserCircle size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY'] },
  { label: 'Отчёты', path: '/reports', icon: <BarChart3 size={18} />, roles: ['ADMIN', 'METHODIST', 'SECRETARY', 'CHAIRMAN'] },
  { label: 'ГАК', path: '/gac', icon: <Vote size={18} />, roles: ['ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER'] },
  { label: 'Заседания', path: '/meetings', icon: <CalendarDays size={18} />, roles: ['ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER'] },
  { label: 'Протоколы', path: '/protocols', icon: <FileText size={18} />, roles: ['ADMIN', 'SECRETARY', 'CHAIRMAN'] },
  { label: 'Допуски', path: '/admissions', icon: <ClipboardCheck size={18} />, roles: ['ADMIN', 'METHODIST'] },
  { label: 'Табло', path: '/voting-monitor', icon: <Monitor size={18} />, roles: ['ADMIN', 'SECRETARY', 'CHAIRMAN'] },
  { label: 'Пользователи', path: '/admin', icon: <UserCircle size={18} />, roles: ['ADMIN'] },
];

export const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { role, logout } = useAuth();

  const filtered = navItems.filter((item) => item.roles.includes(role || ''));

  return (
    <aside className="flex h-full w-64 flex-col border-r border-[var(--color-border)] bg-white">
      <div className="flex h-16 items-center gap-2 border-b border-[var(--color-border)] px-4">
        <Vote className="text-[var(--color-primary)]" size={24} />
        <span className="text-lg font-bold text-[var(--color-text)]">ГИА СПбУТУИЭ</span>
      </div>
      <nav className="flex-1 space-y-1 p-3">
        {filtered.map((item) => {
          const active = location.pathname === item.path;
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={[
                'flex w-full items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition-colors',
                active
                  ? 'bg-[var(--color-primary)] text-white'
                  : 'text-[var(--color-text-muted)] hover:bg-[var(--color-bg)] hover:text-[var(--color-text)]',
              ].join(' ')}
            >
              {item.icon}
              {item.label}
            </button>
          );
        })}
      </nav>
      <div className="border-t border-[var(--color-border)] p-3">
        <button
          onClick={logout}
          className="flex w-full items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-[var(--color-danger)] hover:bg-red-50"
        >
          <LogOut size={18} />
          Выход
        </button>
      </div>
    </aside>
  );
};
