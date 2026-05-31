import { api } from './client';
import type { UserRole, Portal } from '../store/authStore';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: {
    id: string;
    username: string;
    fullName: string;
    email?: string;
  };
  roles: UserRole[];
  availablePortals: Portal[];
  primaryPortal: Portal;
}

export const authApi = {
  login: (data: LoginRequest) => api.post<LoginResponse>('/auth/login', data),
  refresh: (refreshToken: string) =>
    api.post<LoginResponse>('/auth/refresh', { refreshToken }),
  me: () => api.get<LoginResponse['user'] & { roles: UserRole[]; availablePortals: Portal[]; primaryPortal: Portal }>('/auth/me'),
};
