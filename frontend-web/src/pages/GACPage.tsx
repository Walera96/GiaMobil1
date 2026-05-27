import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMeetings } from '../hooks/useMeetings';
import { useGeks } from '../hooks/useGeks';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Search, CalendarDays, Users, FileText, ArrowRight, Vote } from 'lucide-react';

const meetingStatusMap: Record<string, { label: string; variant: 'default' | 'info' | 'success' | 'warning' | 'danger' }> = {
  PLANNED: { label: 'Запланировано', variant: 'info' },
  ACTIVE: { label: 'Активно', variant: 'success' },
  CLOSED: { label: 'Закрыто', variant: 'default' },
  CANCELLED: { label: 'Отменено', variant: 'danger' },
};

export const GACPage: React.FC = () => {
  const navigate = useNavigate();
  const { data: meetings, isLoading: meetingsLoading } = useMeetings();
  const { data: geks } = useGeks();

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | null>(null);

  const filteredMeetings = meetings?.filter((m) => {
    const matchesSearch = m.location?.toLowerCase().includes(search.toLowerCase()) ||
      m.meetingDate?.includes(search);
    const matchesStatus = statusFilter ? m.status === statusFilter : true;
    return matchesSearch && matchesStatus;
  });

  const activeMeetings = meetings?.filter((m) => m.status === 'ACTIVE') || [];
  const closedMeetings = meetings?.filter((m) => m.status === 'CLOSED') || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Государственная аттестационная комиссия</h1>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-blue-100 rounded-lg">
            <CalendarDays size={24} className="text-blue-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{meetings?.length || 0}</div>
            <div className="text-sm text-gray-600">Всего заседаний</div>
          </div>
        </Card>
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-emerald-100 rounded-lg">
            <Vote size={24} className="text-emerald-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{activeMeetings.length}</div>
            <div className="text-sm text-gray-600">Активных</div>
          </div>
        </Card>
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-purple-100 rounded-lg">
            <FileText size={24} className="text-purple-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{closedMeetings.length}</div>
            <div className="text-sm text-gray-600">Завершённых</div>
          </div>
        </Card>
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-amber-100 rounded-lg">
            <Users size={24} className="text-amber-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{geks?.length || 0}</div>
            <div className="text-sm text-gray-600">ГЭК</div>
          </div>
        </Card>
      </div>

      <div className="flex gap-4">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <Input placeholder="Поиск заседания..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-10" />
        </div>
        <div className="flex gap-2">
          <button onClick={() => setStatusFilter(null)} className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${statusFilter === null ? 'bg-[var(--color-primary)] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}>
            Все
          </button>
          {Object.entries(meetingStatusMap).map(([key, { label }]) => (
            <button key={key} onClick={() => setStatusFilter(key)} className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${statusFilter === key ? 'bg-[var(--color-primary)] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}>
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className="space-y-3">
        <h2 className="text-lg font-semibold">Заседания ГЭК</h2>
        {meetingsLoading ? (
          <div className="text-center py-12 text-gray-500">Загрузка...</div>
        ) : (
          filteredMeetings?.map((m) => {
            const status = meetingStatusMap[m.status] || { label: m.status, variant: 'default' };
            return (
              <Card key={m.id} className="p-4 cursor-pointer hover:shadow-md transition-shadow" onClick={() => navigate(`/meetings/${m.id}`)}>
                <div className="flex items-center justify-between">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <CalendarDays size={18} className="text-[var(--color-primary)]" />
                      <span className="font-medium">Заседание ГЭК</span>
                      <Badge variant={status.variant}>{status.label}</Badge>
                    </div>
                    <div className="text-sm text-gray-600">
                      Дата: {new Date(m.meetingDate).toLocaleDateString('ru-RU')} · Место: {m.location || '—'} · Кворум: {m.quorumRequired}
                    </div>
                  </div>
                  <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); navigate(`/meetings/${m.id}`); }}>
                    <ArrowRight size={16} />
                  </Button>
                </div>
              </Card>
            );
          })
        )}
      </div>

      <div className="space-y-3">
        <h2 className="text-lg font-semibold">Комиссии (ГЭК)</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {geks?.map((gek) => (
            <Card key={gek.id} className="p-4">
              <div className="flex items-center gap-3">
                <div className="p-2 bg-[var(--color-primary)] text-white rounded-lg">
                  <Users size={18} />
                </div>
                <div>
                  <div className="font-medium">{gek.name}</div>
                  <div className="text-sm text-gray-600">ID: {gek.id.slice(0, 8)}</div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
};
