import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import {
  Shield,
  Building2,
  School,
  Vote,
  BookOpen,
  GraduationCap,
  ArrowRight,
} from 'lucide-react';

type Portal = 'admin' | 'deanery' | 'department' | 'gek' | 'methodist' | 'student' | 'teacher_portal' | 'student_portal';

const portalConfig: Record<Portal, { label: string; icon: React.ReactNode; color: string; path: string }> = {
  admin: {
    label: 'Администрация',
    icon: <Shield size={32} />,
    color: 'bg-purple-600 hover:bg-purple-700',
    path: '/admin',
  },
  deanery: {
    label: 'Деканат',
    icon: <Building2 size={32} />,
    color: 'bg-blue-600 hover:bg-blue-700',
    path: '/deanery',
  },
  department: {
    label: 'Кафедра',
    icon: <School size={32} />,
    color: 'bg-teal-600 hover:bg-teal-700',
    path: '/department',
  },
  gek: {
    label: 'Комиссия ГИА (ГЭК)',
    icon: <Vote size={32} />,
    color: 'bg-amber-600 hover:bg-amber-700',
    path: '/gek',
  },
  methodist: {
    label: 'Методист',
    icon: <BookOpen size={32} />,
    color: 'bg-emerald-600 hover:bg-emerald-700',
    path: '/methodist',
  },
  student: {
    label: 'Студент',
    icon: <GraduationCap size={32} />,
    color: 'bg-indigo-600 hover:bg-indigo-700',
    path: '/student',
  },
  teacher_portal: {
    label: 'Преподаватель',
    icon: <School size={32} />,
    color: 'bg-cyan-600 hover:bg-cyan-700',
    path: '/teacher',
  },
  student_portal: {
    label: 'Студент (задания)',
    icon: <BookOpen size={32} />,
    color: 'bg-rose-600 hover:bg-rose-700',
    path: '/student-portal',
  },
};

export const PortalSelector: React.FC = () => {
  const { availablePortals, primaryPortal, setPrimaryPortal, user } = useAuthStore();
  const navigate = useNavigate();

  const handleSelect = (portal: Portal) => {
    setPrimaryPortal(portal);
    navigate(portalConfig[portal].path);
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[var(--color-bg)]">
      <div className="w-full max-w-2xl p-8">
        <h1 className="mb-2 text-center text-2xl font-bold text-[var(--color-text)]">
          Добро пожаловать, {user?.fullName || 'пользователь'}
        </h1>
        <p className="mb-8 text-center text-[var(--color-text-muted)]">
          Выберите портал для работы
        </p>

        <div className="grid gap-4 sm:grid-cols-2">
          {availablePortals.map((portal: Portal) => {
            const config = portalConfig[portal];
            const isPrimary = portal === primaryPortal;

            return (
              <button
                key={portal}
                onClick={() => handleSelect(portal)}
                className={[
                  'flex items-center gap-4 rounded-xl p-6 text-white transition-all',
                  'shadow-lg hover:shadow-xl hover:scale-[1.02]',
                  config.color,
                  isPrimary ? 'ring-4 ring-white/50' : '',
                ].join(' ')}
              >
                {config.icon}
                <div className="text-left">
                  <div className="text-lg font-bold">{config.label}</div>
                  {isPrimary && (
                    <div className="text-sm opacity-80">Основной портал</div>
                  )}
                </div>
                <ArrowRight className="ml-auto" size={20} />
              </button>
            );
          })}
        </div>

        <p className="mt-6 text-center text-sm text-[var(--color-text-muted)]">
          Вы можете переключаться между порталами через меню в любой момент
        </p>
      </div>
    </div>
  );
};
