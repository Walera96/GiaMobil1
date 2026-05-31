import { api } from './client';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8090/api';

function extractFilename(contentDisposition: string | undefined): string | null {
  if (!contentDisposition) return null;
  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
  if (utf8Match) return decodeURIComponent(utf8Match[1]);
  const quotedMatch = contentDisposition.match(/filename="([^"]+)"/i);
  if (quotedMatch) return quotedMatch[1];
  const simpleMatch = contentDisposition.match(/filename=([^;]+)/i);
  if (simpleMatch) return simpleMatch[1].trim();
  return null;
}

export interface VedomostDto {
  documentNumber?: string;
  academicYear?: string;
  directionCode?: string;
  directionName?: string;
  directionShort?: string;
  department?: string;
  giaForm?: string;
  course?: number;
  groupName?: string;
  date?: string;
  instituteName?: string;
  directorName?: string;
  chairmanName?: string;
  chairmanDegree?: string;
  totalStudents?: number;
  countZachteno?: number;
  countNeZachteno?: number;
  countOtlichno?: number;
  countHorosho?: number;
  countUdov?: number;
  countNeud?: number;
  countAbsent?: number;
  students: VedomostStudentRecord[];
  committeeMembers: VedomostCommitteeMember[];
}

export interface VedomostStudentRecord {
  seqNumber?: number;
  fullName?: string;
  recordBookNumber?: string;
  scorePoints?: number;
  scoreClassic?: string;
}

export interface VedomostCommitteeMember {
  fullName?: string;
  degree?: string;
}

async function postBlob(path: string, dto: VedomostDto, defaultFilename: string): Promise<{ blob: Blob; filename: string }> {
  const token = localStorage.getItem('accessToken');
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(dto),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => 'Unknown error');
    console.error(`POST ${path} failed:`, res.status, text);
    throw new Error(`HTTP ${res.status}: ${text}`);
  }
  const blob = await res.blob();
  const filename = extractFilename(res.headers.get('content-disposition') || undefined) || defaultFilename;
  return { blob, filename };
}

export const vedomostApi = {
  getTemplate: () => api.get<VedomostDto>('/vedomost/template').then((r) => r.data),

  generatePdf: (dto: VedomostDto) => postBlob('/vedomost/pdf', dto, 'vedomost.pdf'),

  generateExcel: (dto: VedomostDto) => postBlob('/vedomost/excel', dto, 'vedomost.xls'),

  generateWord: (dto: VedomostDto) => postBlob('/vedomost/word', dto, 'vedomost.docx'),
};

export function downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}
