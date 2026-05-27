import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMeetings, useActivateMeeting, useCloseMeeting, useCreateMeeting } from '../hooks/useMeetings';
import { useGeks } from '../hooks/useGeks';
import { useAuth } from '../hooks/useAuth';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { Input } from '../components/ui/Input';
import type { Meeting } from '../types';

const statusMap: Record<string, { label: string; variant: 'default' | 'info' | 'success' | 'danger' }> = {
  PLANNED: { label: 'Запланировано', variant: 'default' },
  SCHEDULED: { label: 'Назначено', variant: 'default' },
  ACTIVE: { label: 'Активно', variant: 'info' },
  CLOSED: { label: 'Завершено', variant: 'success' },
  CANCELLED: { label: 'Отменено', variant: 'danger' },
};

export const MeetingsPage: React.FC = () => {
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const { data: meetings, isLoading } = useMeetings();
  const { data: geks } = useGeks();
  const activate = useActivateMeeting();
  const close = useCloseMeeting();
  const create = useCreateMeeting();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [form, setForm] = useState({ meetingDate: '', startTime: '', endTime: '', location: '', gekId: '' });
  const [error, setError] = useState('');

  const handleCreate = async () => {
    setError('');
    if (!form.meetingDate || !form.startTime || !form.endTime || !form.location || !form.gekId) {
      setError('Все поля обязательны');
      return;
    }
    try {
      await create.mutateAsync({
        meetingDate: new Date(form.meetingDate).toISOString(),
        startTime: form.startTime,
        endTime: form.endTime,
        location: form.location,
        gekId: form.gekId,
      });
      setIsModalOpen(false);
      setForm({ meetingDate: '', startTime: '', endTime: '', location: '', gekId: '' });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Ошибка создания заседания');
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-[var(--color-text)]">Заседания ГЭК</h2>
        {hasRole('SECRETARY', 'METHODIST') && (
          <Button onClick={() => setIsModalOpen(true)}>Создать заседание</Button>
        )}
      </div>
      <Card>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-[var(--color-border)] bg-slate-50">
              <th className="px-4 py-3 text-left font-semibold">Дата</th>
              <th className="px-4 py-3 text-left font-semibold">Статус</th>
              <th className="px-4 py-3 text-left font-semibold">Место</th>
              <th className="px-4 py-3 text-left font-semibold">Действия</th>
            </tr>
          </thead>
          <tbody>
            {isLoading && (
              <tr><td colSpan={4} className="px-4 py-6 text-center text-[var(--color-text-muted)]">Загрузка...</td></tr>
            )}
            {meetings?.map((m: Meeting) => {
              const s = statusMap[m.status] || { label: m.status, variant: 'default' as const };
              return (
                <tr key={m.id} className="border-b border-[var(--color-border)] hover:bg-slate-50">
                  <td className="px-4 py-3">
                    {new Date(m.meetingDate).toLocaleString('ru-RU')}
                    {m.startTime && ` (${m.startTime}–${m.endTime})`}
                  </td>
                  <td className="px-4 py-3"><Badge variant={s.variant}>{s.label}</Badge></td>
                  <td className="px-4 py-3">{m.location || '—'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <Button size="sm" variant="secondary" onClick={() => navigate(`/meetings/${m.id}`)}>
                        Открыть
                      </Button>
                      {hasRole('SECRETARY') && m.status === 'PLANNED' && (
                        <Button size="sm" onClick={() => activate.mutate(m.id)}>Активировать</Button>
                      )}
                      {hasRole('SECRETARY') && m.status === 'ACTIVE' && (
                        <Button size="sm" variant="danger" onClick={() => close.mutate(m.id)}>Закрыть</Button>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </Card>

      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setError(''); }} title="Новое заседание">
        <div className="space-y-3">
          <Input label="Дата и время" type="datetime-local" value={form.meetingDate} onChange={(e) => setForm({ ...form, meetingDate: e.target.value })} />
          <Input label="Время начала" type="time" value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })} />
          <Input label="Время окончания" type="time" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} />
          <Input label="Место" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
          <div>
            <label className="mb-1 block text-sm font-medium text-[var(--color-text)]">ГЭК</label>
            <select
              className="w-full rounded-md border border-[var(--color-border)] bg-white px-3 py-2 text-sm text-[var(--color-text)] focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
              value={form.gekId}
              onChange={(e) => setForm({ ...form, gekId: e.target.value })}
            >
              <option value="">— Выберите ГЭК —</option>
              {geks?.map((g) => (
                <option key={g.id} value={g.id}>{g.name}</option>
              ))}
            </select>
          </div>
          {error && <p className="text-sm text-[var(--color-danger)]">{error}</p>}
          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" onClick={() => { setIsModalOpen(false); setError(''); }}>Отмена</Button>
            <Button onClick={handleCreate} isLoading={create.isPending}>Создать</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
