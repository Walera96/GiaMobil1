import React from 'react';
import { useParams } from 'react-router-dom';
import { useMeetingAgenda } from '../hooks/useMeetings';
import { useSse } from '../hooks/useSse';
import { Card } from '../components/ui/Card';

export const VotingMonitorPage: React.FC = () => {
  const { meetingId } = useParams<{ meetingId: string }>();
  const { data: agenda } = useMeetingAgenda(meetingId || '');
  const { data: sseData } = useSse<{ agendaItemId: string; averageScore: number; totalVotes: number }>(
    `http://localhost:8090/api/sse/meetings/${meetingId}`
  );

  return (
    <div className="flex min-h-screen flex-col bg-[var(--color-bg)] p-4">
      <h1 className="mb-6 text-center text-3xl font-bold text-[var(--color-text)]">
        Табло голосования
      </h1>
      <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-4">
        {agenda?.map((item) => {
          const liveAvg = sseData?.agendaItemId === item.id ? sseData.averageScore : item.averageScore;
          const hasVotes = (item.voteCount || 0) > 0;
          const allVoted = (item.voteCount || 0) >= 5;
          return (
            <Card
              key={item.id}
              className={[
                'flex flex-col items-center justify-center py-8 transition-colors',
                allVoted ? 'border-green-400 bg-green-50' : hasVotes ? 'border-amber-300 bg-amber-50' : 'bg-white',
              ].join(' ')}
            >
              <p className="mb-2 text-center text-sm font-medium text-[var(--color-text)]">
                {item.studentFullName}
              </p>
              <p className="text-5xl font-bold text-[var(--color-primary)]">
                {liveAvg !== undefined && liveAvg !== null ? liveAvg : '—'}
              </p>
              <p className="mt-1 text-xs text-[var(--color-text-muted)]">
                {item.voteCount || 0} / 5 голосов
              </p>
            </Card>
          );
        })}
      </div>
    </div>
  );
};
