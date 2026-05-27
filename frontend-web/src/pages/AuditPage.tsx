import React, { useState } from 'react';
import { auditApi } from '../api/audit';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Input } from '../components/ui/Input';
import type { AuditLog } from '../types';
import { ShieldCheck, Search, Clock, ArrowRightLeft, Plus, Trash2, Hash, Globe } from 'lucide-react';

const actionConfig: Record<string, { label: string; variant: 'success' | 'info' | 'danger'; icon: React.ReactNode }> = {
  INSERT: { label: 'Создание', variant: 'success', icon: <Plus size={12} /> },
  UPDATE: { label: 'Изменение', variant: 'info', icon: <ArrowRightLeft size={12} /> },
  DELETE: { label: 'Удаление', variant: 'danger', icon: <Trash2 size={12} /> },
};

export const AuditPage: React.FC = () => {
  const [table, setTable] = useState('');
  const [recordId, setRecordId] = useState('');
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async () => {
    if (!table.trim() || !recordId.trim()) {
      alert('Укажите таблицу и ID записи');
      return;
    }
    setIsLoading(true);
    try {
      const results = await auditApi.getLogs(table.trim(), recordId.trim());
      setLogs(results);
    } catch {
      alert('Ошибка загрузки журнала аудита');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-[var(--color-primary)] text-white">
          <ShieldCheck size={22} />
        </div>
        <h2 className="text-2xl font-bold text-[var(--color-text)]">Журнал аудита</h2>
      </div>

      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
          <Input
            placeholder="Имя таблицы (например, vote)"
            value={table}
            onChange={(e) => setTable(e.target.value)}
          />
          <Input
            placeholder="ID записи (UUID)"
            value={recordId}
            onChange={(e) => setRecordId(e.target.value)}
          />
          <Button onClick={handleSearch} disabled={isLoading}>
            <Search size={16} className="mr-1.5" />
            {isLoading ? 'Загрузка...' : 'Показать'}
          </Button>
        </div>
      </Card>

      {logs.length > 0 && (
        <Card className="overflow-hidden p-0">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                  <th className="px-4 py-3 text-left font-semibold">Действие</th>
                  <th className="px-4 py-3 text-left font-semibold">Таблица</th>
                  <th className="px-4 py-3 text-left font-semibold">Старое значение</th>
                  <th className="px-4 py-3 text-left font-semibold">Новое значение</th>
                  <th className="px-4 py-3 text-left font-semibold">IP</th>
                  <th className="px-4 py-3 text-left font-semibold">Время</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => {
                  const cfg = actionConfig[log.action] || { label: log.action, variant: 'default' as const, icon: null };
                  return (
                    <tr key={log.id} className="border-t border-[var(--color-border)] transition-colors hover:bg-blue-50/40">
                      <td className="px-4 py-3">
                        <Badge variant={cfg.variant}>
                          <span className="mr-1">{cfg.icon}</span>
                          {cfg.label}
                        </Badge>
                      </td>
                      <td className="px-4 py-3">
                        <span className="flex items-center gap-1">
                          <Hash size={14} className="text-gray-400" />
                          {log.tableName}
                        </span>
                      </td>
                      <td className="px-4 py-3 max-w-xs truncate text-gray-500" title={log.oldValue}>
                        {log.oldValue || '—'}
                      </td>
                      <td className="px-4 py-3 max-w-xs truncate text-[var(--color-text)]" title={log.newValue}>
                        {log.newValue || '—'}
                      </td>
                      <td className="px-4 py-3">
                        <span className="flex items-center gap-1 text-gray-500">
                          <Globe size={14} />
                          {log.ipAddress || '—'}
                        </span>
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap">
                        <span className="flex items-center gap-1 text-gray-500">
                          <Clock size={14} />
                          {new Date(log.createdAt).toLocaleString('ru-RU')}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {logs.length === 0 && !isLoading && (
        <div className="flex items-center justify-center gap-2 rounded-lg border border-dashed border-[var(--color-border)] py-12 text-sm text-gray-500">
          <Search size={16} />
          Введите параметры поиска для просмотра журнала аудита
        </div>
      )}
    </div>
  );
};
