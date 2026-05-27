import { create } from 'zustand';

interface User {
  id: string;
  username: string;
  fullName: string;
  email?: string;
  role: string;
}

interface AuthState {
  accessToken: string | null;
  role: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (token: string, refresh: string, role: string, user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: localStorage.getItem('accessToken'),
  role: localStorage.getItem('role'),
  user: JSON.parse(localStorage.getItem('user') || 'null'),
  isAuthenticated: !!localStorage.getItem('accessToken'),
  login: (token, refresh, role, user) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('refreshToken', refresh);
    localStorage.setItem('role', role);
    localStorage.setItem('user', JSON.stringify(user));
    set({ accessToken: token, role, user, isAuthenticated: true });
  },
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('role');
    localStorage.removeItem('user');
    set({ accessToken: null, role: null, user: null, isAuthenticated: false });
  },
}));
