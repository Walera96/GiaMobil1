import api from './axios';

export interface DisciplineDto {
  id: string;
  code: string;
  name: string;
  hours?: number;
  ectsCredits?: number;
  course?: number;
  semester?: string;
  controlType?: string;
  directionId?: string;
  directionName?: string;
}

export interface CreateDisciplineRequest {
  code: string;
  name: string;
  hours?: number;
  ectsCredits?: number;
  course?: number;
  semester?: string;
  controlType?: string;
  directionId?: string;
}

export const disciplinesApi = {
  getAll: () => api.get<DisciplineDto[]>('/disciplines').then((r) => r.data),
  getById: (id: string) => api.get<DisciplineDto>(`/disciplines/${id}`).then((r) => r.data),
  create: (data: CreateDisciplineRequest) => api.post<DisciplineDto>('/disciplines', data).then((r) => r.data),
  update: (id: string, data: CreateDisciplineRequest) =>
    api.put<DisciplineDto>(`/disciplines/${id}`, data).then((r) => r.data),
  delete: (id: string) => api.delete(`/disciplines/${id}`),
};
