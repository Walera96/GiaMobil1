import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useMeeting, useMeetingAgenda } from '../hooks/useMeetings';
import { useAuth } from '../hooks/useAuth';
import { protocolsApi } from '../api/protocols';
import { votingApi } from '../api/voting';
import { useCastVote, useVoteDetails, useFinishVoting } from '../hooks/useVoting';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { useSse } from '../hooks/useSse';
import {
  ListOrdered, Vote, FileText, Download, MessageSquare,
  CheckCircle2, Clock, MapPin, Users, Send, Flag
} from 'lucide-react';

export const MeetingDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { hasRole } = useAuth();
  const { data: meeting } = useMeeting(id || '');
  const { data: agenda } = useMeetingAgenda(id || '');
  const [tab, setTab] = useState<'agenda' | 'voting' | 'protocol'>('agenda');
  const [comments, setComments] = useState<Record<string, string>>({});
  const [scores, setScores] = useState<Record<string, number>>({});

  const castVote = useCastVote();
  const finishVoting = useFinishVoting();

  const { data: sseData } = useSse<{ agendaItemId: string; averageScore: number; totalVotes: number }>(
    `http://localhost:8090/api/sse/meetings/${id}`
  );

  const handleDownloadPdf = async (downloadFn: () => Promise<{ blob: Blob; filename: string }>) => {
    try {
      const result = await downloadFn();
      const link = document.createElement('a');
      link.href = URL.createObjectURL(result.blob);
      link.download = result.filename;
      link.click();
    } catch {
      alert('Ошибка загрузки документа');
    }
  };

  const handleDownloadThesis = async (studentId: string, fileName?: string) => {
    try {
      const blob = await votingApi.downloadThesis(studentId);
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName || 'thesis.pdf';
      link.click();
    } catch {
      alert('Ошибка загрузки файла ВКР');
    }
  };

  const handleVote = (agendaItemId: string) => {
    const score = scores[agendaItemId];
    if (!score) {
      alert('Выберите оценку');
      return;
    }
    castVote.mutate(
      {
        agendaItemId,
        score,
        comment: comments[agendaItemId] || '',
      },
      {
        onSuccess: () => {
          setScores((prev) => ({ ...prev, [agendaItemId]: 0 }));
          setComments((prev) => ({ ...prev, [agendaItemId]: '' }));
        },
        onError: () => {
          alert('Ошибка голосования');
        },
      }
    );
  };

  const canViewComments = hasRole('CHAIRMAN') || (hasRole('GEK_MEMBER') && meeting?.status === 'CLOSED');
  const isMeetingActive = meeting?.status === 'ACTIVE';

  const tabs = [
    { key: 'agenda' as const, label: 'Повестка', icon: <ListOrdered size={16} /> },
    { key: 'voting' as const, label: 'Голосование', icon: <Vote size={16} /> },
    { key: 'protocol' as const, label: 'Протокол', icon: <FileText size={16} /> },
  ];

  return (
    <div className="space-y-4">
      {/* Шапка заседания */}
      <Card className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-blue-600 text-white">
            <Users size={24} />
          </div>
          <div>
            <h2 className="text-xl font-bold text-[var(--color-text)]">
              Заседание {meeting && new Date(meeting.meetingDate).toLocaleDateString('ru-RU')}
            </h2>
            <div className="mt-0.5 flex flex-wrap items-center gap-3 text-sm text-[var(--color-text-muted)]">
              <span className="flex items-center gap-1">
                <MapPin size={14} />
                {meeting?.location}
              </span>
              <span className="flex items-center gap-1">
                <Clock size={14} />
                {meeting?.startTime?.slice(0, 5)} — {meeting?.endTime?.slice(0, 5)}
              </span>
            </div>
          </div>
        </div>
        <Badge
          variant={
            meeting?.status === 'ACTIVE'
              ? 'info'
              : meeting?.status === 'CLOSED'
                ? 'success'
                : 'default'
          }
          className="self-start sm:self-auto"
        >
          {meeting?.status === 'ACTIVE' && <Clock size={12} className="mr-1 inline" />}
          {meeting?.status === 'CLOSED' && <CheckCircle2 size={12} className="mr-1 inline" />}
          {meeting?.status}
        </Badge>
      </Card>

      {/* Табы с иконками */}
      <div className="flex gap-1 border-b border-[var(--color-border)]">
        {tabs.map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={[
              'flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors',
              tab === t.key
                ? 'border-b-2 border-[var(--color-primary)] text-[var(--color-primary)]'
                : 'text-[var(--color-text-muted)] hover:text-[var(--color-text)]',
            ].join(' ')}
          >
            {t.icon}
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'agenda' && (
        <Card className="overflow-hidden p-0">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                  <th className="px-4 py-3 text-left font-semibold">Студент</th>
                  <th className="px-4 py-3 text-left font-semibold">Тема ВКР</th>
                  <th className="px-4 py-3 text-left font-semibold">Руководитель</th>
                  <th className="px-4 py-3 text-center font-semibold">Средний балл</th>
                  <th className="px-4 py-3 text-center font-semibold">Статус</th>
                </tr>
              </thead>
              <tbody>
                {agenda?.map((item) => (
                  <tr
                    key={item.id}
                    className="border-t border-[var(--color-border)] transition-colors hover:bg-blue-50/40"
                  >
                    <td className="px-4 py-3">
                      <div className="font-medium">{item.studentFullName}</div>
                      <div className="text-xs text-gray-500">{item.studentRecordBook}</div>
                    </td>
                    <td className="px-4 py-3 text-[var(--color-text-muted)]">{item.thesisTopic || '—'}</td>
                    <td className="px-4 py-3 text-[var(--color-text-muted)]">{item.supervisorName || '—'}</td>
                    <td className="px-4 py-3 text-center">
                      {item.overallAverageScore !== undefined && item.overallAverageScore !== null ? (
                        <span className="text-lg font-bold text-[var(--color-primary)]">{item.overallAverageScore}</span>
                      ) : (
                        <span className="text-gray-400">—</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-center">
                      {item.voteCount && item.voteCount > 0 ? (
                        <Badge variant="success">
                          <CheckCircle2 size={12} className="mr-1 inline" />
                          Проголосовано
                        </Badge>
                      ) : (
                        <Badge variant="default">Ожидание</Badge>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {tab === 'voting' && (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
          {agenda?.map((item) => {
            const live = sseData?.agendaItemId === item.id ? sseData.averageScore : item.overallAverageScore;
            const hasVotes = (item.voteCount || 0) > 0;
            const selectedScore = scores[item.id] || 0;
            const commentText = comments[item.id] || '';

            return (
              <Card
                key={item.id}
                className={[
                  'transition-shadow hover:shadow-md',
                  hasVotes ? 'border-l-4 border-l-green-500' : 'border-l-4 border-l-slate-300',
                ].join(' ')}
              >
                <div className="mb-3 flex items-start justify-between">
                  <div>
                    <div className="text-sm font-semibold text-[var(--color-text)]">{item.studentFullName}</div>
                    <div className="text-xs text-gray-500">{item.studentRecordBook}</div>
                  </div>
                  {item.thesisFilePath && (
                    <Button
                      size="sm"
                      variant="ghost"
                      onClick={() => handleDownloadThesis(item.studentId!, item.thesisFileName || undefined)}
                    >
                      <Download size={14} className="mr-1" />
                      ВКР
                    </Button>
                  )}
                </div>

                <div className="flex items-end gap-2">
                  <span className="text-3xl font-bold text-[var(--color-primary)]">
                    {live !== undefined && live !== null ? live : '—'}
                  </span>
                  <span className="mb-1 text-xs text-[var(--color-text-muted)]">
                    {item.voteCount || 0} голосов
                  </span>
                </div>

                <div className="mt-2 h-2 w-full rounded-full bg-slate-100">
                  <div
                    className={[
                      'h-2 rounded-full transition-all duration-500',
                      hasVotes ? 'bg-green-500' : 'bg-slate-300',
                    ].join(' ')}
                    style={{ width: `${Math.min((item.voteCount || 0) * 20, 100)}%` }}
                  />
                </div>

                {hasRole('GEK_MEMBER') && isMeetingActive && (
                  <div className="mt-4 space-y-3 border-t border-[var(--color-border)] pt-3">
                    <div className="text-xs font-medium text-[var(--color-text-muted)]">Ваша оценка</div>
                    <div className="flex gap-2">
                      {[2, 3, 4, 5].map((s) => (
                        <button
                          key={s}
                          onClick={() => setScores((prev) => ({ ...prev, [item.id]: s }))}
                          className={[
                            'h-10 w-10 rounded-lg text-sm font-bold transition-all',
                            selectedScore === s
                              ? 'bg-[var(--color-primary)] text-white shadow-md'
                              : 'bg-slate-100 text-[var(--color-text)] hover:bg-slate-200',
                          ].join(' ')}
                        >
                          {s}
                        </button>
                      ))}
                    </div>
                    <textarea
                      className="w-full rounded-md border border-[var(--color-border)] bg-white px-3 py-2 text-sm text-[var(--color-text)] focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
                      rows={2}
                      placeholder="Комментарий (необязательно)"
                      value={commentText}
                      onChange={(e) => setComments((prev) => ({ ...prev, [item.id]: e.target.value }))}
                    />
                    <Button
                      size="sm"
                      className="w-full"
                      onClick={() => handleVote(item.id)}
                      disabled={castVote.isPending || !selectedScore}
                    >
                      <Send size={14} className="mr-1.5" />
                      {castVote.isPending ? 'Отправка...' : 'Проголосовать'}
                    </Button>
                  </div>
                )}

                {hasRole('CHAIRMAN') && (
                  <div className="mt-3 border-t border-[var(--color-border)] pt-3">
                    <Button
                      size="sm"
                      variant="secondary"
                      className="w-full"
                      onClick={() => finishVoting.mutate(item.id)}
                      disabled={finishVoting.isPending}
                    >
                      <Flag size={14} className="mr-1.5" />
                      {finishVoting.isPending ? 'Завершение...' : 'Завершить голосование'}
                    </Button>
                  </div>
                )}

                {canViewComments && <VoteComments agendaItemId={item.id} />}
              </Card>
            );
          })}
        </div>
      )}

      {tab === 'protocol' && (
        <div className="space-y-3">
          <div className="flex flex-wrap gap-2">
            <Button
              variant="secondary"
              onClick={() => id && handleDownloadPdf(() => protocolsApi.downloadScoreSheetDocx(id))}
            >
              <Download size={16} className="mr-1.5" />
              Ведомость (DOCX)
            </Button>
            <Button
              variant="secondary"
              onClick={() => id && handleDownloadPdf(() => protocolsApi.downloadDocx(id))}
            >
              <Download size={16} className="mr-1.5" />
              Итоговой протокол (DOCX)
            </Button>
            {hasRole('CHAIRMAN') && (
              <Button onClick={() => id && protocolsApi.approve(id)}>
                <CheckCircle2 size={16} className="mr-1.5" />
                Утвердить протокол
              </Button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const VoteComments: React.FC<{ agendaItemId: string }> = ({ agendaItemId }) => {
  const { data: details } = useVoteDetails(agendaItemId);
  const votes = details?.votes || [];
  const comments = votes.filter((v) => v.comment && v.comment.trim().length > 0);

  if (comments.length === 0) return null;

  return (
    <div className="mt-3 border-t border-[var(--color-border)] pt-3">
      <div className="mb-2 flex items-center gap-1.5 text-xs font-medium text-[var(--color-text-muted)]">
        <MessageSquare size={14} />
        Комментарии членов ГЭК
      </div>
      <div className="space-y-2">
        {comments.map((vote) => (
          <div key={vote.id} className="rounded-md bg-slate-50 p-2.5 text-xs">
            <div className="flex items-center justify-between">
              <span className="font-medium text-[var(--color-text)]">{vote.gekMemberName || 'Член ГЭК'}</span>
              <Badge variant="default">Оценка: {vote.score}</Badge>
            </div>
            <p className="mt-1 text-[var(--color-text-muted)]">{vote.comment}</p>
          </div>
        ))}
      </div>
    </div>
  );
};
