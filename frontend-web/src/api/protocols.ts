import { api } from './client';
import type { Protocol, ProtocolRecord, ScoreSheet } from '../types';

function extractFilename(contentDisposition: string | undefined): string | null {
  if (!contentDisposition) return null;
  // Try filename*=UTF-8''...
  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
  if (utf8Match) return decodeURIComponent(utf8Match[1]);
  // Try filename="..."
  const quotedMatch = contentDisposition.match(/filename="([^"]+)"/i);
  if (quotedMatch) return quotedMatch[1];
  // Try filename=...
  const simpleMatch = contentDisposition.match(/filename=([^;]+)/i);
  if (simpleMatch) return simpleMatch[1].trim();
  return null;
}

export const protocolsApi = {
  getByMeetingId: (meetingId: string) =>
    api.get<Protocol>(`/protocols/meeting/${meetingId}`).then((r) => r.data),
  getRecords: (protocolId: string) =>
    api.get<ProtocolRecord[]>(`/protocols/${protocolId}/records`).then((r) => r.data),
  sign: (protocolId: string) =>
    api.post<Protocol>(`/protocols/${protocolId}/sign`).then((r) => r.data),
  approve: (protocolId: string) =>
    api.post<Protocol>(`/protocols/${protocolId}/approve`).then((r) => r.data),
  generateRecords: (meetingId: string) =>
    api.post<void>(`/protocols/meeting/${meetingId}/generate-records`).then((r) => r.data),
  getScoreSheet: (meetingId: string) =>
    api.get<ScoreSheet>(`/protocols/meeting/${meetingId}/score-sheet`).then((r) => r.data),
  search: (params: { studentId?: string; groupId?: string; directionId?: string; studentName?: string }) =>
    api.get<Protocol[]>('/protocols/search', { params }).then((r) => r.data),

  downloadDocx: (protocolId: string) =>
    api.get<Blob>(`/protocols/${protocolId}/docx`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || `final_protocol_${protocolId}.docx`,
    })),

  downloadIndividualDocx: (recordId: string) =>
    api.get<Blob>(`/protocols/record/${recordId}/docx/individual`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || `individual_protocol_${recordId}.docx`,
    })),

  downloadScoreSheetDocx: (meetingId: string) =>
    api.get<Blob>(`/protocols/meeting/${meetingId}/docx/scoresheet`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || `scoresheet_${meetingId}.docx`,
    })),

  downloadFinalPdf: (protocolId: string) =>
    api.get<Blob>(`/protocols/${protocolId}/pdf`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || `final_protocol_${protocolId}.pdf`,
    })),

  downloadScoreSheetPdf: (meetingId: string) =>
    api.get<Blob>(`/protocols/meeting/${meetingId}/pdf/scoresheet`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || `scoresheet_${meetingId}.pdf`,
    })),

  downloadIndividualPdf: (recordId: string) =>
    api.get<Blob>(`/protocols/record/${recordId}/pdf/individual`, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || `individual_protocol_${recordId}.pdf`,
    })),
};
