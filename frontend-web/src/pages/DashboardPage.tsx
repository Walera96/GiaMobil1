import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import {
  Users,
  CalendarDays,
  FileText,
  ClipboardCheck,
  BarChart3,
  Vote,
  GraduationCap,
  ShieldCheck,
  ChevronRight,
} from 'lucide-react';

interface DashCard {
  title: string;
  icon: React.ReactNode;
  iconBg: string;
  desc: string;
  action: string;
  path: string;
}

export const DashboardPage: React.FC = () => {
  const { roles } = useAuth();
  const navigate = useNavigate();

  const roleCards: Record<string, DashCard[]> = {
    SYSTEM_ADMIN: [
      { title: 'Пользователи', icon: <Users size={22} />, iconBg: 'bg-blue-600', desc: 'Управление пользователями и ролями', action: 'Открыть', path: '/admin' },
      { title: 'Аудит', icon: <ShieldCheck size={22} />, iconBg: 'bg-slate-700', desc: 'Журнал изменений и действий', action: 'Открыть', path: '/audit' },
    ],
    UNIVERSITY_ADMIN: [
      { title: 'Пользователи', icon: <Users size={22} />, iconBg: 'bg-blue-600', desc: 'Управление пользователями и ролями', action: 'Открыть', path: '/admin' },
      { title: 'Аудит', icon: <ShieldCheck size={22} />, iconBg: 'bg-slate-700', desc: 'Журнал изменений и действий', action: 'Открыть', path: '/audit' },
    ],
    METHODIST: [
      { title: 'Допуски', icon: <ClipboardCheck size={22} />, iconBg: 'bg-emerald-600', desc: 'Проверка допуска студентов к ГИА', action: 'Открыть', path: '/admissions' },
      { title: 'Ведомости', icon: <FileText size={22} />, iconBg: 'bg-blue-600', desc: 'Формирование и печать ведомостей', action: 'Открыть', path: '/protocols' },
      { title: 'Студенты', icon: <GraduationCap size={22} />, iconBg: 'bg-indigo-600', desc: 'Справочник студентов', action: 'Открыть', path: '/admissions' },
    ],
    GEK_SECRETARY: [
      { title: 'Заседания', icon: <CalendarDays size={22} />, iconBg: 'bg-blue-600', desc: 'Создание и управление заседаниями', action: 'Открыть', path: '/meetings' },
      { title: 'Создать заседание', icon: <CalendarDays size={22} />, iconBg: 'bg-emerald-600', desc: 'Новое заседание ГЭК', action: 'Создать', path: '/meetings' },
      { title: 'Архив', icon: <FileText size={22} />, iconBg: 'bg-slate-600', desc: 'Архив протоколов', action: 'Открыть', path: '/protocols' },
    ],
    GEK_CHAIRMAN: [
      { title: 'Заседания', icon: <CalendarDays size={22} />, iconBg: 'bg-blue-600', desc: 'Заседания на утверждение', action: 'Открыть', path: '/meetings' },
      { title: 'Статистика', icon: <BarChart3 size={22} />, iconBg: 'bg-purple-600', desc: 'Статистика оценок по группе', action: 'Открыть', path: '/protocols' },
    ],
    GEK_MEMBER: [
      { title: 'Мобильное приложение', icon: <Vote size={22} />, iconBg: 'bg-blue-600', desc: 'Используйте планшет или телефон для голосования', action: 'QR-код', path: '#' },
    ],
    STUDENT: [
      { title: 'Мой профиль', icon: <GraduationCap size={22} />, iconBg: 'bg-blue-600', desc: 'Личный кабинет студента', action: 'Открыть', path: '/student/profile' },
      { title: 'Мой допуск', icon: <ClipboardCheck size={22} />, iconBg: 'bg-emerald-600', desc: 'Проверьте статус допуска к ГИА', action: 'Открыть', path: '/admissions' },
      { title: 'Моя защита', icon: <CalendarDays size={22} />, iconBg: 'bg-orange-600', desc: 'Дата и время защиты', action: 'Открыть', path: '/meetings' },
      { title: 'Моя оценка', icon: <FileText size={22} />, iconBg: 'bg-purple-600', desc: 'Итоговая оценка', action: 'Открыть', path: '/protocols' },
    ],
  };

  const cards = roles.flatMap((r) => roleCards[r] || []);

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-[var(--color-text)]">Дашборд</h2>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
        {cards.map((card) => (
          <Card
            key={card.title}
            className="group flex cursor-pointer flex-col justify-between transition-shadow hover:shadow-md"
            onClick={() => card.path !== '#' && navigate(card.path)}
          >
            <div className="mb-4 flex items-start gap-3">
              <div className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${card.iconBg} text-white shadow-sm transition-transform group-hover:scale-105`}>
                {card.icon}
              </div>
              <div className="min-w-0">
                <h3 className="font-semibold text-[var(--color-text)]">{card.title}</h3>
                <p className="text-xs leading-relaxed text-[var(--color-text-muted)]">{card.desc}</p>
              </div>
            </div>
            <Button
              variant="secondary"
              size="sm"
              className="w-full justify-between"
              onClick={(e) => {
                e.stopPropagation();
                card.path !== '#' && navigate(card.path);
              }}
            >
              {card.action}
              <ChevronRight size={16} className="transition-transform group-hover:translate-x-0.5" />
            </Button>
          </Card>
        ))}
      </div>
    </div>
  );
};
