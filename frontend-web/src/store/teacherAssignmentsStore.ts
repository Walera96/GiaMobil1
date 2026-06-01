import { create } from 'zustand';
import type { AssignmentType, SubmissionStatus } from '../api/assignments';

/**
 * Локальное состояние UI портала преподавателя — фильтры, поиск, модальные окна.
 */

interface Filters {
  search: string;
  assignmentType: AssignmentType | 'ALL';
  groupId: string | 'ALL';
  status: 'ALL' | 'active' | 'expired';
}

interface TeacherAssignmentsState {
  // Фильтры списка заданий
  filters: Filters;
  setFilters: (filters: Partial<Filters>) => void;
  resetFilters: () => void;

  // Модальное окно создания задания
  isCreateOpen: boolean;
  openCreate: () => void;
  closeCreate: () => void;

  // Модальное окно проверки сдачи
  reviewSubmissionId: string | null;
  reviewAssignmentId: string | null;
  openReview: (assignmentId: string, submissionId: string) => void;
  closeReview: () => void;

  // Drag & drop файлов (временное хранение перед отправкой)
  draftFiles: File[];
  setDraftFiles: (files: File[]) => void;
  addDraftFiles: (files: File[]) => void;
  removeDraftFile: (index: number) => void;
}

const defaultFilters: Filters = {
  search: '',
  assignmentType: 'ALL',
  groupId: 'ALL',
  status: 'ALL',
};

export const useTeacherAssignmentsStore = create<TeacherAssignmentsState>((set) => ({
  filters: { ...defaultFilters },
  setFilters: (partial) =>
    set((state) => ({ filters: { ...state.filters, ...partial } })),
  resetFilters: () => set({ filters: { ...defaultFilters } }),

  isCreateOpen: false,
  openCreate: () => set({ isCreateOpen: true }),
  closeCreate: () => set({ isCreateOpen: false }),

  reviewSubmissionId: null,
  reviewAssignmentId: null,
  openReview: (assignmentId, submissionId) =>
    set({ reviewAssignmentId: assignmentId, reviewSubmissionId: submissionId }),
  closeReview: () =>
    set({ reviewAssignmentId: null, reviewSubmissionId: null }),

  draftFiles: [],
  setDraftFiles: (files) => set({ draftFiles: files }),
  addDraftFiles: (files) =>
    set((state) => ({ draftFiles: [...state.draftFiles, ...files] })),
  removeDraftFile: (index) =>
    set((state) => ({
      draftFiles: state.draftFiles.filter((_, i) => i !== index),
    })),
}));
