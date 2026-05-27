import React, { useState } from 'react';
import { useGrades } from '../hooks/useGrades';
import { useGroups } from '../hooks/useGroups';
import { Card } from '../components/ui/Card';

import { BarChart3, TrendingUp, Award } from 'lucide-react';

const getGradeColor = (percent: number) => {
  if (percent >= 85) return 'bg-emerald-500';
  if (percent >= 70) return 'bg-blue-500';
  if (percent >= 60) return 'bg-amber-500';
  return 'bg-red-500';
};

export const ReportsPage: React.FC = () => {
  const { data: grades, isLoading } = useGrades();
  const { data: groups } = useGroups();

  const [semesterFilter, setSemesterFilter] = useState<string>('');

  const semesters = Array.from(new Set(grades?.map((g) => g.semester).filter(Boolean) || []));

  const filtered = semesterFilter
    ? grades?.filter((g) => g.semester === semesterFilter)
    : grades;

  const stats = {
    total: filtered?.length || 0,
    excellent: filtered?.filter((g) => g.fivePointGrade === 5).length || 0,
    good: filtered?.filter((g) => g.fivePointGrade === 4).length || 0,
    satisfactory: filtered?.filter((g) => g.fivePointGrade === 3).length || 0,
    unsatisfactory: filtered?.filter((g) => g.fivePointGrade === 2).length || 0,
  };

  const avgScore = filtered?.length
    ? (filtered.reduce((sum, g) => sum + (g.totalScore || 0), 0) / filtered.length).toFixed(1)
    : '0';

  const groupStats = groups?.map((group) => {
    const groupGrades = filtered?.filter((g) => g.studentName?.includes(group.name));
    const avg = groupGrades?.length
      ? groupGrades.reduce((sum, g) => sum + (g.totalScore || 0), 0) / groupGrades.length
      : 0;
    return { ...group, avg: Math.round(avg) };
  }).sort((a, b) => b.avg - a.avg);

  if (isLoading) return <div className="text-center py-12 text-gray-500">Загрузка...</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Отчёты</h1>
      </div>

      <div className="flex gap-2">
        <button
          onClick={() => setSemesterFilter('')}
          className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${semesterFilter === '' ? 'bg-[var(--color-primary)] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}
        >
          Все семестры
        </button>
        {semesters.map((s) => (
          <button
            key={s}
            onClick={() => setSemesterFilter(s || '')}
            className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${semesterFilter === s ? 'bg-[var(--color-primary)] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}
          >
            {s}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-blue-100 rounded-lg">
            <BarChart3 size={24} className="text-blue-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{stats.total}</div>
            <div className="text-sm text-gray-600">Всего оценок</div>
          </div>
        </Card>
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-emerald-100 rounded-lg">
            <Award size={24} className="text-emerald-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{stats.excellent}</div>
            <div className="text-sm text-gray-600">Отлично</div>
          </div>
        </Card>
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-amber-100 rounded-lg">
            <TrendingUp size={24} className="text-amber-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">{avgScore}</div>
            <div className="text-sm text-gray-600">Средний балл</div>
          </div>
        </Card>
        <Card className="p-4 flex items-center gap-4">
          <div className="p-3 bg-purple-100 rounded-lg">
            <Award size={24} className="text-purple-600" />
          </div>
          <div>
            <div className="text-2xl font-bold">
              {stats.total > 0 ? Math.round(((stats.excellent + stats.good) / stats.total) * 100) : 0}%
            </div>
            <div className="text-sm text-gray-600">Успеваемость</div>
          </div>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="p-4">
          <h3 className="text-lg font-semibold mb-4">Распределение оценок</h3>
          <div className="space-y-3">
            {[
              { label: 'Отлично (5)', count: stats.excellent, color: 'bg-emerald-500' },
              { label: 'Хорошо (4)', count: stats.good, color: 'bg-blue-500' },
              { label: 'Удовлетворительно (3)', count: stats.satisfactory, color: 'bg-amber-500' },
              { label: 'Неудовлетворительно (2)', count: stats.unsatisfactory, color: 'bg-red-500' },
            ].map((item) => {
              const percent = stats.total > 0 ? (item.count / stats.total) * 100 : 0;
              return (
                <div key={item.label}>
                  <div className="flex justify-between text-sm mb-1">
                    <span>{item.label}</span>
                    <span className="font-medium">{item.count} ({Math.round(percent)}%)</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div className={`h-full ${item.color} transition-all duration-500`} style={{ width: `${percent}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </Card>

        <Card className="p-4">
          <h3 className="text-lg font-semibold mb-4">Топ групп по успеваемости</h3>
          <div className="space-y-3">
            {groupStats?.slice(0, 5).map((group, index) => (
              <div key={group.id} className="flex items-center gap-3">
                <div className="w-6 h-6 rounded-full bg-[var(--color-primary)] text-white flex items-center justify-center text-xs font-bold">
                  {index + 1}
                </div>
                <div className="flex-1">
                  <div className="flex justify-between text-sm">
                    <span className="font-medium">{group.name}</span>
                    <span>{group.avg} баллов</span>
                  </div>
                  <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden mt-1">
                    <div className={`h-full ${getGradeColor(group.avg)} transition-all`} style={{ width: `${Math.min((group.avg / 130) * 100, 100)}%` }} />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
};
