import api from './axios';

export interface StatementRecordDto {
  id: string;
  studentId: string;
  studentName: string;
  recordBookNumber?: string;
  currentControl?: number;
  attendance?: number;
  activity?: number;
  examScore?: number;
  totalScore?: number;
  ectsGrade?: string;
  fivePointGrade?: number;
}

export interface StatementDto {
  id: string;
  statementNumber?: string;
  academicYear?: string;
  semester?: string;
  groupId: string;
  groupName?: string;
  disciplineId?: string;
  disciplineName?: string;
  teacherId?: string;
  teacherName?: string;
  status: string;
  records: StatementRecordDto[];
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateStatementRequest {
  statementNumber?: string;
  academicYear?: string;
  semester?: string;
  groupId: string;
  disciplineId?: string;
  teacherId?: string;
  records?: StatementRecordDto[];
}

export const statementsApi = {
  getAll: () => api.get<StatementDto[]>('/statements').then((r) => r.data),
  getById: (id: string) => api.get<StatementDto>(`/statements/${id}`).then((r) => r.data),
  create: (data: CreateStatementRequest) => api.post<StatementDto>('/statements', data).then((r) => r.data),
  update: (id: string, data: CreateStatementRequest) =>
    api.put<StatementDto>(`/statements/${id}`, data).then((r) => r.data),
  updateRecord: (statementId: string, recordId: string, data: StatementRecordDto) =>
    api.put<StatementDto>(`/statements/${statementId}/records/${recordId}`, data).then((r) => r.data),
  changeStatus: (id: string, status: string) =>
    api.patch<StatementDto>(`/statements/${id}/status?status=${status}`).then((r) => r.data),
  delete: (id: string) => api.delete(`/statements/${id}`),
};
