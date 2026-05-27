import React, { useState } from 'react';
import { useTeachers, useCreateTeacher, useUpdateTeacher, useDeleteTeacher } from '../hooks/useTeachers';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';

import { Button } from '../components/ui/Button';
import { Search, Plus, Pencil, Trash2, Mail, GraduationCap } from 'lucide-react';
import { Modal } from '../components/ui/Modal';
import type { CreateTeacherRequest } from '../api/teachers';

export const TeachersPage: React.FC = () => {
  const { data: teachers, isLoading } = useTeachers();
  const createMutation = useCreateTeacher();
  const updateMutation = useUpdateTeacher();
  const deleteMutation = useDeleteTeacher();

  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<CreateTeacherRequest>({
    lastName: '',
    firstName: '',
    middleName: '',
    department: '',
    position: '',
    degree: '',
    email: '',
  });

  const filtered = teachers?.filter((t) =>
    t.fullName?.toLowerCase().includes(search.toLowerCase()) ||
    t.department?.toLowerCase().includes(search.toLowerCase())
  );

  const openCreate = () => {
    setEditingId(null);
    setForm({ lastName: '', firstName: '', middleName: '', department: '', position: '', degree: '', email: '' });
    setIsModalOpen(true);
  };

  const openEdit = (teacher: NonNullable<typeof teachers>[number]) => {
    setEditingId(teacher.id);
    setForm({
      lastName: teacher.lastName,
      firstName: teacher.firstName,
      middleName: teacher.middleName || '',
      department: teacher.department || '',
      position: teacher.position || '',
      degree: teacher.degree || '',
      email: teacher.email || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingId) {
      updateMutation.mutate({ id: editingId, data: form });
    } else {
      createMutation.mutate(form);
    }
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Преподаватели</h1>
        <Button onClick={openCreate} className="flex items-center gap-2">
          <Plus size={18} /> Добавить
        </Button>
      </div>

      <div className="relative max-w-sm">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
        <Input placeholder="Поиск..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-10" />
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered?.map((t) => (
            <Card key={t.id}>
              <div className="p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <h3 className="text-base font-semibold">{t.fullName}</h3>
                  <div className="flex gap-1">
                    <button onClick={() => openEdit(t)} className="p-1 hover:bg-gray-100 rounded">
                      <Pencil size={14} />
                    </button>
                    <button onClick={() => deleteMutation.mutate(t.id)} className="p-1 hover:bg-red-50 text-red-500 rounded">
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
                <div className="text-sm text-gray-600 space-y-1">
                  <div className="flex items-center gap-2">
                    <GraduationCap size={14} />
                    <span>{t.position || '—'}{t.degree ? `, ${t.degree}` : ''}</span>
                  </div>
                  <div>{t.department || '—'}</div>
                  {t.email && (
                    <div className="flex items-center gap-2">
                      <Mail size={14} />
                      <a href={`mailto:${t.email}`} className="text-[var(--color-primary)] hover:underline">{t.email}</a>
                    </div>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingId ? 'Редактировать' : 'Новый преподаватель'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-3 gap-3">
            <Input placeholder="Фамилия *" required value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} />
            <Input placeholder="Имя *" required value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
            <Input placeholder="Отчество" value={form.middleName} onChange={(e) => setForm({ ...form, middleName: e.target.value })} />
          </div>
          <Input placeholder="Кафедра" value={form.department} onChange={(e) => setForm({ ...form, department: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input placeholder="Должность" value={form.position} onChange={(e) => setForm({ ...form, position: e.target.value })} />
            <Input placeholder="Уч. степень" value={form.degree} onChange={(e) => setForm({ ...form, degree: e.target.value })} />
          </div>
          <Input placeholder="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <div className="flex justify-end gap-2">
            <Button type="button" variant="secondary" onClick={() => setIsModalOpen(false)}>Отмена</Button>
            <Button type="submit">Сохранить</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
