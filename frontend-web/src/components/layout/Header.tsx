import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useUnreadCount } from '../../hooks/useNotifications';
import { User, Bell } from 'lucide-react';

export const Header: React.FC = () => {
  const { user, role } = useAuth();
  const navigate = useNavigate();
  const { data: unreadData } = useUnreadCount();
  const unreadCount = unreadData?.count || 0;

  const roleLabels: Record<string, string> = {
    ADMIN: 'Администратор',
    METHODIST: 'Методист',
    SECRETARY: 'Секретарь ГЭК',
    CHAIRMAN: 'Председатель ГЭК',
    GEK_MEMBER: 'Член ГЭК',
    STUDENT: 'Студент',
  };

  return (
    <header className="flex h-16 items-center justify-between border-b border-[var(--color-border)] bg-white px-6">
      <h1 className="text-xl font-semibold text-[var(--color-text)]">Государственная итоговая аттестация</h1>
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate('/notifications')}
          className="relative flex h-9 w-9 items-center justify-center rounded-full text-[var(--color-text-muted)] transition-colors hover:bg-slate-100 hover:text-[var(--color-text)]"
        >
          <Bell size={20} />
          {unreadCount > 0 && (
            <span className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </button>
        <div className="text-right">
          <p className="text-sm font-medium text-[var(--color-text)]">{user?.fullName || user?.username}</p>
          <p className="text-xs text-[var(--color-text-muted)]">{roleLabels[role || ''] || role}</p>
        </div>
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-[var(--color-primary)] text-white">
          <User size={18} />
        </div>
      </div>
    </header>
  );
};
