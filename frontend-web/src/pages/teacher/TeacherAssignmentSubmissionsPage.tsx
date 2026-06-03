import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAssignmentSubmissions, useTeacherAssignment } from '../../hooks/useTeacherAssignments';
import { TeacherAssignmentReviewModal } from './TeacherAssignmentReviewModal';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import {
  ArrowLeft, Users, CheckCircle2, Clock, AlertCircle, RotateCcw,
  FileText, MessageSquare, Send
} from 'lucide-react';
import type { Submission, SubmissionStatus } from '../../api/assignments';

/** Русские названия статусов сдачи */
const statusLabels: Record<SubmissionStatus, string> = {
  DRAFT: 'Черновик',
  SUBMITTED: 'Сдано',
  REVIEWING: 'На проверке',
  REVIEWED: 'Проверено',
  RETURNED: 'На доработку',
};

/** Цвета бейджей по статусу */
const statusVariants: Record<SubmissionStatus, 'default' | 'success' | 'warning' | 'danger' | 'info'> = {
  DRAFT: 'default',
  SUBMITTED: 'info',
  REVIEWING: 'warning',
  REVIEWED: 'success',
  RETURNED: 'danger',
};

/** Генерирует аватар с инициалами */
function getInitials(name: string | undefined): string {
  if (!name) return '?';
  return name.split(' ').map((p) => p[0]).join('').slice(0, 2).toUpperCase();
}

/** Форматирует дату сдачи */
function formatDate(dateStr: string | undefined): string {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export const TeacherAssignmentSubmissionsPage: React.FC = () => {
  const { id: assignmentId } = useParams<{ id: string }>();
  const navigateBack = () => window.history.back();

  const { data: assignment } = useTeacherAssignment(assignmentId || '');
  const [statusFilter, setStatusFilter] = useState<SubmissionStatus | 'ALL'>('ALL');
  const { data: submissions, isLoading } = useAssignmentSubmissions(
    assignmentId || '',
    statusFilter === 'ALL' ? undefined : statusFilter
  );

  // Модальное окно проверки
  const [reviewSubmission, setReviewSubmission] = useState<Submission | null>(null);
  const [isReviewOpen, setIsReviewOpen] = useState(false);

  const openReview = (submission: Submission) => {
    setReviewSubmission(submission);
    setIsReviewOpen(true);
  };

  const closeReview = () => {
    setIsReviewOpen(false);
    setReviewSubmission(null);
  };

  if (!assignmentId) {
    return <div className="text-center py-16">Задание не указано</div>;
  }

  return (
    <div className="space-y-6">
      {/* Шапка */}
      <div className="flex items-center gap-3">
        <button
          onClick={navigateBack}
          className="rounded-md p-2 text-[var(--color-text-muted)] hover:bg-[var(--color-bg)] transition-colors"
        >
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text)]">
            {assignment?.title || 'Задание'}
          </h1>
          <p className="text-sm text-[var(--color-text-muted)]">
            Сдачи студентов
            {assignment?.targetGroupName && ` · ${assignment.targetGroupName}`}
          </p>
        </div>
      </div>

      {/* Фильтры по статусу */}
      <div className="flex flex-wrap gap-2">
        {(['ALL', 'SUBMITTED', 'REVIEWING', 'REVIEWED', 'RETURNED'] as const).map((s) => (
          <button
            key={s}
            onClick={() => setStatusFilter(s)}
            className={[
              'px-3 py-1.5 rounded-md text-sm font-medium transition-colors',
              statusFilter === s
                ? 'bg-[var(--color-primary)] text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200',
            ].join(' ')}
          >
            {s === 'ALL' ? 'Все' : statusLabels[s]}
          </button>
        ))}
      </div>

      {/* Список сдач */}
      {isLoading ? (
        <div className="text-center py-16 text-[var(--color-text-muted)]">Загрузка сдач...</div>
      ) : !submissions || submissions.length === 0 ? (
        <div className="text-center py-16">
          <Users className="mx-auto mb-3 text-gray-300" size={48} />
          <p className="text-[var(--color-text-muted)]">Пока нет сдач</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {submissions.map((s) => (
            <SubmissionCard
              key={s.id}
              submission={s}
              onReview={() => openReview(s)}
            />
          ))}
        </div>
      )}

      {/* Модальное окно проверки */}
      <TeacherAssignmentReviewModal
        assignmentId={assignmentId}
        submission={reviewSubmission}
        maxScore={assignment?.maxScore || 100}
        isOpen={isReviewOpen}
        onClose={closeReview}
      />
    </div>
  );
};

/** Карточка одной сдачи */
const SubmissionCard: React.FC<{
  submission: Submission;
  onReview: () => void;
}> = ({ submission, onReview }) => {
  return (
    <Card className="p-4 space-y-3 hover:shadow-md transition-shadow">
      {/* Студент */}
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[var(--color-primary)] text-white text-sm font-bold">
          {getInitials(submission.studentName)}
        </div>
        <div className="flex-1 min-w-0">
          <div className="text-sm font-semibold text-[var(--color-text)] truncate">
            {submission.studentName || 'Неизвестный студент'}
          </div>
          <div className="text-xs text-[var(--color-text-muted)]">
            {submission.studentGroup || '—'}
          </div>
        </div>
        <Badge variant={statusVariants[submission.status]}>
          {statusLabels[submission.status]}
        </Badge>
      </div>

      {/* Информация */}
      <div className="space-y-1 text-sm text-[var(--color-text-muted)]">
        <div className="flex items-center gap-1">
          <Clock size={14} />
          <span>Сдано: {formatDate(submission.submittedAt)}</span>
        </div>
        {submission.totalScore !== undefined && (
          <div className="flex items-center gap-1">
            <CheckCircle2 size={14} />
            <span>Балл: <strong className="text-[var(--color-text)]">{submission.totalScore}</strong></span>
          </div>
        )}
        {submission.solutionFiles && submission.solutionFiles.length > 0 && (
          <div className="flex items-center gap-1">
            <FileText size={14} />
            <span>{submission.solutionFiles.length} файл(ов)</span>
          </div>
        )}
        {submission.studentComment && (
          <div className="flex items-start gap-1">
            <MessageSquare size={14} className="mt-0.5 shrink-0" />
            <span className="line-clamp-2">{submission.studentComment}</span>
          </div>
        )}
      </div>

      {/* Кнопка действия */}
      <div className="pt-2">
        {submission.status === 'SUBMITTED' || submission.status === 'REVIEWING' || submission.status === 'RETURNED' ? (
          <Button size="sm" className="w-full" onClick={onReview}>
            <Send size={14} className="mr-2" />
            {submission.status === 'RETURNED' ? 'Перепроверить' : 'Проверить'}
          </Button>
        ) : (
          <Button size="sm" variant="ghost" className="w-full" onClick={onReview}>
            Посмотреть
          </Button>
        )}
      </div>
    </Card>
  );
};
