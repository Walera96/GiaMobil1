import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStudentAssignments } from '../../hooks/useStudentAssignments';
import { useStudentAssignmentsStore } from '../../store/studentAssignmentsStore';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import { Progress } from '../../components/ui/Progress';
import { Tabs } from '../../components/ui/Tabs';
import {
  Search, ClipboardList, Clock, AlertTriangle, CheckCircle2,
  ChevronRight, FileText, CalendarClock
} from 'lucide-react';
import type { Assignment, AssignmentType } from '../../api/assignments';

/** Русские названия типов */
const typeLabels: Record<AssignmentType, string> = {
  VKR: 'ВКР',
  COURSEWORK: 'Курсовая',
  LAB: 'Лабораторная',
  PRACTICE: 'Практика',
  EXAM: 'Экзамен',
  HOMEWORK: 'Домашнее задание',
};

const filterTabs = [
  { id: 'ALL', label: 'Все' },
  { id: 'OVERDUE', label: 'Просроченные' },
  { id: 'TODAY', label: 'Сегодня' },
  { id: 'ACTIVE', label: 'Активные' },
  { id: 'COMPLETED', label: 'Завершённые' },
];

/** Определяет категорию задания */
function getAssignmentCategory(a: Assignment): 'OVERDUE' | 'TODAY' | 'ACTIVE' | 'COMPLETED' {
  if (a.totalSubmissions && a.totalSubmissions > 0) return 'COMPLETED';
  if (!a.deadline) return 'ACTIVE';
  const now = new Date();
  const dl = new Date(a.deadline);
  const diffHours = (dl.getTime() - now.getTime()) / (1000 * 60 * 60);
  if (diffHours < 0) return 'OVERDUE';
  if (diffHours <= 24) return 'TODAY';
  return 'ACTIVE';
}

/** Динамический таймер дедлайна */
function useDeadlineText(deadline: string | undefined): {
  text: string;
  urgent: boolean;
  overdue: boolean;
} {
  const [state, setState] = useState(() => calcDeadline(deadline));

  useEffect(() => {
    if (!deadline) return;
    const timer = setInterval(() => setState(calcDeadline(deadline)), 1000);
    return () => clearInterval(timer);
  }, [deadline]);

  return state;
}

function calcDeadline(deadline: string | undefined): {
  text: string;
  urgent: boolean;
  overdue: boolean;
} {
  if (!deadline) return { text: 'Без срока', urgent: false, overdue: false };
  const now = new Date();
  const dl = new Date(deadline);
  const diffMs = dl.getTime() - now.getTime();
  if (diffMs < 0) return { text: 'Просрочено', urgent: false, overdue: true };

  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffH = Math.floor(diffMin / 60);
  const diffD = Math.floor(diffH / 24);
  const h = diffH % 24;
  const m = diffMin % 60;

  if (diffD > 0) return { text: `Осталось ${diffD} д ${h} ч`, urgent: diffD <= 1, overdue: false };
  if (diffH > 0) return { text: `Осталось ${diffH} ч ${m} мин`, urgent: true, overdue: false };
  return { text: `Осталось ${m} мин`, urgent: true, overdue: false };
}

/** Форматирует дату */
function formatDate(dateStr: string | undefined): string {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
}

