import React, { useState, useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useStudentAssignments, useStudentAssignment, useMySubmission, useSubmitAssignment } from '../../hooks/useStudentAssignments';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Progress } from '../../components/ui/Progress';
import {
  BookOpen,
  Clock,
  CheckCircle2,
  AlertCircle,
  ChevronLeft,
  Send,
  User,
  Bell,
} from 'lucide-react';
import { toast } from '../../store/toastStore';

const typeLabels: Record<string, string> = {
  LAB: 'Лаб.',
  COURSEWORK: 'КР',
  HOMEWORK: 'ДЗ',
  EXAM: 'Экз.',
  PROJECT: 'Проект',
};

const statusColors: Record<string, string> = {
  PENDING: 'bg-gray-400',
  SUBMITTED: 'bg-amber-500',
  GRADED: 'bg-green-500',
  RETURNED: 'bg-red-500',
  LATE: 'bg-orange-500',
};

const statusLabels: Record<string, string> = {
  PENDING: 'Не сдано',
  SUBMITTED: 'На проверке',
  GRADED: 'Оценено',
  RETURNED: 'На доработке',
  LATE: 'Просрочено',
};

function formatTimeLeft(deadline?: string): string {
  if (!deadline) return '—';
  const diff = new Date(deadline).getTime() - Date.now();
  if (diff <= 0) return 'Просрочено';
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  if (days > 0) return `${days}д ${hours}ч`;
  return `${hours}ч`;
}

function getAssignmentStatus(assignment: any, submission: any): string {
  if (submission) return submission.status;
  if (!assignment.deadline) return 'PENDING';
  return new Date(assignment.deadline) <= new Date() ? 'LATE' : 'PENDING';
}

/** Список заданий (mobile) */
const AssignmentList: React.FC<{ onSelect: (id: string) => void }> = ({ onSelect }) => {
  const { data: assignments, isLoading } = useStudentAssignments();
  const [filter, setFilter] = useState<'ALL' | 'ACTIVE' | 'OVERDUE' | 'COMPLETED'>('ALL');

  const filtered = useMemo(() => {
    if (!assignments) return [];
    const now = new Date();
    switch (filter) {
      case 'ACTIVE':
        return assignments.filter((a) => !a.deadline || new Date(a.deadline) > now);
      case 'OVERDUE':
        return assignments.filter((a) => a.deadline && new Date(a.deadline) <= now);
      case 'COMPLETED':
        return assignments.filter((a) => a.totalSubmissions && a.totalSubmissions > 0);
      default:
        return assignments;
    }
  }, [assignments, filter]);

  if (isLoading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="space-y-3 pb-20">
      {/* Фильтры */}
      <div className="flex gap-2 overflow-x-auto px-4 py-2">
        {(['ALL', 'ACTIVE', 'OVERDUE', 'COMPLETED'] as const).map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={[
              'whitespace-nowrap rounded-full px-3 py-1 text-xs font-medium transition',
              filter === f ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600',
            ].join(' ')}
          >
            {f === 'ALL' && 'Все'}
            {f === 'ACTIVE' && 'Активные'}
            {f === 'OVERDUE' && 'Просроченные'}
            {f === 'COMPLETED' && 'Завершённые'}
          </button>
        ))}
      </div>

      {/* Карточки */}
      {filtered.map((a) => {
        const isOverdue = a.deadline ? new Date(a.deadline) <= new Date() : false;
        return (
          <Card
            key={a.id}
            onClick={() => onSelect(a.id)}
            className="mx-4 flex cursor-pointer flex-col gap-2 rounded-2xl border-0 bg-white p-4 shadow-sm active:scale-[0.98] transition-transform"
          >
            <div className="flex items-center justify-between">
              <Badge className="text-[10px]">{typeLabels[a.assignmentType] || a.assignmentType}</Badge>
              <span className="text-xs text-gray-400">
                <Clock size={12} className="inline mr-1" />
                {formatTimeLeft(a.deadline)}
              </span>
            </div>
            <h3 className="text-sm font-semibold text-gray-900 line-clamp-2">{a.title}</h3>
            <div className="flex items-center gap-2">
              <div className={`h-2 w-2 rounded-full ${isOverdue ? 'bg-red-500' : 'bg-blue-500'}`} />
              <span className="text-xs text-gray-500">{isOverdue ? 'Просрочено' : 'Активно'}</span>
            </div>
            {a.maxScore !== undefined && (
              <p className="text-xs text-gray-400">Макс. балл: {a.maxScore}</p>
            )}
          </Card>
        );
      })}

      {filtered.length === 0 && (
        <div className="flex flex-col items-center justify-center py-16 text-gray-400">
          <BookOpen size={40} className="mb-2" />
          <p className="text-sm">Заданий не найдено</p>
        </div>
      )}
    </div>
  );
};

