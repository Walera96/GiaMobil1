import { api } from './client';

export interface Gek {
  id: string;
  name: string;
}

export const geksApi = {
  getAll: () => api.get<Gek[]>('/geks').then((r) => r.data),
};
