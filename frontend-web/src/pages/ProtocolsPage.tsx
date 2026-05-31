import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useDirections } from '../hooks/useDirections';
import { useGroups } from '../hooks/useGroups';
import { protocolsApi } from '../api/protocols';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Input } from '../components/ui/Input';
import { DocumentPreviewModal } from '../components/DocumentPreviewModal';
import type { Protocol, ProtocolRecord } from '../types';
import {
  Search, FileText, Download, CheckCircle2, Eye, Hash,
  GraduationCap, Building2, ArrowRight, AlertCircle
} from 'lucide-react';

export const ProtocolsPage: React.FC = () => {
  const { hasRole } = useAuth();
  const { data: directions } = useDirections();
  const { data: groups } = useGroups();

  const [meetingId, setMeetingId] = useState('');
  const [studentName, setStudentName] = useState('');
  const [groupId, setGroupId] = useState('');
  const [directionId, setDirectionId] = useState('');

  const [searchResults, setSearchResults] = useState<Protocol[]>([]);
  const [protocol, setProtocol] = useState<Protocol | null>(null);
  const [records, setRecords] = useState<ProtocolRecord[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [preview, setPreview] = useState<{ blob: Blob; filename: string; mimeType: string } | null>(null);

  const loadProtocol = async (mId: string) => {
    if (!mId) return;
    const p = await protocolsApi.getByMeetingId(mId);
    setProtocol(p);
    const r = await protocolsApi.getRecords(p.id);
    setRecords(r);
  };

  const handleSearch = async () => {
    setIsSearching(true);
    try {
      const params: Record<string, string> = {};
      if (studentName.trim()) params.studentName = studentName.trim();
      if (groupId) params.groupId = groupId;
      if (directionId) params.directionId = directionId;
      const results = await protocolsApi.search(params);
      setSearchResults(results);
      setProtocol(null);
      setRecords([]);
    } catch {
      alert('Ошибка поиска протоколов');
    } finally {
      setIsSearching(false);
    }
  };

  const downloadBlob = (blob: Blob, filename: string) => {
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();
  };

  const downloadWithFilename = (result: { blob: Blob; filename: string }) => {
    downloadBlob(result.blob, result.filename);
  };

  const handleApprove = async () => {
    if (!protocol) return;
    try {
      await protocolsApi.approve(protocol.id);
      alert('Протокол утверждён');
      if (meetingId) await loadProtocol(meetingId);
    } catch {
      alert('Ошибка утверждения протокола');
    }
  };

  const openPreview = async (docType: 'FINAL' | 'SCORESHEET' | 'INDIVIDUAL', pId: string) => {
    try {
      if (docType === 'FINAL') {
        const { blob, filename } = await protocolsApi.downloadFinalPdf(pId);
        setPreview({ blob, filename, mimeType: 'application/pdf' });
      } else if (docType === 'SCORESHEET') {
        const { blob, filename } = await protocolsApi.downloadScoreSheetPdf(meetingId);
        setPreview({ blob, filename, mimeType: 'application/pdf' });
      }
    } catch {
      alert('Ошибка загрузки предпросмотра');
    }
  };

  const statusMap: Record<string, { label: string; variant: 'default' | 'info' | 'success' | 'warning' }> = {
    DRAFT: { label: 'Черновик', variant: 'default' },
    APPROVED: { label: 'Утверждён', variant: 'info' },
    SIGNED: { label: 'Подписан', variant: 'success' },
    ARCHIVED: { label: 'Архив', variant: 'warning' },
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-[var(--color-primary)] text-white">
          <FileText size={22} />
        </div>
        <h2 className="text-2xl font-bold text-[var(--color-text)]">Протоколы</h2>
      </div>

      {/* Search filters */}
      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <Input
              placeholder="ФИО студента"
              value={studentName}
              onChange={(e) => setStudentName(e.target.value)}
              className="pl-9"
            />
          </div>
          <select
            className="rounded-md border border-[var(--color-border)] bg-white px-3 py-2 text-sm text-[var(--color-text)] focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
            value={groupId}
            onChange={(e) => setGroupId(e.target.value)}
          >
            <option value="">Все группы</option>
            {groups?.map((g) => (
              <option key={g.id} value={g.id}>
                {g.name}
              </option>
            ))}
          </select>
          <select
            className="rounded-md border border-[var(--color-border)] bg-white px-3 py-2 text-sm text-[var(--color-text)] focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
            value={directionId}
            onChange={(e) => setDirectionId(e.target.value)}
          >
            <option value="">Все направления</option>
            {directions?.map((d) => (
              <option key={d.id} value={d.id}>
                {d.code} — {d.name}
              </option>
            ))}
          </select>
          <Button onClick={handleSearch} disabled={isSearching}>
            <Search size={16} className="mr-1.5" />
            {isSearching ? 'Поиск...' : 'Найти'}
          </Button>
        </div>

        {/* Direct meeting loader */}
        <div className="mt-3 flex gap-2 border-t border-[var(--color-border)] pt-3">
          <div className="relative flex-1">
            <Hash size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <Input
              placeholder="ID заседания"
              value={meetingId}
              onChange={(e) => setMeetingId(e.target.value)}
              className="pl-9"
            />
          </div>
          <Button variant="secondary" onClick={() => loadProtocol(meetingId)}>
            <ArrowRight size={16} className="mr-1.5" />
            Загрузить
          </Button>
        </div>
      </Card>

      {/* Search results */}
      {searchResults.length > 0 && (
        <Card>
          <h3 className="mb-3 text-lg font-semibold text-[var(--color-text)]">Результаты поиска</h3>
          <div className="space-y-2">
            {searchResults.map((p) => (
              <div
                key={p.id}
                className="group flex cursor-pointer items-center justify-between rounded-lg border border-[var(--color-border)] p-3 transition-colors hover:border-blue-300 hover:bg-blue-50/30"
                onClick={() => {
                  if (p.meetingId) {
                    setMeetingId(p.meetingId);
                    loadProtocol(p.meetingId);
                  }
                }}
              >
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-slate-100 text-slate-500 group-hover:bg-blue-100 group-hover:text-blue-600">
                    <FileText size={18} />
                  </div>
                  <div>
                    <div className="font-medium text-[var(--color-text)]">
                      Протокол #{p.protocolNumber || p.id.slice(0, 8)}
                    </div>
                    <div className="text-xs text-gray-500">
                      Заседание: {p.meetingId?.slice(0, 8)}...
                    </div>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {(hasRole('GEK_SECRETARY') || hasRole('DEAN')) && p.meetingId && (
                    <Button
                      size="sm"
                      variant="ghost"
                      onClick={(e) => {
                        e.stopPropagation();
                        openPreview('FINAL', p.id);
                      }}
                    >
                      <Eye size={14} className="mr-1" />
                      Предпросмотр
                    </Button>
                  )}
                  <Badge variant={statusMap[p.status]?.variant || 'default'}>
                    {statusMap[p.status]?.label || p.status}
                  </Badge>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Protocol detail */}
      {protocol && (
        <Card>
          <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-100 text-blue-600">
                <FileText size={20} />
              </div>
              <div>
                <h3 className="text-lg font-semibold text-[var(--color-text)]">
                  Протокол #{protocol.protocolNumber || protocol.id.slice(0, 8)}
                </h3>
                <Badge variant={statusMap[protocol.status]?.variant || 'default'}>
                  {statusMap[protocol.status]?.label || protocol.status}
                </Badge>
              </div>
            </div>
            <div className="flex flex-wrap gap-2">
              <Button
                variant="secondary"
                size="sm"
                onClick={async () => {
                  try {
                    const result = await protocolsApi.downloadDocx(protocol.id);
                    downloadWithFilename(result);
                  } catch {
                    alert('Ошибка загрузки DOCX');
                  }
                }}
              >
                <Download size={14} className="mr-1.5" />
                Итоговой DOCX
              </Button>
              <Button
                variant="secondary"
                size="sm"
                onClick={async () => {
                  try {
                    const result = await protocolsApi.downloadScoreSheetDocx(meetingId);
                    downloadWithFilename(result);
                  } catch {
                    alert('Ошибка загрузки DOCX');
                  }
                }}
              >
                <Download size={14} className="mr-1.5" />
                Ведомость DOCX
              </Button>
              {(hasRole('GEK_SECRETARY') || hasRole('SYSTEM_ADMIN')) && (
                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() => openPreview('FINAL', protocol.id)}
                >
                  <Eye size={14} className="mr-1.5" />
                  Предпросмотр
                </Button>
              )}
              {hasRole('GEK_CHAIRMAN') && protocol.status === 'DRAFT' && (
                <Button size="sm" onClick={handleApprove}>
                  <CheckCircle2 size={14} className="mr-1.5" />
                  Утвердить
                </Button>
              )}
            </div>
          </div>

          {records.length === 0 ? (
            <div className="flex items-center justify-center gap-2 rounded-md bg-slate-50 py-8 text-sm text-gray-500">
              <AlertCircle size={16} />
              Записи протокола не найдены. Нажмите «Сгенерировать записи протокола».
            </div>
          ) : (
            <div className="overflow-hidden rounded-lg border border-[var(--color-border)]">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                    <th className="px-4 py-3 text-left font-semibold">Студент</th>
                    <th className="px-4 py-3 text-left font-semibold">Группа</th>
                    <th className="px-4 py-3 text-left font-semibold">Направление</th>
                    <th className="px-4 py-3 text-center font-semibold">Баллы</th>
                    <th className="px-4 py-3 text-center font-semibold">Оценка</th>
                    <th className="px-4 py-3 text-center font-semibold">Решение</th>
                    <th className="px-4 py-3 text-center font-semibold">DOCX</th>
                  </tr>
                </thead>
                <tbody>
                  {records.map((rec) => (
                    <tr
                      key={rec.id}
                      className="border-t border-[var(--color-border)] transition-colors hover:bg-blue-50/40"
                    >
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <GraduationCap size={16} className="text-[var(--color-primary)]" />
                          {rec.studentFullName || rec.studentId}
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <span className="flex items-center gap-1">
                          <Building2 size={14} className="text-gray-400" />
                          {rec.groupName || '—'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-[var(--color-text-muted)]">
                        {rec.directionCode ? `${rec.directionCode} — ${rec.directionName}` : '—'}
                      </td>
                      <td className="px-4 py-3 text-center font-medium">{rec.scorePoints ?? '—'}</td>
                      <td className="px-4 py-3 text-center font-bold text-[var(--color-primary)]">{rec.finalScore ?? '—'}</td>
                      <td className="px-4 py-3 text-center">{rec.decision || '—'}</td>
                      <td className="px-4 py-3 text-center">
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={async () => {
                            try {
                              const result = await protocolsApi.downloadIndividualDocx(rec.id);
                              downloadWithFilename(result);
                            } catch {
                              alert('Ошибка загрузки DOCX');
                            }
                          }}
                        >
                          <Download size={14} className="mr-1" />
                          Скачать
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {preview && (
        <DocumentPreviewModal
          isOpen={!!preview}
          onClose={() => setPreview(null)}
          blob={preview.blob}
          filename={preview.filename}
          mimeType={preview.mimeType}
        />
      )}
    </div>
  );
};
