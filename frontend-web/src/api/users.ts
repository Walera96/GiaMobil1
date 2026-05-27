import { api } from './client';

export interface User {
  id: string;
  username: string;
  fullName: string;
  email: string;
  role: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  fullName: string;
  email: string;
  role: string;
}

export interface UpdateUserRequest {
  username: string;
  fullName: string;
  email: string;
  role: string;
}

export const usersApi = {
  getAll: () => api.get<User[]>('/users').then((r) => r.data),
  getById: (id: string) => api.get<User>(`/users/${id}`).then((r) => r.data),
  create: (data: CreateUserRequest) => api.post<User>('/users', data).then((r) => r.data),
  update: (id: string, data: UpdateUserRequest) => api.put<User>(`/users/${id}`, data).then((r) => r.data),
  delete: (id: string) => api.delete(`/users/${id}`),
};
