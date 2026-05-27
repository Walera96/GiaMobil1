import { api } from './client';

export interface Direction {
  id: string;
  code: string;
  name: string;
}

export const directionsApi = {
  getAll: () => api.get<Direction[]>('/directions').then((r) => r.data),
};
