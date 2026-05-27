import React, { useState } from 'react';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Input } from '../components/ui/Input';
import { useAdmissions } from '../hooks/useAdmissions';

export const AdmissionsPage: React.FC = () => {
  const [filter, setFilter] = useState('');
  const { data: admissions, isLoading } = useAdmissions();

  const filtered = admissions?.filter((a) =>
    (a.studentFullName?.toLowerCase() || '').includes(filter.toLowerCase()) ||
    (a.groupName?.toLowerCase() || '').includes(filter.toLowerCase())
  ) || [];

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold text-[var(--color-text)]">Допуск к ГИА</h2>
      <Input placeholder="Поиск по ФИО или группе" value={filter} onChange={(e) => setFilter(e.target.value)} />
      <Card>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-[var(--color-border)] bg-slate-50">
              <th className="px-4 py-3 text-left">ФИО</th>
              <th className="px-4 py-3 text-left">Группа</th>
              <th className="px-4 py-3 text-left">БРС</th>
              <th className="px-4 py-3 text-left">Задолженности</th>
              <th className="px-4 py-3 text-left">Статус</th>
            </tr>
          </thead>
          <tbody>
            {isLoading && (
              <tr><td colSpan={5} className="px-4 py-6 text-center text-[var(--color-text-muted)]">Загрузка...</td></tr>
            )}
            {!isLoading && filtered.length === 0 && (
              <tr><td colSpan={5} className="px-4 py-6 text-center text-[var(--color-text-muted)]">Нет данных</td></tr>
            )}
            {filtered.map((a) => (
              <tr key={a.id} className="border-b border-[var(--color-border)] hover:bg-slate-50">
                <td className="px-4 py-3 font-medium">{a.studentFullName || '—'}</td>
                <td className="px-4 py-3">{a.groupName || '—'}</td>
                <td className="px-4 py-3">{a.brsScore ?? '—'}</td>
                <td className="px-4 py-3">{a.hasDebt ? 'Есть' : 'Нет'}</td>
                <td className="px-4 py-3">
                  <Badge variant={a.isAdmitted ? 'success' : 'danger'}>
                    {a.isAdmitted ? 'Допущен' : 'Не допущен'}
                  </Badge>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
};
