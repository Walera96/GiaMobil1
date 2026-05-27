import api from './axios';

export interface GradeDto {
  id: string;
  studentId: string;
  studentName: string;
  disciplineId?: string;
  disciplineName?: string;
  subjectName: string;
  score?: number;
  currentControl?: number;
  attendance?: number;
  activity?: number;
  examScore?: number;
  totalScore?: number;
  ectsGrade?: string;
  fivePointGrade?: number;
  semester?: string;
}

export interface CreateGradeRequest {
  studentId: string;
  disciplineId?: string;
  subjectName: string;
  score?: number;
  currentControl?: number;
  attendance?: number;
  activity?: number;
  examScore?: number;
  semester?: string;
}

export const gradesApi = {
  getAll: () => api.get<GradeDto[]>('/grades').then((r) => r.data),
  getByStudent: (studentId: string) =>
    api.get<GradeDto[]>(`/grades/student/${studentId}`).then((r) => r.data),
  getByDiscipline: (disciplineId: string) =>
    api.get<GradeDto[]>(`/grades/discipline/${disciplineId}`).then((r) => r.data),
  getByGroup: (groupId: string) =>
    api.get<GradeDto[]>(`/grades/group/${groupId}`).then((r) => r.data),
  create: (data: CreateGradeRequest) => api.post<GradeDto>('/grades', data).then((r) => r.data),
  update: (id: string, data: CreateGradeRequest) =>
    api.put<GradeDto>(`/grades/${id}`, data).then((r) => r.data),
  delete: (id: string) => api.delete(`/grades/${id}`),
};
