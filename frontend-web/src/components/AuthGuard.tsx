import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuthGuard } from '../hooks/useAuthGuard';
import { Loader2 } from 'lucide-react';

type AuthGuardProps = {
  children: React.ReactNode;
  requiredPortal?: string;
  fallback?: React.ReactNode;
};

/**
 * Компонент-обёртка для защиты роутов по ролям.
 * - Если пользователь не аутентифицирован → редирект на /login
 * - Если указан requiredPortal и нет доступа → редирект на доступный портал или /
 */
export const AuthGuard: React.FC<AuthGuardProps> = ({
  children,
  requiredPortal,
  fallback,
}) => {
  const { isAuthenticated, isLoading, hasPortalAccess, availablePortals } = useAuthGuard();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredPortal && !hasPortalAccess(requiredPortal)) {
    const redirectTo = availablePortals.length > 0 ? `/${availablePortals[0]}` : '/';
    return <Navigate to={redirectTo} replace />;
  }

  return <>{fallback || children}</>;
};
