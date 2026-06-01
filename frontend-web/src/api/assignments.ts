import { api } from './client';

export interface AttachedFile {
  fileName: string;
  fileUrl: string;
  fileSize: number;
}

export interface ScoringConfig {
  type: string;
  criteria: Array<{
    name: string;
    description?: string;
    weight?: number;
    maxPoints?: number;
    required?: boolean;
    order?: number;
  }>;
  maxTotalScore?: number;
  thresholds?: {
    excellent?: number;
    good?: number;
    satisfactory?: number;
    pass?: number;
  };
  allowRetake?: boolean;
  cumulative?: boolean;
  semester?: number;
  passingScore?: number;
}

export type AssignmentType = 'VKR' | 'COURSEWORK' | 'LAB' | 'PRACTICE' | 'EXAM' | 'HOMEWORK';

export interface Assignment {
  id: string;
  title: string;
  description?: string;
  assignmentType: AssignmentType;
  createdById: string;
  createdByName?: string;
  targetGroupId?: string;
  targetGroupName?: string;
  targetStudentIds?: string[];
  deadline?: string;
  allowLateSubmission: boolean;
  maxScore?: number;
  scoringConfig?: ScoringConfig;
  attachedFiles?: AttachedFile[];
  totalSubmissions?: number;
  pendingSubmissions?: number;
  createdAt: string;
  updatedAt: string;
}

export interface AssignmentCreateRequest {
  title: string;
  description?: string;
  assignmentType: AssignmentType;
  targetGroupId?: string;
  targetStudentIds?: string[];
  deadline?: string;
  allowLateSubmission: boolean;
  maxScore?: number;
  scoringConfig?: ScoringConfig;
  attachedFiles?: AttachedFile[];
}

export type SubmissionStatus = 'DRAFT' | 'SUBMITTED' | 'REVIEWING' | 'REVIEWED' | 'RETURNED';

export interface Submission {
  id: string;
  assignmentId: string;
  assignmentTitle?: string;
  studentId: string;
  studentName?: string;
  studentGroup?: string;
  solutionFiles?: AttachedFile[];
  studentComment?: string;
  status: SubmissionStatus;
  submittedAt?: string;
  score?: unknown;
  totalScore?: number;
  teacherFeedback?: string;
  teacherComment?: string;
  reviewedById?: string;
  reviewedByName?: string;
  reviewedAt?: string;
  version: number;
  previousVersionId?: string;
}

export interface ReviewRequest {
  totalScore: number;
  teacherFeedback: string;
  teacherComment?: string;
  returnForRevision: boolean;
}

/* ========== API для преподавателя ========== */

export const teacherAssignmentsApi = {
  /** Создать задание */
  create: (data: AssignmentCreateRequest) =>
    api.post<Assignment>('/teacher/assignments', data),

  /** Список заданий текущего преподавателя */
  getMyAssignments: () =>
    api.get<Assignment[]>('/teacher/assignments/my'),

  /** Детали задания */
  getById: (id: string) =>
    api.get<Assignment>(`/teacher/assignments/${id}`),

  /** Удалить задание */
  delete: (id: string) =>
    api.delete<void>(`/teacher/assignments/${id}`),

  /** Сдачи по заданию (опционально с фильтром по статусу) */
  getSubmissions: (assignmentId: string, status?: SubmissionStatus) =>
    api.get<Submission[]>(`/teacher/assignments/${assignmentId}/submissions`, {
      params: status ? { status } : undefined,
    }),

  /** Проверить сдачу */
  reviewSubmission: (assignmentId: string, submissionId: string, data: ReviewRequest) =>
    api.post<Submission>(`/teacher/assignments/${assignmentId}/submissions/${submissionId}/review`, data),
};

/* ========== API для студента ========== */

export const studentAssignmentsApi = {
  /** Мои задания */
  getMyAssignments: () =>
    api.get<Assignment[]>('/student/assignments/my'),

  /** Детали задания */
  getById: (id: string) =>
    api.get<Assignment>(`/student/assignments/${id}`),

  /** Сдать задание */
  submit: (assignmentId: string, data: {
    solutionFiles?: AttachedFile[];
    studentComment?: string;
  }) =>
    api.post<Submission>(`/student/assignments/${assignmentId}/submit`, data),

  /** Мои сдачи */
  getMySubmissions: () =>
    api.get<Submission[]>('/student/assignments/submissions'),

  /** Сохранить черновик */
  saveDraft: (submissionId: string, data: {
    solutionFiles?: AttachedFile[];
    studentComment?: string;
  }) =>
    api.post<Submission>(`/student/assignments/submissions/${submissionId}/save-draft`, data),
};
