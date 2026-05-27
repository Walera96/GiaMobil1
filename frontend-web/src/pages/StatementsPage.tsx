import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStatements, useDeleteStatement, useChangeStatementStatus } from '../hooks/useStatements';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Search, Plus, FileText, Trash2, CheckCircle, Printer } from 'lucide-react';

const statusMap: Record<string, { label: string; variant: 'default' | 'info' | 'success' | 'warning' }> = {
  DRAFT: { label: 'Черновик', variant: 'info' },
  PENDING: { label: 'На проверке', variant: 'warning' },
  APPROVED: { label: 'Утверждено', variant: 'success' },
  PRINTED: { label: 'В печати', variant: 'default' },
};

export const StatementsPage: React.FC = () => {
  const navigate = useNavigate();
  const { data: statements, isLoading } = useStatements();
  const deleteMutation = useDeleteStatement();
  const statusMutation = useChangeStatementStatus();

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | null>(null);

  const filtered = statements?.filter((s) => {
    const matchesSearch = s.groupName?.toLowerCase().includes(search.toLowerCase()) ||
      s.disciplineName?.toLowerCase().includes(search.toLowerCase()) ||
      s.statementNumber?.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter ? s.status === statusFilter : true;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Ведомости</h1>
        <Button onClick={() => navigate('/statements/new')} className="flex items-center gap-2">
          <Plus size={18} /> Создать ведомость
        </Button>
      </div>

      <div className="flex gap-4">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <Input placeholder="Поиск..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-10" />
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => setStatusFilter(null)}
            className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${statusFilter === null ? 'bg-[var(--color-primary)] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}
          >
            Все
          </button>
          {Object.entries(statusMap).map(([key, { label }]) => (
            <button
              key={key}
              onClick={() => setStatusFilter(key)}
              className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${statusFilter === key ? 'bg-[var(--color-primary)] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка...</div>
      ) : (
        <div className="space-y-3">
          {filtered?.map((s) => {
            const status = statusMap[s.status] || { label: s.status, variant: 'secondary' };
            return (
              <Card key={s.id} className="cursor-pointer hover:shadow-md transition-shadow p-4" onClick={() => navigate(`/statements/${s.id}`)}>
                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <FileText size={18} className="text-[var(--color-primary)]" />
                        <span className="font-medium">Ведомость №{s.statementNumber || '—'}</span>
                        <Badge variant={status.variant}>{status.label}</Badge>
                      </div>
                      <div className="text-sm text-gray-600">
                        Группа: <span className="font-medium">{s.groupName}</span>
                        {s.disciplineName && <> · Дисциплина: <span className="font-medium">{s.disciplineName}</span></>}
                        {s.teacherName && <> · Преподаватель: {s.teacherName}</>}
                      </div>
                      <div className="text-xs text-gray-500">
                        {s.academicYear} · {s.semester}
                      </div>
                    </div>
                    <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                      {s.status === 'DRAFT' && (
                        <Button size="sm" variant="ghost" onClick={() => statusMutation.mutate({ id: s.id, status: 'PENDING' })}>
                          <CheckCircle size={14} className="mr-1" /> На проверку
                        </Button>
                      )}
                      {s.status === 'APPROVED' && (
                        <Button size="sm" variant="ghost" onClick={() => statusMutation.mutate({ id: s.id, status: 'PRINTED' })}>
                          <Printer size={14} className="mr-1" /> Печать
                        </Button>
                      )}
                      <button onClick={() => deleteMutation.mutate(s.id)} className="p-2 hover:bg-red-50 text-red-500 rounded">
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </div>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
};
