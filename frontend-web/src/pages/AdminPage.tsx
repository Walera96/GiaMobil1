import React, { useState } from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';

import { Modal } from '../components/ui/Modal';
import { Input } from '../components/ui/Input';
import { useUsers, useCreateUser, useUpdateUser, useDeleteUser } from '../hooks/useUsers';
import { Pencil, Trash2, Plus } from 'lucide-react';

const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Администратор',
  METHODIST: 'Методист',
  SECRETARY: 'Секретарь',
  CHAIRMAN: 'Председатель',
  GEK_MEMBER: 'Член ГЭК',
  STUDENT: 'Студент',
  DEAN: 'Деканат',
};

const demoAudit = [
  { id: '1', timestamp: '2026-05-15 14:32', user: 'secretary', table: 'meeting', action: 'INSERT', oldValue: '-', newValue: 'Заседание 15.05.2026' },
  { id: '2', timestamp: '2026-05-15 15:10', user: 'member1', table: 'vote', action: 'INSERT', oldValue: '-', newValue: 'score=5' },
];

export const AdminPage: React.FC = () => {
  const { data: users, isLoading } = useUsers();
  const createMutation = useCreateUser();
  const updateMutation = useUpdateUser();
  const deleteMutation = useDeleteUser();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState({ username: '', password: '', fullName: '', email: '', role: 'GEK_MEMBER' });
  const [error, setError] = useState<string | null>(null);

  const openCreate = () => {
    setEditingId(null);
    setForm({ username: '', password: '', fullName: '', email: '', role: 'GEK_MEMBER' });
    setError(null);
    setIsModalOpen(true);
  };

  const openEdit = (user: { id: string; username: string; fullName: string; email: string; role: string }) => {
    setEditingId(user.id);
    setForm({ username: user.username, password: '', fullName: user.fullName || '', email: user.email || '', role: user.role });
    setError(null);
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!form.username.trim()) {
      setError('Введите логин');
      return;
    }
    if (!editingId && !form.password) {
      setError('Введите пароль для нового пользователя');
      return;
    }
    if (editingId) {
      updateMutation.mutate(
        { id: editingId, data: { username: form.username, fullName: form.fullName, email: form.email, role: form.role } },
        { onSuccess: () => setIsModalOpen(false), onError: (err: any) => setError(err?.response?.data?.message || 'Ошибка сохранения') }
      );
    } else {
      createMutation.mutate(
        { username: form.username, password: form.password, fullName: form.fullName, email: form.email, role: form.role },
        { onSuccess: () => setIsModalOpen(false), onError: (err: any) => setError(err?.response?.data?.message || 'Ошибка создания') }
      );
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-[var(--color-text)]">Администрирование</h2>

      <div>
        <div className="mb-3 flex items-center justify-between">
          <h3 className="text-lg font-semibold">Пользователи</h3>
          <Button size="sm" onClick={openCreate} className="flex items-center gap-1">
            <Plus size={16} /> Добавить
          </Button>
        </div>
        <Card>
          {isLoading ? (
            <div className="p-4 text-gray-500">Загрузка...</div>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-[var(--color-border)] bg-slate-50">
                  <th className="px-4 py-3 text-left">ФИО</th>
                  <th className="px-4 py-3 text-left">Логин</th>
                  <th className="px-4 py-3 text-left">Роль</th>
                  <th className="px-4 py-3 text-left">Email</th>
                  <th className="px-4 py-3 text-right">Действия</th>
                </tr>
              </thead>
              <tbody>
                {users?.map((u) => (
                  <tr key={u.id} className="border-b border-[var(--color-border)] hover:bg-slate-50">
                    <td className="px-4 py-3 font-medium">{u.fullName || '—'}</td>
                    <td className="px-4 py-3">{u.username}</td>
                    <td className="px-4 py-3">{ROLE_LABELS[u.role] || u.role}</td>
                    <td className="px-4 py-3">{u.email || '—'}</td>
                    <td className="px-4 py-3 text-right">
                      <div className="flex justify-end gap-1">
                        <button onClick={() => openEdit(u)} className="p-1 hover:bg-gray-100 rounded" title="Редактировать">
                          <Pencil size={14} />
                        </button>
                        <button onClick={() => deleteMutation.mutate(u.id)} className="p-1 hover:bg-red-50 text-red-500 rounded" title="Удалить">
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </Card>
      </div>

      <div>
        <h3 className="mb-3 text-lg font-semibold">Журнал аудита</h3>
        <Card>
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-[var(--color-border)] bg-slate-50">
                <th className="px-4 py-3 text-left">Время</th>
                <th className="px-4 py-3 text-left">Пользователь</th>
                <th className="px-4 py-3 text-left">Таблица</th>
                <th className="px-4 py-3 text-left">Действие</th>
                <th className="px-4 py-3 text-left">Изменение</th>
              </tr>
            </thead>
            <tbody>
              {demoAudit.map((a) => (
                <tr key={a.id} className="border-b border-[var(--color-border)] hover:bg-slate-50">
                  <td className="px-4 py-3">{a.timestamp}</td>
                  <td className="px-4 py-3">{a.user}</td>
                  <td className="px-4 py-3">{a.table}</td>
                  <td className="px-4 py-3">{a.action}</td>
                  <td className="px-4 py-3">
                    <span className="text-[var(--color-text-muted)]">{a.oldValue}</span>
                    {' → '}
                    <span className="font-medium">{a.newValue}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingId ? 'Редактировать пользователя' : 'Новый пользователь'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          {error && <div className="rounded-md bg-red-50 p-2 text-sm text-red-600">{error}</div>}
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Логин <span className="text-red-500">*</span></label>
            <Input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required />
          </div>
          {!editingId && (
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Пароль <span className="text-red-500">*</span></label>
              <Input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required={!editingId} />
            </div>
          )}
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">ФИО</label>
            <Input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Email</label>
            <Input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Роль</label>
            <select
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
            >
              <option value="ADMIN">Администратор</option>
              <option value="METHODIST">Методист</option>
              <option value="SECRETARY">Секретарь</option>
              <option value="CHAIRMAN">Председатель</option>
              <option value="GEK_MEMBER">Член ГЭК</option>
              <option value="STUDENT">Студент</option>
              <option value="DEAN">Деканат</option>
            </select>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="ghost" onClick={() => setIsModalOpen(false)}>Отмена</Button>
            <Button type="submit">Сохранить</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
