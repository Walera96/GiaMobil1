import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useGroups } from '../hooks/useGroups';
import { studentsApi } from '../api/students';
import { useQuery } from '@tanstack/react-query';
import { Card } from '../components/ui/Card';

import { ArrowLeft, Users } from 'lucide-react';

export const GroupDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: groups } = useGroups();

  const group = groups?.find((g) => g.id === id);

  const { data: students, isLoading } = useQuery({
    queryKey: ['students', 'group', id],
    queryFn: () => studentsApi.getAll(id),
    enabled: !!id,
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button onClick={() => navigate('/groups')} className="p-2 hover:bg-gray-100 rounded-md">
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text)]">{group?.name || 'Группа'}</h1>
          <p className="text-sm text-gray-600">{group?.course} курс</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="p-4 flex items-center gap-3">
          <Users size={24} className="text-[var(--color-primary)]" />
          <div>
            <div className="text-2xl font-bold">{students?.length || 0}</div>
            <div className="text-sm text-gray-600">Студентов</div>
          </div>
        </Card>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка...</div>
      ) : (
        <div className="space-y-2">
          {students?.map((student, index) => (
            <Card
              key={student.id}
              className="p-4 cursor-pointer hover:shadow-md transition-shadow"
              onClick={() => navigate(`/students/${student.id}`)}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="w-10 h-10 rounded-full bg-[var(--color-primary)] text-white flex items-center justify-center font-bold">
                    {index + 1}
                  </div>
                  <div>
                    <div className="font-medium">
                      {student.lastName} {student.firstName} {student.middleName || ''}
                    </div>
                    <div className="text-sm text-gray-600">
                      № зач. книжки: {student.recordBookNumber || '—'}
                    </div>
                  </div>
                </div>
                <div className="flex gap-2">
                  {student.thesisTopic && (
                    <div className="text-sm text-gray-500 max-w-xs truncate" title={student.thesisTopic}>
                      {student.thesisTopic}
                    </div>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
