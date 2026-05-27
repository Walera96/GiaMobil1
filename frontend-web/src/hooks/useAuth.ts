import { useAuthStore } from '../store/authStore';

export function useAuth() {
  const { isAuthenticated, role, user, logout } = useAuthStore();

  const hasRole = (...roles: string[]) => {
    return role ? roles.includes(role) : false;
  };

  return { isAuthenticated, role, user, hasRole, logout };
}