/** Детали задания (mobile) */
const AssignmentDetail: React.FC<{ id: string; onBack: () => void }> = ({ id, onBack }) => {
  const { data: assignment } = useStudentAssignment(id);
  const { data: submission } = useMySubmission(id);
  const submitMutation = useSubmitAssignment();
  const [studentComment, setStudentComment] = useState('');
  const [showConfirm, setShowConfirm] = useState(false);

  if (!assignment) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
      </div>
    );
  }

  const isOverdue = assignment.deadline ? new Date(assignment.deadline) <= new Date() : false;
  const canSubmit = !submission || submission.status === 'RETURNED';
  const status = getAssignmentStatus(assignment, submission);

  const handleSubmit = () => {
    if (!studentComment.trim()) {
      toast.error('Введите ответ');
      return;
    }
    submitMutation.mutate(
      {
        assignmentId: id,
        payload: {
          studentComment,
          solutionFiles: [],
        },
      },
      {
        onSuccess: () => {
          toast.success('Сдано!');
          setStudentComment('');
          setShowConfirm(false);
        },
        onError: () => toast.error('Ошибка при сдаче'),
      }
    );
  };

  return (
    <div className="space-y-4 pb-24 px-4">
      {/* Header */}
      <div className="flex items-center gap-3 pt-2">
        <button onClick={onBack} className="rounded-full p-2 hover:bg-gray-100">
          <ChevronLeft size={24} className="text-gray-700" />
        </button>
        <div className="min-w-0 flex-1">
          <h1 className="text-lg font-bold text-gray-900 line-clamp-1">{assignment.title}</h1>
          <p className="text-xs text-gray-500">
            {typeLabels[assignment.assignmentType]} • Дедлайн:{' '}
            {assignment.deadline ? new Date(assignment.deadline).toLocaleDateString('ru-RU') : '—'}
          </p>
        </div>
      </div>

      {/* Status */}
      {submission && (
        <Card className="rounded-xl border-0 bg-gray-50 p-3">
          <div className="flex items-center gap-2">
            {submission.status === 'GRADED' ? (
              <CheckCircle2 size={18} className="text-green-600" />
            ) : submission.status === 'RETURNED' ? (
              <AlertCircle size={18} className="text-red-600" />
            ) : (
              <Clock size={18} className="text-amber-600" />
            )}
            <span className="text-sm font-medium">{statusLabels[submission.status] || submission.status}</span>
          </div>
          {submission.totalScore !== undefined && assignment.maxScore !== undefined && (
            <div className="mt-2">
              <div className="flex justify-between text-xs text-gray-500 mb-1">
                <span>Балл</span>
                <span>
                  {submission.totalScore} / {assignment.maxScore}
                </span>
              </div>
              <Progress value={submission.totalScore} max={assignment.maxScore} size="md" color="green" />
            </div>
          )}
          {submission.teacherFeedback && (
            <p className="mt-2 text-xs text-gray-600 bg-white rounded-lg p-2">
              <span className="font-medium">Комментарий:</span> {submission.teacherFeedback}
            </p>
          )}
        </Card>
      )}

      {/* Description */}
      <div>
        <h2 className="text-sm font-semibold text-gray-800 mb-1">Описание</h2>
        <p className="text-sm text-gray-600 whitespace-pre-line">{assignment.description || 'Нет описания'}</p>
      </div>

      {/* Submit form */}
      {canSubmit && (
        <div className="space-y-3">
          <h2 className="text-sm font-semibold text-gray-800">Ответ</h2>
          <textarea
            value={studentComment}
            onChange={(e) => setStudentComment(e.target.value)}
            placeholder="Введите ваш ответ..."
            rows={5}
            className="w-full rounded-xl border border-gray-200 bg-white p-3 text-sm focus:border-blue-500 focus:outline-none"
          />
          <Button
            className="w-full rounded-xl"
            disabled={submitMutation.isPending || !studentComment.trim()}
            onClick={() => setShowConfirm(true)}
          >
            <Send size={16} className="mr-2" />
            {submitMutation.isPending ? 'Отправка...' : 'Сдать задание'}
          </Button>
        </div>
      )}

      {isOverdue && !submission && (
        <div className="flex items-center gap-2 rounded-xl bg-red-50 p-3 text-xs text-red-700">
          <AlertCircle size={16} />
          Дедлайн прошёл. Сдача может быть не принята.
        </div>
      )}

      {/* Confirm modal */}
      {showConfirm && (
        <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/50 sm:items-center">
          <div className="w-full max-w-sm rounded-t-2xl bg-white p-4 sm:rounded-2xl">
            <h3 className="text-lg font-bold text-gray-900">Подтвердить сдачу?</h3>
            <p className="mt-1 text-sm text-gray-500">После отправки изменить ответ будет нельзя.</p>
            <div className="mt-4 flex gap-2">
              <Button variant="secondary" className="flex-1" onClick={() => setShowConfirm(false)}>
                Отмена
              </Button>
              <Button className="flex-1" onClick={handleSubmit} disabled={submitMutation.isPending}>
                Отправить
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

/** Bottom navigation */
const BottomNav: React.FC = () => {
  const navigate = useNavigate();
  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 flex h-16 items-center justify-around border-t border-gray-200 bg-white px-2">
      <button onClick={() => navigate('/m')} className="flex flex-col items-center gap-0.5 text-blue-600">
        <BookOpen size={20} />
        <span className="text-[10px] font-medium">Задания</span>
      </button>
      <button onClick={() => navigate('/notifications')} className="flex flex-col items-center gap-0.5 text-gray-400">
        <Bell size={20} />
        <span className="text-[10px] font-medium">Уведомления</span>
      </button>
      <button onClick={() => navigate('/student/profile')} className="flex flex-col items-center gap-0.5 text-gray-400">
        <User size={20} />
        <span className="text-[10px] font-medium">Профиль</span>
      </button>
    </nav>
  );
};

export default function MobileAssignmentsPage() {
  const { id } = useParams<{ id?: string }>();
  const [selectedId, setSelectedId] = useState<string | null>(id || null);

  return (
    <div className="min-h-screen bg-gray-50">
      {selectedId ? (
        <AssignmentDetail id={selectedId} onBack={() => setSelectedId(null)} />
      ) : (
        <>
          <div className="sticky top-0 z-10 border-b border-gray-200 bg-white px-4 py-3">
            <h1 className="text-xl font-bold text-gray-900">Мои задания</h1>
            <p className="text-xs text-gray-500">Мобильная версия</p>
          </div>
          <AssignmentList onSelect={setSelectedId} />
        </>
      )}
      <BottomNav />
    </div>
  );
}