export const StudentAssignmentsPage: React.FC = () => {
  const navigate = useNavigate();
  const { data: assignments, isLoading } = useStudentAssignments();
  const { filter, setFilter, search, setSearch } = useStudentAssignmentsStore();

  const filtered = useMemo(() => {
    if (!assignments) return [];
    return assignments.filter((a) => {
      const matchesSearch =
        search === '' ||
        a.title.toLowerCase().includes(search.toLowerCase()) ||
        (a.description && a.description.toLowerCase().includes(search.toLowerCase()));
      const cat = getAssignmentCategory(a);
      const matchesFilter = filter === 'ALL' || cat === filter;
      return matchesSearch && matchesFilter;
    });
  }, [assignments, filter, search]);

  // Прогресс по заданиям: сдано / всего
  const progress = useMemo(() => {
    if (!assignments || assignments.length === 0) return null;
    const completed = assignments.filter((a) => getAssignmentCategory(a) === 'COMPLETED').length;
    return { completed, total: assignments.length, percent: Math.round((completed / assignments.length) * 100) };
  }, [assignments]);

  const stats = useMemo(() => {
    if (!assignments) return { total: 0, overdue: 0, today: 0, active: 0, completed: 0 };
    const cats = assignments.map(getAssignmentCategory);
    return {
      total: assignments.length,
      overdue: cats.filter((c) => c === 'OVERDUE').length,
      today: cats.filter((c) => c === 'TODAY').length,
      active: cats.filter((c) => c === 'ACTIVE').length,
      completed: cats.filter((c) => c === 'COMPLETED').length,
    };
  }, [assignments]);

  return (
    <div className="space-y-6 px-4 py-4 sm:px-6">
      {/* Заголовок */}
      <div>
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Мои задания</h1>
        <p className="text-sm text-[var(--color-text-muted)]">
          Учебные задания, дедлайны и оценки
        </p>
      </div>

      {/* Прогресс-бар по заданиям */}
      {progress && (
        <Card className="p-4 space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-[var(--color-text)] font-medium">Прогресс</span>
            <span className="text-[var(--color-text-muted)]">
              {progress.completed} из {progress.total}
            </span>
          </div>
          <Progress value={progress.percent} max={100} color="green" size="md" showLabel />
        </Card>
      )}

      {/* Виджеты статистики */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3">
        <Card className="flex items-center gap-2 p-3">
          <ClipboardList size={18} className="text-blue-600" />
          <div>
            <div className="text-lg font-bold text-[var(--color-text)]">{stats.total}</div>
            <div className="text-[10px] text-[var(--color-text-muted)] uppercase tracking-wide">Всего</div>
          </div>
        </Card>
        <Card className="flex items-center gap-2 p-3">
          <AlertTriangle size={18} className="text-red-600" />
          <div>
            <div className="text-lg font-bold text-red-600">{stats.overdue}</div>
            <div className="text-[10px] text-[var(--color-text-muted)] uppercase tracking-wide">Просрочено</div>
          </div>
        </Card>
        <Card className="flex items-center gap-2 p-3">
          <Clock size={18} className="text-amber-600" />
          <div>
            <div className="text-lg font-bold text-amber-600">{stats.today}</div>
            <div className="text-[10px] text-[var(--color-text-muted)] uppercase tracking-wide">Сегодня</div>
          </div>
        </Card>
        <Card className="flex items-center gap-2 p-3">
          <CalendarClock size={18} className="text-blue-600" />
          <div>
            <div className="text-lg font-bold text-[var(--color-text)]">{stats.active}</div>
            <div className="text-[10px] text-[var(--color-text-muted)] uppercase tracking-wide">Активные</div>
          </div>
        </Card>
        <Card className="flex items-center gap-2 p-3">
          <CheckCircle2 size={18} className="text-green-600" />
          <div>
            <div className="text-lg font-bold text-green-600">{stats.completed}</div>
            <div className="text-[10px] text-[var(--color-text-muted)] uppercase tracking-wide">Готово</div>
          </div>
        </Card>
      </div>

      {/* Фильтры */}
      <div className="space-y-3">
        <Tabs tabs={filterTabs} activeTab={filter} onChange={(id) => setFilter(id as typeof filter)} />
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <Input
            placeholder="Поиск по названию..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Список заданий */}
      {isLoading ? (
        <div className="text-center py-16 text-[var(--color-text-muted)]">Загрузка...</div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-16">
          <FileText className="mx-auto mb-3 text-gray-300" size={48} />
          <p className="text-[var(--color-text-muted)]">Задания не найдены</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map((a) => (
            <AssignmentCard key={a.id} assignment={a} onClick={() => navigate(`/student/assignments/${a.id}`)} />
          ))}
        </div>
      )}
    </div>
  );
};

/** Карточка задания с динамическим таймером */
const AssignmentCard: React.FC<{
  assignment: Assignment;
  onClick: () => void;
}> = ({ assignment, onClick }) => {
  const timeLeft = useDeadlineText(assignment.deadline);
  const cat = getAssignmentCategory(assignment);
  const isOverdue = cat === 'OVERDUE';
  const isToday = cat === 'TODAY';
  const isCompleted = cat === 'COMPLETED';

  return (
    <Card
      className="cursor-pointer hover:shadow-md transition-shadow"
      onClick={onClick}
    >
      <div className="p-4 flex flex-col sm:flex-row sm:items-center gap-3">
        {/* Индикатор статуса */}
        <div className="flex items-center gap-3 min-w-0">
          <div
            className={[
              'h-10 w-1.5 rounded-full shrink-0',
              isOverdue ? 'bg-red-500' : isToday ? 'bg-amber-500' : isCompleted ? 'bg-green-500' : 'bg-blue-500',
            ].join(' ')}
          />
          <div className="min-w-0">
            <div className="flex items-center gap-2 flex-wrap">
              <Badge variant="info" className="text-[10px]">
                {typeLabels[assignment.assignmentType]}
              </Badge>
              {isOverdue && <Badge variant="danger" className="text-[10px]">Просрочено</Badge>}
              {isToday && <Badge variant="warning" className="text-[10px]">Дедлайн сегодня</Badge>}
              {isCompleted && <Badge variant="success" className="text-[10px]">Сдано</Badge>}
            </div>
            <h3 className="text-base font-semibold text-[var(--color-text)] mt-1 truncate">
              {assignment.title}
            </h3>
          </div>
        </div>

        {/* Мета-информация */}
        <div className="sm:ml-auto flex flex-wrap items-center gap-3 text-sm text-[var(--color-text-muted)]">
          <div className="flex items-center gap-1 min-h-[24px]">
            <Clock size={14} />
            <span className={isOverdue ? 'text-red-600 font-medium' : isToday ? 'text-amber-600 font-medium' : ''}>
              {timeLeft.text}
            </span>
          </div>
          <div className="flex items-center gap-1 min-h-[24px]">
            <CalendarClock size={14} />
            {formatDate(assignment.deadline)}
          </div>
          {assignment.maxScore && (
            <div className="text-xs bg-[var(--color-bg)] px-2 py-1 rounded min-h-[24px] flex items-center">
              {assignment.maxScore} баллов
            </div>
          )}
          <ChevronRight size={16} className="text-gray-300 hidden sm:block shrink-0" />
        </div>
      </div>
    </Card>
  );
};
