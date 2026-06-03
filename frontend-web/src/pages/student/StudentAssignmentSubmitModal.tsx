import React, { useEffect, useRef, useState } from 'react';
import { useSubmitAssignment } from '../../hooks/useStudentAssignments';
import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/Button';
import { Textarea } from '../../components/ui/Textarea';
import { Badge } from '../../components/ui/Badge';
import { Upload, X, FileText, Send, Save } from 'lucide-react';
import type { AttachedFile } from '../../api/assignments';

interface StudentAssignmentSubmitModalProps {
  assignmentId: string;
  isOpen: boolean;
  onClose: () => void;
}

/** Ключ для автосохранения в localStorage */
const getDraftKey = (assignmentId: string) => `assignment_draft_${assignmentId}`;

export const StudentAssignmentSubmitModal: React.FC<StudentAssignmentSubmitModalProps> = ({
  assignmentId,
  isOpen,
  onClose,
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [comment, setComment] = useState('');
  const [files, setFiles] = useState<File[]>([]);
  const [lastSaved, setLastSaved] = useState<string | null>(null);

  const submitMutation = useSubmitAssignment();

  // Загружаем черновик из localStorage при открытии
  useEffect(() => {
    if (!isOpen) return;
    const draft = localStorage.getItem(getDraftKey(assignmentId));
    if (draft) {
      try {
        const parsed = JSON.parse(draft);
        setComment(parsed.comment || '');
        // Файлы из localStorage не восстанавливаем (File нельзя сериализовать)
      } catch {
        // игнорируем corrupted draft
      }
    }
  }, [isOpen, assignmentId]);

  // Автосохранение черновика каждые 10 секунд
  useEffect(() => {
    if (!isOpen) return;
    const timer = setInterval(() => {
      if (comment.trim() || files.length > 0) {
        localStorage.setItem(
          getDraftKey(assignmentId),
          JSON.stringify({ comment, savedAt: new Date().toISOString() })
        );
        setLastSaved(new Date().toLocaleTimeString('ru-RU'));
      }
    }, 10000);
    return () => clearInterval(timer);
  }, [isOpen, assignmentId, comment, files]);

  const handleSubmit = () => {
    const attachedFiles: AttachedFile[] = files.map((f) => ({
      fileName: f.name,
      fileUrl: URL.createObjectURL(f),
      fileSize: f.size,
    }));

    submitMutation.mutate(
      {
        assignmentId,
        payload: {
          solutionFiles: attachedFiles,
          studentComment: comment.trim() || undefined,
        },
      },
      {
        onSuccess: () => {
          localStorage.removeItem(getDraftKey(assignmentId));
          setComment('');
          setFiles([]);
          setLastSaved(null);
          onClose();
        },
      }
    );
  };

  /** Drag & drop */
  const handleDragOver = (e: React.DragEvent) => e.preventDefault();
  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    if (e.dataTransfer.files.length > 0) {
      setFiles((prev) => [...prev, ...Array.from(e.dataTransfer.files)]);
    }
  };

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Сдача работы" size="lg">
      <div className="space-y-4">
        {/* Комментарий */}
        <Textarea
          label="Комментарий к работе"
          placeholder="Опишите, что было сделано..."
          rows={4}
          value={comment}
          onChange={(e) => setComment(e.target.value)}
        />

        {/* Drag & drop */}
        <div>
          <label className="mb-1 block text-sm font-medium text-[var(--color-text)]">
            Прикреплённые файлы
          </label>
          <div
            onDragOver={handleDragOver}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className="cursor-pointer rounded-lg border-2 border-dashed border-[var(--color-border)] p-6 text-center transition-colors hover:border-[var(--color-primary)] hover:bg-blue-50/50 active:bg-blue-100/50"
          >
            <Upload className="mx-auto mb-2 text-[var(--color-text-muted)]" size={28} />
            <p className="text-sm text-[var(--color-text-muted)]">
              Нажмите или перетащите файлы сюда
            </p>
            <input
              ref={fileInputRef}
              type="file"
              multiple
              className="hidden"
              onChange={(e) => {
                if (e.target.files) setFiles((prev) => [...prev, ...Array.from(e.target.files)]);
              }}
            />
          </div>

          {/* Превью файлов */}
          {files.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-2">
              {files.map((f, i) => (
                <Badge key={i} variant="default" className="flex items-center gap-1 py-1.5">
                  <FileText size={12} />
                  <span className="max-w-[120px] truncate">{f.name}</span>
                  <span className="text-[10px] text-gray-400">
                    {(f.size / 1024).toFixed(0)} KB
                  </span>
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      removeFile(i);
                    }}
                    className="ml-1 rounded-full p-0.5 hover:bg-red-100 hover:text-red-600"
                    aria-label="Удалить файл"
                  >
                    <X size={12} />
                  </button>
                </Badge>
              ))}
            </div>
          )}
        </div>

        {/* Автосохранение статус */}
        {lastSaved && (
          <div className="flex items-center gap-1 text-xs text-[var(--color-text-muted)]">
            <Save size={12} />
            Черновик сохранён в {lastSaved}
          </div>
        )}

        {/* Кнопки — touch-friendly min-h-[44px] */}
        <div className="flex flex-col gap-3 pt-2">
          <Button
            onClick={handleSubmit}
            isLoading={submitMutation.isPending}
            className="min-h-[44px]"
          >
            <Send size={16} className="mr-2" />
            Отправить на проверку
          </Button>
          <Button
            variant="ghost"
            onClick={onClose}
            className="min-h-[44px]"
          >
            Отмена
          </Button>
        </div>
      </div>
    </Modal>
  );
};
