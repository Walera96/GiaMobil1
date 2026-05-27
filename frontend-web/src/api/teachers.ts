import api from './axios';

export interface TeacherDto {
  id: string;
  lastName: string;
  firstName: string;
  middleName?: string;
  fullName: string;
  department?: string;
  position?: string;
  degree?: string;
  email?: string;
}

export interface CreateTeacherRequest {
  lastName: string;
  firstName: string;
  middleName?: string;
  department?: string;
  position?: string;
  degree?: string;
  email?: string;
}

export const teachersApi = {
  getAll: () => api.get<TeacherDto[]>('/teachers').then((r) => r.data),
  getById: (id: string) => api.get<TeacherDto>(`/teachers/${id}`).then((r) => r.data),
  create: (data: CreateTeacherRequest) => api.post<TeacherDto>('/teachers', data).then((r) => r.data),
  update: (id: string, data: CreateTeacherRequest) =>
    api.put<TeacherDto>(`/teachers/${id}`, data).then((r) => r.data),
  delete: (id: string) => api.delete(`/teachers/${id}`),
};
