import { create } from 'zustand';

interface User {
  id: string;
  username: string;
  fullName: string;
  email?: string;
}

export type UserRole =
  | 'SYSTEM_ADMIN' | 'UNIVERSITY_ADMIN'
  | 'DEAN' | 'DEAN_SECRETARY'
  | 'DEPARTMENT_HEAD' | 'DEPARTMENT_SECRETARY' | 'SUPERVISOR'
  | 'GEK_SECRETARY' | 'GEK_CHAIRMAN' | 'GEK_MEMBER'
  | 'METHODIST'
  | 'STUDENT'
  // Legacy (переходные)
  | 'ADMIN' | 'SECRETARY' | 'CHAIRMAN';

export type Portal = 'admin' | 'deanery' | 'department' | 'gek' | 'methodist' | 'student' | 'teacher_portal' | 'student_portal';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: User | null;
  roles: UserRole[];
  availablePortals: Portal[];
  primaryPortal: Portal | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  login: (token: string, refresh: string, user: User, roles: UserRole[], portals: Portal[], primary: Portal) => void;
  logout: () => void;
  setPrimaryPortal: (portal: Portal) => void;
  initFromStorage: () => void;
}

const STORAGE_KEYS = {
  accessToken: 'accessToken',
  refreshToken: 'refreshToken',
  user: 'user',
  roles: 'roles',
  portals: 'portals',
  primaryPortal: 'primaryPortal',
};

const parseJSON = <T,>(str: string | null, fallback: T): T => {
  if (!str) return fallback;
  try { return JSON.parse(str); } catch { return fallback; }
};

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  refreshToken: null,
  user: null,
  roles: [],
  availablePortals: [],
  primaryPortal: null,
  isAuthenticated: false,
  isLoading: true,

  login: (token, refresh, user, roles, portals, primary) => {
    localStorage.setItem(STORAGE_KEYS.accessToken, token);
    localStorage.setItem(STORAGE_KEYS.refreshToken, refresh);
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
    localStorage.setItem(STORAGE_KEYS.roles, JSON.stringify(roles));
    localStorage.setItem(STORAGE_KEYS.portals, JSON.stringify(portals));
    localStorage.setItem(STORAGE_KEYS.primaryPortal, primary);
    set({
      accessToken: token,
      refreshToken: refresh,
      user,
      roles,
      availablePortals: portals,
      primaryPortal: primary,
      isAuthenticated: true,
      isLoading: false,
    });
  },

  logout: () => {
    Object.values(STORAGE_KEYS).forEach((k) => localStorage.removeItem(k));
    set({
      accessToken: null,
      refreshToken: null,
      user: null,
      roles: [],
      availablePortals: [],
      primaryPortal: null,
      isAuthenticated: false,
      isLoading: false,
    });
  },

  setPrimaryPortal: (portal) => {
    localStorage.setItem(STORAGE_KEYS.primaryPortal, portal);
    set({ primaryPortal: portal });
  },

  initFromStorage: () => {
    const token = localStorage.getItem(STORAGE_KEYS.accessToken);
    const refresh = localStorage.getItem(STORAGE_KEYS.refreshToken);
    const user = parseJSON<User | null>(localStorage.getItem(STORAGE_KEYS.user), null);
    const roles = parseJSON<UserRole[]>(localStorage.getItem(STORAGE_KEYS.roles), []);
    const portals = parseJSON<Portal[]>(localStorage.getItem(STORAGE_KEYS.portals), []);
    const primary = localStorage.getItem(STORAGE_KEYS.primaryPortal) as Portal | null;

    set({
      accessToken: token,
      refreshToken: refresh,
      user,
      roles,
      availablePortals: portals,
      primaryPortal: primary,
      isAuthenticated: !!token,
      isLoading: false,
    });
  },
}));
