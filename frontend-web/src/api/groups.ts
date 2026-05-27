import { api } from './client';

export interface StudyGroup {
  id: string;
  name: string;
  course: number;
}

export const groupsApi = {
  getAll: () => api.get<StudyGroup[]>('/study-groups').then((r) => r.data),
  getById: (id: string) => api.get<StudyGroup>(`/study-groups/${id}`).then((r) => r.data),
};
