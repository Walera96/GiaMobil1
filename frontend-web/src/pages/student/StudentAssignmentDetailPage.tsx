import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useStudentAssignment, useMySubmission } from '../../hooks/useStudentAssignments';
import { StudentAssignmentSubmitModal } from './StudentAssignmentSubmitModal';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Progress } from '../../components/ui/Progress';
import {
  ArrowLeft, Clock, CalendarClock, FileText,
  CheckCircle2, AlertTriangle, RotateCcw, MessageSquare,
  User, Send
} from 'lucide-react';

/** Русские названия типов */
const typeLabels: Record<string, string> = {
  VKR: 'ВКР',
  COURSEWORK: 'Курсовая',
  LAB: 'Лабораторная',
  PRACTICE: 'Практика',
  EXAM: 'Экзамен',
  HOMEWORK: 'Домашнее задание',
};

/** Статус сдачи */
const statusLabels: Record<string, string> = {
  DRAFT: 'Черновик',
  SUBMITTED: 'Сдано на проверку',
  REVIEWING: 'На проверке',
  REVIEWED: 'Проверено',
  RETURNED: 'На доработку',
};

const statusVariants: Record<string, 'default' | 'success' | 'warning' | 'danger' | 'info'> = {
  DRAFT: 'default',
  SUBMITTED: 'info',
  REVIEWING: 'warning',
  REVIEWED: 'success',
  RETURNED: 'danger',
};

function formatDate(dateStr: string | undefined): string {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit' });
}

/** Динамический таймер дедлайна */
function useDeadlineTimer(deadline: string | undefined) {
  const [timeLeft, setTimeLeft] = useState(() => calculateTimeLeft(deadline));

  useEffect(() => {
    if (!deadline) return;
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft(deadline));
    }, 1000);
    return () => clearInterval(timer);
  }, [deadline]);

  return timeLeft;
}

function calculateTimeLeft(deadline: string | undefined): {
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
  const s = diffSec % 60;

  if (diffD > 0) {
    return { text: `Осталось ${diffD} д ${h} ч`, urgent: diffD <= 1, overdue: false };
  }
  if (diffH > 0) {
    return { text: `Осталось ${diffH} ч ${m} мин`, urgent: true, overdue: false };
  }
  return { text: `Осталось ${m} мин ${s} сек`, urgent: true, overdue: false };
}

