import { api } from './client';
import type { AuditLog } from '../types';

export const auditApi = {
  getLogs: (table: string, recordId: string) =>
    api.get<AuditLog[]>('/audit-logs', { params: { table, recordId } }).then((r) => r.data),
};
