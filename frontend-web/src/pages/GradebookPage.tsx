import React, { useState, useMemo } from 'react';
import { useGrades, useGradesByGroup, useCreateGrade, useUpdateGrade, useDeleteGrade } from '../hooks/useGrades';
import { useDisciplines } from '../hooks/useDisciplines';
import { useStudents } from '../hooks/useStudents';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Search, Plus, Pencil, Trash2, User, ArrowUpDown } from 'lucide-react';
import { Modal } from '../components/ui/Modal';
import type { GradeDto, CreateGradeRequest } from '../api/grades';
import { useSearchParams, useNavigate } from 'react-router-dom';

const getGradeColor = (percent: number) => {
  if (percent >= 85) return 'bg-emerald-100 text-emerald-800';
  if (percent >= 70) return 'bg-blue-100 text-blue-800';
  if (percent >= 60) return 'bg-amber-100 text-amber-800';
  return 'bg-red-100 text-red-800';
};

type SortField = 'studentName' | 'subjectName' | 'totalScore' | 'ectsGrade' | 'fivePointGrade';
type SortDir = 'asc' | 'desc';

export const GradebookPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get('groupId');
  const { data: allGrades, isLoading: allLoading } = useGrades();
  const { data: groupGrades, isLoading: groupLoading } = useGradesByGroup(groupId);
  const grades = groupId ? groupGrades : allGrades;
  const isLoading = groupId ? groupLoading : allLoading;
  const navigate = useNavigate();
  const { data: disciplines } = useDisciplines();
  const { data: students } = useStudents(groupId || undefined);
  const createMutation = useCreateGrade();
  const updateMutation = useUpdateGrade();
  const deleteMutation = useDeleteGrade();

  const [search, setSearch] = useState('');
  const [sortField, setSortField] = useState<SortField>('studentName');
  const [sortDir, setSortDir] = useState<SortDir>('asc');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<Partial<CreateGradeRequest>>({
    studentId: '',
    disciplineId: '',
    subjectName: '',
    currentControl: 0,
    attendance: 0,
    activity: 0,
    examScore: 0,
    semester: '',
  });

  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  };

  const filtered = useMemo(() => {
    let data = grades?.filter((g) =>
      g.studentName?.toLowerCase().includes(search.toLowerCase()) ||
      g.subjectName?.toLowerCase().includes(search.toLowerCase())
    ) || [];
    data = [...data].sort((a, b) => {
      const dir = sortDir === 'asc' ? 1 : -1;
      switch (sortField) {
        case 'studentName': return dir * (a.studentName || '').localeCompare(b.studentName || '');
        case 'subjectName': return dir * (a.subjectName || '').localeCompare(b.subjectName || '');
        case 'totalScore': return dir * ((a.totalScore || 0) - (b.totalScore || 0));
        case 'ectsGrade': return dir * (a.ectsGrade || '').localeCompare(b.ectsGrade || '');
        case 'fivePointGrade': return dir * ((a.fivePointGrade || 0) - (b.fivePointGrade || 0));
        default: return 0;
      }
    });
    return data;
  }, [grades, search, sortField, sortDir]);

  const openCreate = () => {
    setEditingId(null);
    setForm({ studentId: '', disciplineId: '', subjectName: '', currentControl: 0, attendance: 0, activity: 0, examScore: 0, semester: '' });
    setIsModalOpen(true);
  };

  const openEdit = (grade: GradeDto) => {
    setEditingId(grade.id);
    setForm({
      studentId: grade.studentId,
      disciplineId: grade.disciplineId || '',
      subjectName: grade.subjectName,
      currentControl: grade.currentControl || 0,
      attendance: grade.attendance || 0,
      activity: grade.activity || 0,
      examScore: grade.examScore || 0,
      semester: grade.semester || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const data = {
      ...form,
      subjectName: form.subjectName || '',
      studentId: form.studentId || '',
    } as CreateGradeRequest;
    if (editingId) {
      updateMutation.mutate({ id: editingId, data });
    } else {
      createMutation.mutate(data);
    }
    setIsModalOpen(false);
  };

  const getPercent = (total: number | undefined) => {
    if (!total) return 0;
    return Math.round((total / 130) * 100);
  };

  const SortHeader = ({ field, children }: { field: SortField; children: React.ReactNode }) => (
    <th
      className="px-4 py-3 text-left font-medium text-gray-700 cursor-pointer hover:bg-gray-100 select-none"
      onClick={() => handleSort(field)}
    >
      <div className="flex items-center gap-1">
        {children}
        <ArrowUpDown size={14} className={sortField === field ? 'text-[var(--color-primary)]' : 'text-gray-400'} />
      </div>
    </th>
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Журнал оценок</h1>
        <Button onClick={openCreate} className="flex items-center gap-2">
          <Plus size={18} /> Добавить оценку
        </Button>
      </div>

      <div className="relative max-w-sm">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
        <Input placeholder="Поиск по студенту или предмету..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-10" />
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка...</div>
      ) : (
        <Card className="p-0">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <SortHeader field="studentName">Студент</SortHeader>
                    <SortHeader field="subjectName">Предмет</SortHeader>
                    <th className="px-4 py-3 text-center font-medium text-gray-700">Текущий</th>
                    <th className="px-4 py-3 text-center font-medium text-gray-700">Посещ.</th>
                    <th className="px-4 py-3 text-center font-medium text-gray-700">Активн.</th>
                    <th className="px-4 py-3 text-center font-medium text-gray-700">Экзамен</th>
                    <SortHeader field="totalScore">Итого</SortHeader>
                    <SortHeader field="ectsGrade">ECTS</SortHeader>
                    <SortHeader field="fivePointGrade">5-балл</SortHeader>
                    <th className="px-4 py-3 text-right font-medium text-gray-700">Действия</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {filtered?.map((g) => {
                    const percent = getPercent(g.totalScore);
                    return (
                      <tr key={g.id} className="hover:bg-gray-50">
                        <td className="px-4 py-3 font-medium">
                          <button
                            className="flex items-center gap-1 text-[var(--color-primary)] hover:underline"
                            onClick={() => navigate(`/students/${g.studentId}`)}
                          >
                            <User size={14} />
                            {g.studentName}
                          </button>
                        </td>
                        <td className="px-4 py-3 text-gray-600">{g.subjectName}</td>
                        <td className="px-4 py-3 text-center">{g.currentControl ?? '—'}</td>
                        <td className="px-4 py-3 text-center">{g.attendance ?? '—'}</td>
                        <td className="px-4 py-3 text-center">{g.activity ?? '—'}</td>
                        <td className="px-4 py-3 text-center">{g.examScore ?? '—'}</td>
                        <td className="px-4 py-3 text-center font-bold">{g.totalScore ?? '—'}</td>
                        <td className="px-4 py-3 text-center">
                          {g.ectsGrade && <Badge className={getGradeColor(percent)}>{g.ectsGrade}</Badge>}
                        </td>
                        <td className="px-4 py-3 text-center">
                          {g.fivePointGrade && <Badge className={getGradeColor(percent)}>{g.fivePointGrade}</Badge>}
                        </td>
                        <td className="px-4 py-3 text-right">
                          <div className="flex justify-end gap-1">
                            <button onClick={() => openEdit(g)} className="p-1 hover:bg-gray-100 rounded" title="Редактировать">
                              <Pencil size={14} />
                            </button>
                            <button onClick={() => deleteMutation.mutate(g.id)} className="p-1 hover:bg-red-50 text-red-500 rounded" title="Удалить">
                              <Trash2 size={14} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
        </Card>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingId ? 'Редактировать оценку' : 'Новая оценка'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Студент <span className="text-red-500">*</span></label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.studentId}
              onChange={(e) => setForm({ ...form, studentId: e.target.value })}
              required
            >
              <option value="">— Выберите студента —</option>
              {students?.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.lastName} {s.firstName} {s.middleName || ''} ({s.group?.name || 'без группы'})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Дисциплина</label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.disciplineId}
              onChange={(e) => {
                const disc = disciplines?.find((d) => d.id === e.target.value);
                setForm({ ...form, disciplineId: e.target.value, subjectName: disc?.name || '' });
              }}
            >
              <option value="">— Выберите дисциплину —</option>
              {disciplines?.map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Название предмета</label>
            <Input placeholder="Введите название предмета вручную (если дисциплина не выбрана)" value={form.subjectName} onChange={(e) => setForm({ ...form, subjectName: e.target.value })} />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Текущий контроль</label>
              <Input placeholder="0–70 баллов" type="number" min={0} max={70} value={form.currentControl} onChange={(e) => setForm({ ...form, currentControl: parseInt(e.target.value) || 0 })} />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Посещаемость</label>
              <Input placeholder="0–10 баллов" type="number" min={0} max={10} value={form.attendance} onChange={(e) => setForm({ ...form, attendance: parseInt(e.target.value) || 0 })} />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Активность</label>
              <Input placeholder="0–20 баллов" type="number" min={0} max={20} value={form.activity} onChange={(e) => setForm({ ...form, activity: parseInt(e.target.value) || 0 })} />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Экзамен</label>
              <Input placeholder="0–30 баллов" type="number" min={0} max={30} value={form.examScore} onChange={(e) => setForm({ ...form, examScore: parseInt(e.target.value) || 0 })} />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Семестр</label>
            <Input placeholder="Например: 2025/2026-1" value={form.semester} onChange={(e) => setForm({ ...form, semester: e.target.value })} />
          </div>

          <div className="flex justify-end gap-2">
            <Button type="button" variant="secondary" onClick={() => setIsModalOpen(false)}>Отмена</Button>
            <Button type="submit">Сохранить</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