export const StudentAssignmentDetailPage: React.FC = () => {
  const { id: assignmentId } = useParams<{ id: string }>();
  const navigateBack = () => window.history.back();

  const { data: assignment, isLoading: loadingAssignment } = useStudentAssignment(assignmentId || '');
  const { data: submission } = useMySubmission(assignmentId || '');

  const [isSubmitOpen, setIsSubmitOpen] = useState(false);

  const timeLeft = useDeadlineTimer(assignment?.deadline);

  if (!assignmentId) {
    return <div className="text-center py-16">Задание не указано</div>;
  }

  if (loadingAssignment) {
    return <div className="text-center py-16 text-[var(--color-text-muted)]">Загрузка...</div>;
  }

  if (!assignment) {
    return <div className="text-center py-16">Задание не найдено</div>;
  }

  const isReviewed = submission?.status === 'REVIEWED';
  const isReturned = submission?.status === 'RETURNED';
  const canSubmit = !submission || submission.status === 'DRAFT' || isReturned;

  return (
    <div className="space-y-6 max-w-3xl mx-auto px-4 py-4 sm:px-6">
      {/* Шапка */}
      <div className="flex items-center gap-3">
        <button
          onClick={navigateBack}
          className="rounded-md p-2 min-h-[44px] min-w-[44px] text-[var(--color-text-muted)] hover:bg-[var(--color-bg)] transition-colors"
          aria-label="Назад"
        >
          <ArrowLeft size={20} />
        </button>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <Badge variant="info">{typeLabels[assignment.assignmentType] || assignment.assignmentType}</Badge>
            {timeLeft.overdue && <Badge variant="danger">Просрочено</Badge>}
            {timeLeft.urgent && !timeLeft.overdue && <Badge variant="warning">Срочно</Badge>}
            {submission && (
              <Badge variant={statusVariants[submission.status] || 'default'}>
                {statusLabels[submission.status] || submission.status}
              </Badge>
            )}
          </div>
          <h1 className="text-xl font-bold text-[var(--color-text)] mt-1 truncate">{assignment.title}</h1>
        </div>
      </div>

      {/* Информация о задании */}
      <Card className="p-4 space-y-3">
        {assignment.description && (
          <p className="text-sm text-[var(--color-text)] whitespace-pre-wrap">{assignment.description}</p>
        )}

        <div className="flex flex-wrap gap-4 text-sm text-[var(--color-text-muted)] pt-2 border-t border-[var(--color-border)]">
          <div className="flex items-center gap-1">
            <Clock size={14} />
            <span className={timeLeft.overdue ? 'text-red-600 font-medium' : timeLeft.urgent ? 'text-amber-600 font-medium' : ''}>
              {timeLeft.text}
            </span>
          </div>
          <div className="flex items-center gap-1">
            <CalendarClock size={14} />
            {formatDate(assignment.deadline)}
          </div>
          {assignment.maxScore && (
            <div className="flex items-center gap-1">
              <CheckCircle2 size={14} />
              Макс. {assignment.maxScore} баллов
            </div>
          )}
          {assignment.createdByName && (
            <div className="flex items-center gap-1">
              <User size={14} />
              {assignment.createdByName}
            </div>
          )}
        </div>

        {/* Файлы преподавателя */}
        {assignment.attachedFiles && assignment.attachedFiles.length > 0 && (
          <div className="pt-2">
            <div className="text-xs font-medium text-[var(--color-text-muted)] mb-1 uppercase tracking-wide">
              Материалы задания
            </div>
            <div className="flex flex-wrap gap-2">
              {assignment.attachedFiles.map((f, i) => (
                <a
                  key={i}
                  href={f.fileUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1 rounded-md bg-blue-50 px-2 py-1 text-xs text-blue-700 hover:bg-blue-100 min-h-[32px]"
                >
                  <FileText size={12} />
                  {f.fileName}
                </a>
              ))}
            </div>
          </div>
        )}
      </Card>

      {/* Блок оценки (если проверено) */}
      {(isReviewed || isReturned) && submission && (
        <Card className="p-4 space-y-3">
          <div className="flex items-center gap-2">
            {isReviewed ? (
              <CheckCircle2 size={20} className="text-green-600" />
            ) : (
              <RotateCcw size={20} className="text-amber-600" />
            )}
            <h2 className="text-lg font-semibold text-[var(--color-text)]">
              {isReviewed ? 'Результат проверки' : 'Требуется доработка'}
            </h2>
          </div>

          {submission.totalScore !== undefined && assignment.maxScore && (
            <div className="space-y-1">
              <div className="flex items-center justify-between text-sm">
                <span className="text-[var(--color-text-muted)]">Балл</span>
                <span className="font-bold text-[var(--color-text)]">
                  {submission.totalScore} / {assignment.maxScore}
                </span>
              </div>
              <Progress
                value={submission.totalScore}
                max={assignment.maxScore}
                color="green"
                size="md"
              />
            </div>
          )}

          {submission.teacherFeedback && (
            <div className="rounded-lg bg-green-50 p-3 text-sm text-green-800">
              <div className="flex items-center gap-1 font-medium mb-1">
                <MessageSquare size={14} />
                Комментарий преподавателя
              </div>
              {submission.teacherFeedback}
            </div>
          )}

          {submission.reviewedAt && (
            <div className="text-xs text-[var(--color-text-muted)]">
              Проверено: {formatDate(submission.reviewedAt)}
              {submission.reviewedByName && ` · ${submission.reviewedByName}`}
            </div>
          )}
        </Card>
      )}

      {/* Кнопка сдачи — touch-friendly */}
      {canSubmit && (
        <Button
          onClick={() => setIsSubmitOpen(true)}
          className="w-full min-h-[48px] text-base"
        >
          <Send size={18} className="mr-2" />
          Сдать работу
        </Button>
      )}

      {/* Информация о сданной работе */}
      {(submission?.status === 'SUBMITTED' || submission?.status === 'REVIEWING') && (
        <Card className="p-4 space-y-3">
          <div className="flex items-center gap-2 text-[var(--color-text-muted)]">
            <Clock size={18} />
            <span className="text-sm">
              Работа {submission.status === 'SUBMITTED' ? 'сдана' : 'на проверке'} · {formatDate(submission.submittedAt)}
            </span>
          </div>
          {submission.studentComment && (
            <div className="text-sm text-[var(--color-text)] whitespace-pre-wrap">
              {submission.studentComment}
            </div>
          )}
          {submission.solutionFiles && submission.solutionFiles.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {submission.solutionFiles.map((f, i) => (
                <a
                  key={i}
                  href={f.fileUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1 rounded-md bg-blue-50 px-2 py-1 text-xs text-blue-700 hover:bg-blue-100"
                >
                  <FileText size={12} />
                  {f.fileName}
                </a>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* Модалка сдачи */}
      <StudentAssignmentSubmitModal
        assignmentId={assignmentId}
        isOpen={isSubmitOpen}
        onClose={() => setIsSubmitOpen(false)}
      />
    </div>
  );
};
