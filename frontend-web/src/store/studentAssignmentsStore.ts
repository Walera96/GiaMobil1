import { create } from 'zustand';

/**
 * Локальное состояние UI портала студента — фильтры, черновики, модальные окна.
 */

type StudentFilter = 'ALL' | 'OVERDUE' | 'TODAY' | 'ACTIVE' | 'COMPLETED';

interface StudentAssignmentsState {
  // Фильтр списка заданий
  filter: StudentFilter;
  setFilter: (filter: StudentFilter) => void;

  // Поиск
  search: string;
  setSearch: (search: string) => void;

  // Drag & drop файлы для сдачи
  draftFiles: File[];
  setDraftFiles: (files: File[]) => void;
  addDraftFiles: (files: File[]) => void;
  removeDraftFile: (index: number) => void;

  // Текст комментария студента (для автосохранения)
  draftComment: string;
  setDraftComment: (comment: string) => void;
}

export const useStudentAssignmentsStore = create<StudentAssignmentsState>((set) => ({
  filter: 'ALL',
  setFilter: (filter) => set({ filter }),

  search: '',
  setSearch: (search) => set({ search }),

  draftFiles: [],
  setDraftFiles: (files) => set({ draftFiles: files }),
  addDraftFiles: (files) =>
    set((state) => ({ draftFiles: [...state.draftFiles, ...files] })),
  removeDraftFile: (index) =>
    set((state) => ({
      draftFiles: state.draftFiles.filter((_, i) => i !== index),
    })),

  draftComment: '',
  setDraftComment: (comment) => set({ draftComment: comment }),
}));
