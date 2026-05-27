import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGroups } from '../hooks/useGroups';
import { useDisciplines } from '../hooks/useDisciplines';
import { useTeachers } from '../hooks/useTeachers';
import { useCreateStatement } from '../hooks/useStatements';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { ArrowLeft, FilePlus } from 'lucide-react';

export const StatementCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const { data: groups } = useGroups();
  const { data: disciplines } = useDisciplines();
  const { data: teachers } = useTeachers();
  const createMutation = useCreateStatement();

  const [form, setForm] = useState({
    statementNumber: '',
    academicYear: '2025-2026',
    semester: '2025-2',
    groupId: '',
    disciplineId: '',
    teacherId: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.groupId) return;
    createMutation.mutate(
      {
        statementNumber: form.statementNumber,
        academicYear: form.academicYear,
        semester: form.semester,
        groupId: form.groupId,
        disciplineId: form.disciplineId || undefined,
        teacherId: form.teacherId || undefined,
      },
      {
        onSuccess: (data) => {
          navigate(`/statements/${data.id}`);
        },
      }
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button onClick={() => navigate('/statements')} className="p-2 hover:bg-gray-100 rounded-md">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Создание ведомости</h1>
      </div>

      <Card className="p-6 max-w-2xl">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Номер ведомости</label>
              <Input
                placeholder="Например: 319092"
                value={form.statementNumber}
                onChange={(e) => setForm({ ...form, statementNumber: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Учебный год</label>
              <Input
                placeholder="2025-2026"
                value={form.academicYear}
                onChange={(e) => setForm({ ...form, academicYear: e.target.value })}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Семестр *</label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.semester}
              onChange={(e) => setForm({ ...form, semester: e.target.value })}
              required
            >
              <option value="2025-1">2025-1 (осенний)</option>
              <option value="2025-2">2025-2 (весенний)</option>
              <option value="2026-1">2026-1 (осенний)</option>
              <option value="2026-2">2026-2 (весенний)</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Группа *</label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.groupId}
              onChange={(e) => setForm({ ...form, groupId: e.target.value })}
              required
            >
              <option value="">— Выберите группу —</option>
              {groups?.map((g) => (
                <option key={g.id} value={g.id}>{g.name} ({g.course} курс)</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Дисциплина</label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.disciplineId}
              onChange={(e) => setForm({ ...form, disciplineId: e.target.value })}
            >
              <option value="">— Выберите дисциплину —</option>
              {disciplines?.map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Преподаватель</label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.teacherId}
              onChange={(e) => setForm({ ...form, teacherId: e.target.value })}
            >
              <option value="">— Выберите преподавателя —</option>
              {teachers?.map((t) => (
                <option key={t.id} value={t.id}>{t.fullName}</option>
              ))}
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="secondary" onClick={() => navigate('/statements')}>
              Отмена
            </Button>
            <Button type="submit" className="flex items-center gap-2">
              <FilePlus size={16} />
              Создать ведомость
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
