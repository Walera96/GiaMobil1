import { api } from './client';

export interface Student {
  id: string;
  lastName: string;
  firstName: string;
  middleName?: string;
  recordBookNumber?: string;
  thesisTopic?: string;
  supervisorName?: string;
  group?: {
    id: string;
    name: string;
    course: number;
    direction?: {
      id: string;
      code: string;
      name: string;
    };
  };
}

export const studentsApi = {
  getAll: (groupId?: string) =>
    api.get<Student[]>('/students', { params: groupId ? { groupId } : undefined }).then((r) => r.data),
  getById: (id: string) => api.get<Student>(`/students/${id}`).then((r) => r.data),
};
