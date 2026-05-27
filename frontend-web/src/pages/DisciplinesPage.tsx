import React, { useState } from 'react';
import { useDisciplines, useCreateDiscipline, useUpdateDiscipline, useDeleteDiscipline } from '../hooks/useDisciplines';
import { useDirections } from '../hooks/useDirections';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Search, BookOpen, Plus, Pencil, Trash2 } from 'lucide-react';
import { Modal } from '../components/ui/Modal';

export const DisciplinesPage: React.FC = () => {
  const { data: disciplines, isLoading } = useDisciplines();
  const { data: directions } = useDirections();
  const createMutation = useCreateDiscipline();
  const updateMutation = useUpdateDiscipline();
  const deleteMutation = useDeleteDiscipline();

  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState({
    code: '',
    name: '',
    hours: '',
    ectsCredits: '',
    course: '',
    semester: '',
    controlType: 'EXAM',
    directionId: '',
  });

  const filtered = disciplines?.filter((d) =>
    d.name.toLowerCase().includes(search.toLowerCase()) ||
    d.code?.toLowerCase().includes(search.toLowerCase())
  );

  const openCreate = () => {
    setEditingId(null);
    setForm({ code: '', name: '', hours: '', ectsCredits: '', course: '', semester: '', controlType: 'EXAM', directionId: '' });
    setIsModalOpen(true);
  };

  const openEdit = (discipline: typeof disciplines extends undefined ? never : NonNullable<typeof disciplines>[number]) => {
    setEditingId(discipline.id);
    setForm({
      code: discipline.code || '',
      name: discipline.name,
      hours: discipline.hours?.toString() || '',
      ectsCredits: discipline.ectsCredits?.toString() || '',
      course: discipline.course?.toString() || '',
      semester: discipline.semester || '',
      controlType: discipline.controlType || 'EXAM',
      directionId: discipline.directionId || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const data = {
      code: form.code,
      name: form.name,
      hours: form.hours ? parseInt(form.hours) : undefined,
      ectsCredits: form.ectsCredits ? parseInt(form.ectsCredits) : undefined,
      course: form.course ? parseInt(form.course) : undefined,
      semester: form.semester || undefined,
      controlType: form.controlType,
      directionId: form.directionId || undefined,
    };
    if (editingId) {
      updateMutation.mutate({ id: editingId, data });
    } else {
      createMutation.mutate(data);
    }
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Дисциплины</h1>
        <Button onClick={openCreate} className="flex items-center gap-2">
          <Plus size={18} /> Добавить
        </Button>
      </div>

      <div className="relative max-w-sm">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
        <Input placeholder="Поиск дисциплины..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-10" />
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered?.map((d) => (
            <Card key={d.id}>
              <div className="p-4">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-base font-semibold">{d.name}</h3>
                  <div className="flex gap-1">
                    <button onClick={() => openEdit(d)} className="p-1 hover:bg-gray-100 rounded">
                      <Pencil size={14} />
                    </button>
                    <button onClick={() => deleteMutation.mutate(d.id)} className="p-1 hover:bg-red-50 text-red-500 rounded">
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <BookOpen size={16} />
                    <span>{d.code || '—'}</span>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {d.course && <Badge variant="info">{d.course} курс</Badge>}
                    {d.semester && <Badge variant="info">{d.semester}</Badge>}
                    {d.controlType && <Badge variant="default">{d.controlType === 'EXAM' ? 'Экзамен' : 'Зачет'}</Badge>}
                    {d.ectsCredits && <Badge variant="default">{d.ectsCredits} ECTS</Badge>}
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingId ? 'Редактировать дисциплину' : 'Новая дисциплина'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input placeholder="Код" value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} />
          <Input placeholder="Название *" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input placeholder="Часы" type="number" value={form.hours} onChange={(e) => setForm({ ...form, hours: e.target.value })} />
            <Input placeholder="ECTS" type="number" value={form.ectsCredits} onChange={(e) => setForm({ ...form, ectsCredits: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input placeholder="Курс" type="number" value={form.course} onChange={(e) => setForm({ ...form, course: e.target.value })} />
            <Input placeholder="Семестр" value={form.semester} onChange={(e) => setForm({ ...form, semester: e.target.value })} />
          </div>
          <select
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
            value={form.controlType}
            onChange={(e) => setForm({ ...form, controlType: e.target.value })}
          >
            <option value="EXAM">Экзамен</option>
            <option value="TEST">Зачет</option>
          </select>
          <select
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
            value={form.directionId}
            onChange={(e) => setForm({ ...form, directionId: e.target.value })}
          >
            <option value="">— Направление —</option>
            {directions?.map((dir) => (
              <option key={dir.id} value={dir.id}>{dir.code} {dir.name}</option>
            ))}
          </select>
          <div className="flex justify-end gap-2">
            <Button type="button" variant="secondary" onClick={() => setIsModalOpen(false)}>Отмена</Button>
            <Button type="submit">Сохранить</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
