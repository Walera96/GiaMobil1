import { api } from './client';
import type { Notification } from '../types';

export const notificationsApi = {
  getAll: () => api.get<Notification[]>('/notifications').then((r) => r.data),
  getUnreadCount: () =>
    api.get<{ count: number }>('/notifications/unread-count').then((r) => r.data),
  markAsRead: (id: string) => api.post<void>(`/notifications/${id}/read`).then((r) => r.data),
  markAllAsRead: () => api.post<void>('/notifications/read-all').then((r) => r.data),
};
