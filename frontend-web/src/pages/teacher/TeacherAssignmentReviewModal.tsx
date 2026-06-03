import React, { useState } from 'react';
import { useReviewSubmission } from '../../hooks/useTeacherAssignments';
import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/Button';
import { Textarea } from '../../components/ui/Textarea';
import { Slider } from '../../components/ui/Slider';
import { Input } from '../../components/ui/Input';
import {
  CheckCircle2, RotateCcw, MessageSquare, FileText, User
} from 'lucide-react';
import type { Submission } from '../../api/assignments';

/** Генерирует аватар с инициалами */
function getInitials(name: string | undefined): string {
  if (!name) return '?';
  return name.split(' ').map((p) => p[0]).join('').slice(0, 2).toUpperCase();
}

interface TeacherAssignmentReviewModalProps {
  assignmentId: string;
  submission: Submission | null;
  maxScore: number;
  isOpen: boolean;
  onClose: () => void;
}

export const TeacherAssignmentReviewModal: React.FC<TeacherAssignmentReviewModalProps> = ({
  assignmentId,
  submission,
  maxScore,
  isOpen,
  onClose,
}) => {
  const [score, setScore] = useState(submission?.totalScore ?? maxScore);
  const [feedback, setFeedback] = useState(submission?.teacherFeedback ?? '');
  const [comment, setComment] = useState(submission?.teacherComment ?? '');
  const [returnForRevision, setReturnForRevision] = useState(false);

  const reviewMutation = useReviewSubmission();

  // Сброс формы при открытии новой сдачи
  React.useEffect(() => {
    if (submission) {
      setScore(submission.totalScore ?? maxScore);
      setFeedback(submission.teacherFeedback ?? '');
      setComment(submission.teacherComment ?? '');
      setReturnForRevision(false);
    }
  }, [submission, maxScore]);

  if (!submission) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    reviewMutation.mutate(
      {
        assignmentId,
        submissionId: submission.id,
        payload: {
          totalScore: score,
          teacherFeedback: feedback,
          teacherComment: comment,
          returnForRevision: returnForRevision,
        },
      },
      {
        onSuccess: () => {
          onClose();
        },
      }
    );
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Проверка работы" size="md">
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Информация о студенте */}
        <div className="flex items-center gap-3 rounded-lg bg-[var(--color-bg)] p-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[var(--color-primary)] text-white text-sm font-bold">
            {getInitials(submission.studentName)}
          </div>
          <div>
            <div className="text-sm font-semibold text-[var(--color-text)]">
              {submission.studentName || 'Неизвестный студент'}
            </div>
            <div className="text-xs text-[var(--color-text-muted)]">
              Версия {submission.version}
              {submission.previousVersionId && ' (пересдача)'}
            </div>
          </div>
        </div>

        {/* Файлы студента */}
        {submission.solutionFiles && submission.solutionFiles.length > 0 && (
          <div>
            <label className="mb-1 block text-sm font-medium text-[var(--color-text)]">
              Файлы студента
            </label>
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
          </div>
        )}

        {/* Комментарий студента */}
        {submission.studentComment && (
          <div className="rounded-lg bg-amber-50 p-3 text-sm text-amber-800">
            <div className="mb-1 flex items-center gap-1 font-medium">
              <MessageSquare size={14} />
              Комментарий студента
            </div>
            {submission.studentComment}
          </div>
        )}

        {/* Оценка */}
        <div className="space-y-2">
          <label className="block text-sm font-medium text-[var(--color-text)]">
            Балл (макс. {maxScore})
          </label>
          <div className="flex items-center gap-4">
            <div className="flex-1">
              <Slider min={0} max={maxScore} step={1} value={score} onChange={setScore} />
            </div>
            <Input
              type="number"
              min={0}
              max={maxScore}
              value={score}
              onChange={(e) => setScore(Number(e.target.value))}
              className="w-20 text-center"
            />
          </div>
        </div>

        {/* Обратная связь */}
        <Textarea
          label="Обратная связь (видна студенту)"
          placeholder="Опишите, что хорошо, а что нужно исправить..."
          rows={3}
          value={feedback}
          onChange={(e) => setFeedback(e.target.value)}
        />

        {/* Внутренний комментарий */}
        <Textarea
          label="Внутренний комментарий (только для преподавателя)"
          placeholder="Заметки для себя..."
          rows={2}
          value={comment}
          onChange={(e) => setComment(e.target.value)}
        />

        {/* На доработку */}
        <label className="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            checked={returnForRevision}
            onChange={(e) => setReturnForRevision(e.target.checked)}
            className="h-4 w-4 rounded border-gray-300 text-[var(--color-danger)] focus:ring-[var(--color-danger)]"
          />
          <span className="text-sm text-[var(--color-danger)] flex items-center gap-1">
            <RotateCcw size={14} />
            Вернуть на доработку
          </span>
        </label>

        {/* Кнопки */}
        <div className="flex justify-end gap-3 pt-2">
          <Button type="button" variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button type="submit" isLoading={reviewMutation.isPending}>
            <CheckCircle2 size={16} className="mr-2" />
            Сохранить оценку
          </Button>
        </div>
      </form>
    </Modal>
  );
};
