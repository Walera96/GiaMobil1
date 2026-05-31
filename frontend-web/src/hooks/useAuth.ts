import { useAuthStore } from '../store/authStore';

export function useAuth() {
  const { isAuthenticated, roles, user, logout } = useAuthStore();

  const hasRole = (...checkRoles: string[]) => {
    return roles.some((r) => checkRoles.includes(r));
  };

  return { isAuthenticated, roles, user, hasRole, logout };
}
