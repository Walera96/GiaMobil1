import { create } from 'zustand';
import type { ScoringConfig } from '../api/assignments';

/**
 * Zustand store для конфигурации системы оценивания.
 * Хранит черновик ScoringConfig перед отправкой на backend.
 */

interface ScoringState {
  config: ScoringConfig;
  setConfig: (config: ScoringConfig) => void;
  updateCriteria: (index: number, field: string, value: unknown) => void;
  addCriterion: () => void;
  removeCriterion: (index: number) => void;
  updateThresholds: (field: string, value: number | undefined) => void;
  setType: (type: string) => void;
  setMaxTotalScore: (score: number) => void;
  setPassingScore: (score: number) => void;
  setAllowRetake: (allow: boolean) => void;
  reset: () => void;
  /** Сумма весов всех критериев */
  totalWeight: () => number;
  /** Валидность конфигурации */
  isValid: () => boolean;
}

const defaultConfig: ScoringConfig = {
  type: 'weighted',
  criteria: [
    { name: 'Посещаемость', weight: 8, maxPoints: 8, order: 1 },
    { name: 'Промежуточная аттестация', weight: 20, maxPoints: 20, order: 2 },
    { name: 'Практические работы', weight: 25, maxPoints: 25, order: 3 },
    { name: 'Финальный экзамен', weight: 25, maxPoints: 25, order: 4 },
    { name: 'Дополнительные баллы', weight: 22, maxPoints: 22, order: 5 },
  ],
  maxTotalScore: 100,
  thresholds: {
    excellent: 90,
    good: 75,
    satisfactory: 60,
    pass: 50,
  },
  allowRetake: true,
  cumulative: false,
  passingScore: 60,
};

const examTemplate: ScoringConfig = {
  type: 'exam_5point',
  criteria: [
    { name: 'Теория', weight: 40, maxPoints: 40, order: 1 },
    { name: 'Практика', weight: 40, maxPoints: 40, order: 2 },
    { name: 'Бонусы', weight: 20, maxPoints: 20, order: 3 },
  ],
  maxTotalScore: 100,
  thresholds: { excellent: 90, good: 75, satisfactory: 60, pass: 50 },
  allowRetake: false,
  cumulative: false,
  passingScore: 60,
};

const projectTemplate: ScoringConfig = {
  type: 'criteria',
  criteria: [
    { name: 'ТЗ и анализ', weight: 20, maxPoints: 20, order: 1 },
    { name: 'Проектирование', weight: 25, maxPoints: 25, order: 2 },
    { name: 'Реализация', weight: 30, maxPoints: 30, order: 3 },
    { name: 'Документация', weight: 15, maxPoints: 15, order: 4 },
    { name: 'Защита', weight: 10, maxPoints: 10, order: 5 },
  ],
  maxTotalScore: 100,
  thresholds: { excellent: 90, good: 75, satisfactory: 60, pass: 50 },
  allowRetake: true,
  cumulative: false,
  passingScore: 60,
};

export const scoringTemplates: Record<string, ScoringConfig> = {
  standard: defaultConfig,
  exam: examTemplate,
  project: projectTemplate,
};

export const useScoringStore = create<ScoringState>((set, get) => ({
  config: { ...defaultConfig },

  setConfig: (config) => set({ config: { ...config } }),

  updateCriteria: (index, field, value) =>
    set((state) => {
      const criteria = [...(state.config.criteria || [])];
      if (criteria[index]) {
        criteria[index] = { ...criteria[index], [field]: value };
      }
      return { config: { ...state.config, criteria } };
    }),

  addCriterion: () =>
    set((state) => {
      const criteria = [...(state.config.criteria || [])];
      const nextOrder = criteria.length > 0 ? Math.max(...criteria.map((c) => c.order || 0)) + 1 : 1;
      criteria.push({ name: 'Новый критерий', weight: 0, maxPoints: 0, order: nextOrder });
      return { config: { ...state.config, criteria } };
    }),

  removeCriterion: (index) =>
    set((state) => {
      const criteria = [...(state.config.criteria || [])];
      criteria.splice(index, 1);
      return { config: { ...state.config, criteria } };
    }),

  updateThresholds: (field, value) =>
    set((state) => ({
      config: {
        ...state.config,
        thresholds: { ...state.config.thresholds, [field]: value },
      },
    })),

  setType: (type) => set((state) => ({ config: { ...state.config, type } })),
  setMaxTotalScore: (score) => set((state) => ({ config: { ...state.config, maxTotalScore: score } })),
  setPassingScore: (score) => set((state) => ({ config: { ...state.config, passingScore: score } })),
  setAllowRetake: (allow) => set((state) => ({ config: { ...state.config, allowRetake: allow } })),

  reset: () => set({ config: { ...defaultConfig } }),

  totalWeight: () => {
    const criteria = get().config.criteria || [];
    return criteria.reduce((sum, c) => sum + (c.weight || 0), 0);
  },

  isValid: () => {
    const state = get();
    const totalWeight = state.totalWeight();
    return totalWeight === 100 && (state.config.criteria || []).every((c) => c.name?.trim());
  },
}));
