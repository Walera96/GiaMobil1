import React, { useState, useRef } from 'react';
import { useCreateAssignment } from '../../hooks/useTeacherAssignments';
import { useTeacherAssignmentsStore } from '../../store/teacherAssignmentsStore';
import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Textarea } from '../../components/ui/Textarea';
import { Select } from '../../components/ui/Select';
import { Slider } from '../../components/ui/Slider';
import { Badge } from '../../components/ui/Badge';
import { X, Upload, File } from 'lucide-react';
import type { AssignmentType, AssignmentCreateRequest, AttachedFile } from '../../api/assignments';

const typeOptions: Array<{ value: AssignmentType; label: string }> = [
  { value: 'VKR', label: 'ВКР' },
  { value: 'COURSEWORK', label: 'Курсовая работа' },
  { value: 'LAB', label: 'Лабораторная' },
  { value: 'PRACTICE', label: 'Практика' },
  { value: 'EXAM', label: 'Экзамен' },
  { value: 'HOMEWORK', label: 'Домашнее задание' },
];

/** Форматирует Date в строку для input[type="datetime-local"] */
function toDatetimeLocalValue(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export const TeacherAssignmentCreateModal: React.FC = () => {
  const { isCreateOpen, closeCreate, draftFiles, addDraftFiles, removeDraftFile, setDraftFiles } =
    useTeacherAssignmentsStore();
  const createMutation = useCreateAssignment();
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Локальное состояние формы
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [assignmentType, setAssignmentType] = useState<AssignmentType>('HOMEWORK');
  const [deadline, setDeadline] = useState('');
  const [allowLate, setAllowLate] = useState(false);
  const [maxScore, setMaxScore] = useState(100);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const resetForm = () => {
    setTitle('');
    setDescription('');
    setAssignmentType('HOMEWORK');
    setDeadline('');
    setAllowLate(false);
    setMaxScore(100);
    setErrors({});
    setDraftFiles([]);
  };

  const handleClose = () => {
    closeCreate();
    resetForm();
  };

  /** Валидация формы */
  const validate = (): boolean => {
    const nextErrors: Record<string, string> = {};
    if (!title.trim()) nextErrors.title = 'Введите название задания';
    if (title.trim().length > 500) nextErrors.title = 'Максимум 500 символов';
    if (!assignmentType) nextErrors.type = 'Выберите тип задания';
    if (deadline) {
      const d = new Date(deadline);
      if (isNaN(d.getTime())) nextErrors.deadline = 'Некорректная дата';
      else if (d < new Date()) nextErrors.deadline = 'Дедлайн не может быть в прошлом';
    }
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const payload: AssignmentCreateRequest = {
      title: title.trim(),
      description: description.trim() || undefined,
      assignmentType,
      deadline: deadline ? new Date(deadline).toISOString() : undefined,
      allowLateSubmission: allowLate,
      maxScore,
      attachedFiles: draftFiles.map((f) => ({
        fileName: f.name,
        fileUrl: URL.createObjectURL(f),
        fileSize: f.size,
      })),
    };

    createMutation.mutate(payload, {
      onSuccess: () => {
        handleClose();
      },
    });
  };

  /** Drag & drop обработчики */
  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };
  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    if (e.dataTransfer.files.length > 0) {
      addDraftFiles(Array.from(e.dataTransfer.files));
    }
  };

  return (
    <Modal isOpen={isCreateOpen} onClose={handleClose} title="Создать задание" size="lg">
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Название */}
        <Input
          label="Название задания *"
          placeholder="Например, Лабораторная работа №3"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          error={errors.title}
        />

        {/* Тип + дедлайн */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-[var(--color-text)]">
              Тип задания *
            </label>
            <Select
              options={typeOptions.map((o) => ({ value: o.value, label: o.label }))}
              value={assignmentType}
              onChange={(e) => setAssignmentType(e.target.value as AssignmentType)}
            />
            {errors.type && <p className="mt-1 text-xs text-[var(--color-danger)]">{errors.type}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-[var(--color-text)]">
              Дедлайн
            </label>
            <input
              type="datetime-local"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
              className={[
                'w-full rounded-md border px-3 py-2 text-sm outline-none transition-colors',
                'border-[var(--color-border)] bg-white text-[var(--color-text)]',
                'focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)] focus:ring-opacity-20',
                errors.deadline && 'border-[var(--color-danger)]',
              ].join(' ')}
            />
            {errors.deadline && <p className="mt-1 text-xs text-[var(--color-danger)]">{errors.deadline}</p>}
          </div>
        </div>

        {/* Описание */}
        <Textarea
          label="Описание"
          placeholder="Подробное описание задания, требования, критерии..."
          rows={4}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        {/* Макс. балл — слайдер + инпут */}
        <div className="space-y-2">
          <label className="block text-sm font-medium text-[var(--color-text)]">
            Максимальный балл
          </label>
          <div className="flex items-center gap-4">
            <div className="flex-1">
              <Slider min={1} max={100} step={1} value={maxScore} onChange={setMaxScore} />
            </div>
            <Input
              type="number"
              min={1}
              max={100}
              value={maxScore}
              onChange={(e) => setMaxScore(Number(e.target.value))}
              className="w-20 text-center"
            />
          </div>
        </div>

        {/* Разрешить позднюю сдачу */}
        <label className="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            checked={allowLate}
            onChange={(e) => setAllowLate(e.target.checked)}
            className="h-4 w-4 rounded border-gray-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
          />
          <span className="text-sm text-[var(--color-text)]">Разрешить сдачу после дедлайна</span>
        </label>

        {/* Загрузка файлов — drag & drop */}
        <div>
          <label className="mb-1 block text-sm font-medium text-[var(--color-text)]">
            Прикреплённые файлы
          </label>
          <div
            onDragOver={handleDragOver}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className={[
              'cursor-pointer rounded-lg border-2 border-dashed border-[var(--color-border)]',
              'p-6 text-center transition-colors hover:border-[var(--color-primary)] hover:bg-blue-50/50',
            ].join(' ')}
          >
            <Upload className="mx-auto mb-2 text-[var(--color-text-muted)]" size={28} />
            <p className="text-sm text-[var(--color-text-muted)]">
              Перетащите файлы сюда или нажмите для выбора
            </p>
            <input
              ref={fileInputRef}
              type="file"
              multiple
              className="hidden"
              onChange={(e) => {
                if (e.target.files) addDraftFiles(Array.from(e.target.files));
              }}
            />
          </div>

          {/* Список выбранных файлов */}
          {draftFiles.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-2">
              {draftFiles.map((f, i) => (
                <Badge key={i} variant="default" className="flex items-center gap-1">
                  <File size={12} />
                  <span className="max-w-[150px] truncate">{f.name}</span>
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      removeDraftFile(i);
                    }}
                    className="ml-1 hover:text-red-600"
                  >
                    <X size={12} />
                  </button>
                </Badge>
              ))}
            </div>
          )}
        </div>

        {/* Кнопки */}
        <div className="flex justify-end gap-3 pt-2">
          <Button type="button" variant="ghost" onClick={handleClose}>
            Отмена
          </Button>
          <Button type="submit" isLoading={createMutation.isPending}>
            Создать задание
          </Button>
        </div>
      </form>
    </Modal>
  );
};
