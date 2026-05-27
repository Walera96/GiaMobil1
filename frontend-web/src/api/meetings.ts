import { api } from './client';
import type { Meeting, AgendaItem } from '../types';

export interface CreateMeetingRequest {
  meetingDate: string;
  startTime?: string;
  endTime?: string;
  location?: string;
  gekId: string;
  quorumRequired?: number;
}

export const meetingsApi = {
  getAll: () => api.get<Meeting[]>('/meetings').then((r) => r.data),
  getById: (id: string) => api.get<Meeting>(`/meetings/${id}`).then((r) => r.data),
  create: (data: CreateMeetingRequest) => api.post<Meeting>('/meetings', data).then((r) => r.data),
  activate: (id: string) => api.post<void>(`/meetings/${id}/activate`).then((r) => r.data),
  close: (id: string) => api.post<void>(`/meetings/${id}/close`).then((r) => r.data),
  cancel: (id: string) => api.post<void>(`/meetings/${id}/cancel`).then((r) => r.data),
  getAgenda: (meetingId: string) =>
    api.get<AgendaItem[]>(`/meetings/${meetingId}/agenda-items`).then((r) => r.data),
};
