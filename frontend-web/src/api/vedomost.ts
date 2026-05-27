import { api } from './client';

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

export const vedomostApi = {
  getTemplate: () => api.get<VedomostDto>('/vedomost/template').then((r) => r.data),

  generatePdf: (dto: VedomostDto) =>
    api.post<Blob>('/vedomost/pdf', dto, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || 'vedomost.pdf',
    })),

  generateExcel: (dto: VedomostDto) =>
    api.post<Blob>('/vedomost/excel', dto, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || 'vedomost.xls',
    })),

  generateWord: (dto: VedomostDto) =>
    api.post<Blob>('/vedomost/word', dto, { responseType: 'blob' }).then((r) => ({
      blob: r.data,
      filename: extractFilename(r.headers['content-disposition']) || 'vedomost.docx',
    })),
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
