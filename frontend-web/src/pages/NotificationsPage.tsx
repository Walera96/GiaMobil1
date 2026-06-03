import React from 'react';
import { useNotifications } from '../hooks/useNotifications';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import {
  Bell,
  CheckCircle2,
  Clock,
  Info,
  AlertTriangle,
  AlertCircle,
  Trash2,
  Wifi,
  WifiOff,
} from 'lucide-react';

const typeConfig: Record<string, { icon: React.ReactNode; bg: string; label: string }> = {
  info: { icon: <Info size={18} className="text-blue-600" />, bg: 'bg-blue-50', label: 'Инфо' },
  success: { icon: <CheckCircle2 size={18} className="text-green-600" />, bg: 'bg-green-50', label: 'Успех' },
  warning: { icon: <AlertTriangle size={18} className="text-amber-600" />, bg: 'bg-amber-50', label: 'Внимание' },
  error: { icon: <AlertCircle size={18} className="text-red-600" />, bg: 'bg-red-50', label: 'Ошибка' },
  assignment: { icon: <Bell size={18} className="text-purple-600" />, bg: 'bg-purple-50', label: 'Задание' },
  submission: { icon: <CheckCircle2 size={18} className="text-teal-600" />, bg: 'bg-teal-50', label: 'Сдача' },
};

export const NotificationsPage: React.FC = () => {
  const {
    notifications,
    unreadCount,
    isConnected,
    markAsRead,
    markAllAsRead,
    clearNotifications,
  } = useNotifications();

  const unreadNotifications = notifications.filter((n) => !n.read);
  const readNotifications = notifications.filter((n) => n.read);

  const renderNotification = (n: (typeof notifications)[0]) => {
    const cfg = typeConfig[n.type] || typeConfig.info;
    return (
      <Card
        key={n.id}
        className={[
          'flex items-start gap-3 transition-colors',
          !n.read ? 'border-l-4 border-l-blue-500' : 'opacity-80',
        ].join(' ')}
      >
        <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg ${cfg.bg}`}>
          {cfg.icon}
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2 flex-wrap">
            <h3 className="font-medium text-[var(--color-text)]">{n.title}</h3>
            {!n.read && <Badge variant="info">Новое</Badge>}
            <Badge variant="secondary" className="text-[10px]">
              {cfg.label}
            </Badge>
          </div>
          <p className="mt-0.5 text-sm text-[var(--color-text-muted)]">{n.message}</p>
          {n.link && (
            <a
              href={n.link}
              className="mt-1 inline-block text-xs text-blue-600 hover:underline"
            >
              Перейти →
            </a>
          )}
          <div className="mt-1 flex items-center gap-2 text-xs text-gray-400">
            <Clock size={12} />
            {new Date(n.createdAt).toLocaleString('ru-RU')}
          </div>
        </div>
        <div className="flex shrink-0 flex-col gap-1">
          {!n.read && (
            <Button size="sm" variant="ghost" onClick={() => markAsRead(n.id)}>
              <CheckCircle2 size={14} />
            </Button>
          )}
        </div>
      </Card>
    );
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-[var(--color-primary)] text-white">
            <Bell size={22} />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-[var(--color-text)]">Уведомления</h2>
            <p className="text-sm text-[var(--color-text-muted)]">
              {unreadCount > 0
                ? `${unreadCount} непрочитанных`
                : 'Все уведомления прочитаны'}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          {isConnected ? (
            <Badge variant="success" className="flex items-center gap-1">
              <Wifi size={12} />
              Онлайн
            </Badge>
          ) : (
            <Badge variant="warning" className="flex items-center gap-1">
              <WifiOff size={12} />
              Оффлайн
            </Badge>
          )}
          {unreadCount > 0 && (
            <Button size="sm" variant="secondary" onClick={markAllAsRead}>
              <CheckCircle2 size={14} className="mr-1.5" />
              Прочитать все
            </Button>
          )}
          {notifications.length > 0 && (
            <Button size="sm" variant="ghost" onClick={clearNotifications}>
              <Trash2 size={14} className="mr-1.5" />
              Очистить
            </Button>
          )}
        </div>
      </div>

      {notifications.length > 0 ? (
        <div className="space-y-6">
          {unreadNotifications.length > 0 && (
            <div className="space-y-2">
              <h3 className="text-sm font-semibold text-[var(--color-text)] uppercase tracking-wide">
                Новые
              </h3>
              {unreadNotifications.map(renderNotification)}
            </div>
          )}
          {readNotifications.length > 0 && (
            <div className="space-y-2">
              <h3 className="text-sm font-semibold text-[var(--color-text)] uppercase tracking-wide">
                Прочитанные
              </h3>
              {readNotifications.map(renderNotification)}
            </div>
          )}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-2 rounded-lg border border-dashed border-[var(--color-border)] py-16 text-sm text-gray-500">
          <Bell size={32} className="text-gray-300" />
          <p>Уведомлений пока нет</p>
          <p className="text-xs text-gray-400">
            Новые уведомления будут появляться здесь в реальном времени
          </p>
        </div>
      )}
    </div>
  );
};
