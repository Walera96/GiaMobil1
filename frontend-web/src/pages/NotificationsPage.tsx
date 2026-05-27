import React from 'react';
import { useNotifications, useMarkAsRead, useMarkAllAsRead } from '../hooks/useNotifications';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Bell, CheckCircle2, Clock, Info, AlertTriangle, AlertCircle } from 'lucide-react';

const typeIcons: Record<string, React.ReactNode> = {
  INFO: <Info size={18} className="text-blue-600" />,
  SUCCESS: <CheckCircle2 size={18} className="text-green-600" />,
  WARNING: <AlertTriangle size={18} className="text-amber-600" />,
  ERROR: <AlertCircle size={18} className="text-red-600" />,
};

const typeBg: Record<string, string> = {
  INFO: 'bg-blue-50',
  SUCCESS: 'bg-green-50',
  WARNING: 'bg-amber-50',
  ERROR: 'bg-red-50',
};

export const NotificationsPage: React.FC = () => {
  const { data: notifications, isLoading } = useNotifications();
  const markAsRead = useMarkAsRead();
  const markAllAsRead = useMarkAllAsRead();

  const unreadCount = notifications?.filter((n) => !n.read).length || 0;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-[var(--color-primary)] text-white">
            <Bell size={22} />
          </div>
          <h2 className="text-2xl font-bold text-[var(--color-text)]">Уведомления</h2>
        </div>
        {unreadCount > 0 && (
          <Button size="sm" variant="secondary" onClick={() => markAllAsRead.mutate()} disabled={markAllAsRead.isPending}>
            <CheckCircle2 size={14} className="mr-1.5" />
            Прочитать все
          </Button>
        )}
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center gap-2 py-12 text-gray-500">
          <div className="h-5 w-5 animate-spin rounded-full border-2 border-[var(--color-primary)] border-t-transparent" />
          Загрузка...
        </div>
      ) : notifications && notifications.length > 0 ? (
        <div className="space-y-2">
          {notifications.map((n) => (
            <Card
              key={n.id}
              className={[
                'flex items-start gap-3 transition-colors',
                !n.read ? 'border-l-4 border-l-blue-500' : 'opacity-80',
              ].join(' ')}
            >
              <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg ${typeBg[n.type] || 'bg-slate-100'}`}>
                {typeIcons[n.type] || <Info size={18} className="text-gray-500" />}
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <h3 className="font-medium text-[var(--color-text)]">{n.title}</h3>
                  {!n.read && <Badge variant="info">Новое</Badge>}
                </div>
                <p className="mt-0.5 text-sm text-[var(--color-text-muted)]">{n.message}</p>
                <div className="mt-1 flex items-center gap-2 text-xs text-gray-400">
                  <Clock size={12} />
                  {new Date(n.createdAt).toLocaleString('ru-RU')}
                </div>
              </div>
              {!n.read && (
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => markAsRead.mutate(n.id)}
                  disabled={markAsRead.isPending}
                >
                  <CheckCircle2 size={14} className="mr-1" />
                  Прочитать
                </Button>
              )}
            </Card>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-2 rounded-lg border border-dashed border-[var(--color-border)] py-16 text-sm text-gray-500">
          <Bell size={32} className="text-gray-300" />
          <p>Уведомлений пока нет</p>
        </div>
      )}
    </div>
  );
};
