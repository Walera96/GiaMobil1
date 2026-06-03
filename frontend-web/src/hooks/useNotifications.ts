import { useEffect, useRef, useState, useCallback } from 'react';
import { toast } from '../store/toastStore';
import { useAuthStore } from '../store/authStore';
import { API_BASE_URL } from '../api/axios';

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error' | 'assignment' | 'submission';
  read: boolean;
  createdAt: string;
  link?: string;
  metadata?: Record<string, unknown>;
}

const RECONNECT_DELAY = 3000;
const MAX_RECONNECT_ATTEMPTS = 5;

export function useNotifications() {
  const { token, isAuthenticated } = useAuthStore();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimerRef = useRef<ReturnType<typeof setTimeout>>();
  const reconnectAttempts = useRef(0);

  const markAsRead = useCallback((id: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  }, []);

  const markAllAsRead = useCallback(() => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  }, []);

  const clearNotifications = useCallback(() => {
    setNotifications([]);
  }, []);

  useEffect(() => {
    setUnreadCount(notifications.filter((n) => !n.read).length);
  }, [notifications]);

  useEffect(() => {
    if (!isAuthenticated || !token) {
      return;
    }

    const connect = () => {
      const url = `${API_BASE_URL}/sse/notifications?token=${encodeURIComponent(token)}`;
      const es = new EventSource(url);
      eventSourceRef.current = es;

      es.onopen = () => {
        reconnectAttempts.current = 0;
        setIsConnected(true);
      };

      es.addEventListener('notification', (event: MessageEvent) => {
        try {
          const data = JSON.parse(event.data) as Notification;
          setNotifications((prev) => {
            if (prev.some((n) => n.id === data.id)) return prev;
            return [data, ...prev];
          });

          // Показать toast для нового уведомления
          const toastType = data.type === 'error' ? 'error' : data.type === 'warning' ? 'warning' : data.type === 'success' ? 'success' : 'info';
          toast[toastType](`${data.title}: ${data.message}`);
        } catch {
          // ignore malformed events
        }
      });

      es.addEventListener('connected', () => {
        // подключение установлено
      });

      es.onerror = () => {
        setIsConnected(false);
        es.close();
        if (reconnectAttempts.current < MAX_RECONNECT_ATTEMPTS) {
          reconnectAttempts.current += 1;
          reconnectTimerRef.current = setTimeout(connect, RECONNECT_DELAY * reconnectAttempts.current);
        }
      };
    };

    connect();

    return () => {
      if (reconnectTimerRef.current) {
        clearTimeout(reconnectTimerRef.current);
      }
      eventSourceRef.current?.close();
      setIsConnected(false);
    };
  }, [isAuthenticated, token]);

  return {
    notifications,
    unreadCount,
    isConnected,
    markAsRead,
    markAllAsRead,
    clearNotifications,
  };
}
