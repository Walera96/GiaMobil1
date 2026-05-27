import { api } from './client';

export interface Admission {
  id: string;
  studentId: string;
  studentFullName: string;
  groupName: string;
  brsScore: number;
  hasDebt: boolean;
  isAdmitted: boolean;
  checkedAt: string;
  createdAt: string;
}

export const admissionsApi = {
  getAll: () => api.get<Admission[]>('/admissions').then((r) => r.data),
};
