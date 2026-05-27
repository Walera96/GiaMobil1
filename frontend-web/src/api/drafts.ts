import { api } from './client';
import type { DraftDocument } from '../types';

export interface CreateDraftRequest {
  protocolId: string;
  documentType: 'INDIVIDUAL' | 'FINAL' | 'SCORESHEET';
  createdBy: string;
}

export const draftsApi = {
  getById: (id: string) => api.get<DraftDocument>(`/drafts/${id}`).then((r) => r.data),

  getByProtocol: (protocolId: string) =>
    api.get<DraftDocument[]>(`/drafts/protocol/${protocolId}`).then((r) => r.data),

  create: (data: CreateDraftRequest) =>
    api.post<DraftDocument>('/drafts', data).then((r) => r.data),

  update: (id: string, content: string) =>
    api.put<DraftDocument>(`/drafts/${id}`, { content }).then((r) => r.data),

  approve: (id: string) =>
    api.post<DraftDocument>(`/drafts/${id}/approve`).then((r) => r.data),

  preview: (id: string) =>
    api.get<string>(`/drafts/${id}/preview`, { responseType: 'text' }).then((r) => r.data),

  downloadDocx: (id: string) =>
    api.get<Blob>(`/drafts/${id}/docx`, { responseType: 'blob' }).then((r) => r.data),
};
