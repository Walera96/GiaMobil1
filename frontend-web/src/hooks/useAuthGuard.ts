import { useMemo } from 'react';
import { useAuthStore, type UserRole } from '../store/authStore';

/**
 * Хук для проверки доступа пользователя к ресурсу на основе ролей.
 * Используется в AuthGuard-компоненте и для условного рендеринга UI.
 */

/** Карта порталов → требуемые роли */
const portalRoles: Record<string, UserRole[]> = {
  admin: ['SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
  deanery: ['DEAN', 'DEAN_SECRETARY', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN', 'METHODIST', 'GEK_SECRETARY', 'GEK_CHAIRMAN'],
  department: ['DEPARTMENT_HEAD', 'DEPARTMENT_SECRETARY', 'SUPERVISOR', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
  gek: ['GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
  methodist: ['METHODIST', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
  teacher_portal: ['SUPERVISOR', 'DEPARTMENT_HEAD', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
  student_portal: ['STUDENT', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
  student: ['STUDENT', 'SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'],
};

export function useAuthGuard() {
  const { roles, isAuthenticated, isLoading } = useAuthStore();

  const userRoles = useMemo(() => new Set(roles), [roles]);

  /** Проверяет, есть ли у пользователя хотя бы одна из требуемых ролей */
  const hasRole = (...requiredRoles: UserRole[]): boolean => {
    return requiredRoles.some((r) => userRoles.has(r));
  };

  /** Проверяет доступ к конкретному порталу */
  const hasPortalAccess = (portal: string): boolean => {
    const required = portalRoles[portal];
    if (!required) return true;
    return hasRole(...required);
  };

  /** Список доступных порталов для текущего пользователя */
  const availablePortals = useMemo(() => {
    return Object.entries(portalRoles)
      .filter(([, reqRoles]) => hasRole(...reqRoles))
      .map(([portal]) => portal);
  }, [userRoles]);

  return {
    isAuthenticated,
    isLoading,
    hasRole,
    hasPortalAccess,
    availablePortals,
  };
}
