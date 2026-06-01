import React, { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTeacherAssignments } from '../../hooks/useTeacherAssignments';
import { useTeacherAssignmentsStore } from '../../store/teacherAssignmentsStore';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import { Tabs } from '../../components/ui/Tabs';
import { Select } from '../../components/ui/Select';
import {
  Plus, Search, ClipboardList, Clock, Users, FileText,
  AlertCircle, CheckCircle2, ChevronRight
} from 'lucide-react';
import type { Assignment, AssignmentType } from '../../api/assignments';
import { TeacherAssignmentCreateModal } from './TeacherAssignmentCreateModal';

/** Русские названия типов заданий */
const assignmentTypeLabels: Record<AssignmentType | 'ALL', string> = {
  ALL: 'Все типы',
  VKR: 'ВКР',
  COURSEWORK: 'Курсовая',
  LAB: 'Лабораторная',
  PRACTICE: 'Практика',
  EXAM: 'Экзамен',
  HOMEWORK: 'Домашнее задание',
};

const assignmentTypeOptions = [
  { value: 'ALL', label: 'Все типы' },
  { value: 'VKR', label: 'ВКР' },
  { value: 'COURSEWORK', label: 'Курсовая' },
  { value: 'LAB', label: 'Лабораторная' },
  { value: 'PRACTICE', label: 'Практика' },
  { value: 'EXAM', label: 'Экзамен' },
  { value: 'HOMEWORK', label: 'Домашнее задание' },
];

const statusTabs = [
  { id: 'ALL', label: 'Все' },
  { id: 'active', label: 'Активные' },
  { id: 'expired', label: 'Просроченные' },
];

/** Определяет, просрочено ли задание */
function isExpired(assignment: Assignment): boolean {
  if (!assignment.deadline) return false;
  return new Date(assignment.deadline) < new Date();
}

/** Форматирует дедлайн для отображения */
function formatDeadline(deadline: string | undefined): string {
  if (!deadline) return 'Без срока';
  const d = new Date(deadline);
  return d.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export const TeacherAssignmentsPage: React.FC = () => {
  const navigate = useNavigate();
  const { data: assignments, isLoading } = useTeacherAssignments();
  const { filters, setFilters, openCreate } = useTeacherAssignmentsStore();

  // Фильтрация заданий
  const filtered = useMemo(() => {
    if (!assignments) return [];
    return assignments.filter((a) => {
      const matchesSearch =
        filters.search === '' ||
        a.title.toLowerCase().includes(filters.search.toLowerCase()) ||
        (a.description && a.description.toLowerCase().includes(filters.search.toLowerCase()));

      const matchesType =
        filters.assignmentType === 'ALL' || a.assignmentType === filters.assignmentType;

      const matchesStatus =
        filters.status === 'ALL' ||
        (filters.status === 'active' && !isExpired(a)) ||
        (filters.status === 'expired' && isExpired(a));

      return matchesSearch && matchesType && matchesStatus;
    });
  }, [assignments, filters]);

  // Статистика для виджетов
  const stats = useMemo(() => {
    if (!assignments) return { total: 0, active: 0, expired: 0, pending: 0 };
    return {
      total: assignments.length,
      active: assignments.filter((a) => !isExpired(a)).length,
      expired: assignments.filter((a) => isExpired(a)).length,
      pending: assignments.reduce((sum, a) => sum + (a.pendingSubmissions || 0), 0),
    };
  }, [assignments]);

  return (
    <div className="space-y-6">
      {/* Заголовок */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text)]">Задания</h1>
          <p className="text-sm text-[var(--color-text-muted)]">
            Управление учебными заданиями и проверка работ
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus size={18} className="mr-2" />
          Создать задание
        </Button>
      </div>

      {/* Виджеты-статистика */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="flex items-center gap-3 p-4">
          <div className="rounded-lg bg-blue-100 p-2.5 text-blue-700">
            <ClipboardList size={20} />
          </div>
          <div>
            <div className="text-2xl font-bold text-[var(--color-text)]">{stats.total}</div>
            <div className="text-xs text-[var(--color-text-muted)]">Всего заданий</div>
          </div>
        </Card>
        <Card className="flex items-center gap-3 p-4">
          <div className="rounded-lg bg-green-100 p-2.5 text-green-700">
            <CheckCircle2 size={20} />
          </div>
          <div>
            <div className="text-2xl font-bold text-[var(--color-text)]">{stats.active}</div>
            <div className="text-xs text-[var(--color-text-muted)]">Активные</div>
          </div>
        </Card>
        <Card className="flex items-center gap-3 p-4">
          <div className="rounded-lg bg-red-100 p-2.5 text-red-700">
            <AlertCircle size={20} />
          </div>
          <div>
            <div className="text-2xl font-bold text-[var(--color-text)]">{stats.expired}</div>
            <div className="text-xs text-[var(--color-text-muted)]">Просроченные</div>
          </div>
        </Card>
        <Card className="flex items-center gap-3 p-4">
          <div className="rounded-lg bg-amber-100 p-2.5 text-amber-700">
            <Users size={20} />
          </div>
          <div>
            <div className="text-2xl font-bold text-[var(--color-text)]">{stats.pending}</div>
            <div className="text-xs text-[var(--color-text-muted)]">На проверке</div>
          </div>
        </Card>
      </div>

      {/* Фильтры */}
      <div className="space-y-3">
        <Tabs
          tabs={statusTabs}
          activeTab={filters.status}
          onChange={(tab) => setFilters({ status: tab as 'ALL' | 'active' | 'expired' })}
        />
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
            <Input
              placeholder="Поиск по названию..."
              value={filters.search}
              onChange={(e) => setFilters({ search: e.target.value })}
              className="pl-10"
            />
          </div>
          <div className="w-full sm:w-56">
            <Select
              options={assignmentTypeOptions}
              value={filters.assignmentType}
              onChange={(e) => setFilters({ assignmentType: e.target.value as AssignmentType | 'ALL' })}
            />
          </div>
        </div>
      </div>

      {/* Список заданий — карточки */}
      {isLoading ? (
        <div className="text-center py-16 text-[var(--color-text-muted)]">Загрузка заданий...</div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-16">
          <FileText className="mx-auto mb-3 text-gray-300" size={48} />
          <p className="text-[var(--color-text-muted)]">Задания не найдены</p>
          <Button variant="ghost" onClick={openCreate} className="mt-3">
            Создать первое задание
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map((a) => (
            <Card
              key={a.id}
              className="cursor-pointer hover:shadow-md transition-shadow group"
              onClick={() => navigate(`/teacher/assignments/${a.id}/submissions`)}
            >
              <div className="p-4 space-y-3">
                {/* Верхняя строка: тип + дедлайн */}
                <div className="flex items-center justify-between">
                  <Badge variant="info">{assignmentTypeLabels[a.assignmentType]}</Badge>
                  <div className="flex items-center gap-1 text-xs text-[var(--color-text-muted)]">
                    <Clock size={14} />
                    {formatDeadline(a.deadline)}
                  </div>
                </div>

                {/* Название */}
                <h3 className="text-lg font-semibold text-[var(--color-text)] group-hover:text-[var(--color-primary)] transition-colors">
                  {a.title}
                </h3>

                {/* Описание (обрезанное) */}
                {a.description && (
                  <p className="text-sm text-[var(--color-text-muted)] line-clamp-2">
                    {a.description}
                  </p>
                )}

                {/* Нижняя строка: группа + сдачи */}
                <div className="flex items-center justify-between pt-2 border-t border-[var(--color-border)]">
                  <div className="flex items-center gap-1 text-sm text-[var(--color-text-muted)]">
                    <Users size={16} />
                    {a.targetGroupName || 'Без группы'}
                  </div>
                  <div className="flex items-center gap-3">
                    {a.pendingSubmissions ? (
                      <Badge variant="warning">{a.pendingSubmissions} на проверке</Badge>
                    ) : (
                      <Badge variant="success">Все проверены</Badge>
                    )}
                    <ChevronRight size={16} className="text-[var(--color-text-muted)]" />
                  </div>
                </div>

                {/* Макс. балл */}
                {a.maxScore && (
                  <div className="text-xs text-[var(--color-text-muted)]">
                    Максимум {a.maxScore} баллов
                  </div>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Модальное окно создания задания */}
      <TeacherAssignmentCreateModal />
    </div>
  );
};
