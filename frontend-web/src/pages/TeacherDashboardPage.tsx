import React from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import {
  GraduationCap, FileText, Users, BookOpen, ClipboardList
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const TeacherDashboardPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">
          Портал преподавателя
        </h1>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Card className="p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => navigate('/teacher/assignments')}>
          <div className="flex items-center gap-4">
            <div className="rounded-lg bg-teal-100 p-3 text-teal-700">
              <ClipboardList size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-[var(--color-text)]">Задания</h3>
              <p className="text-sm text-[var(--color-text-muted)]">Создание и проверка заданий</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => navigate('/groups')}>
          <div className="flex items-center gap-4">
            <div className="rounded-lg bg-blue-100 p-3 text-blue-700">
              <Users size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-[var(--color-text)]">Группы</h3>
              <p className="text-sm text-[var(--color-text-muted)]">Управление группами</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => navigate('/statements')}>
          <div className="flex items-center gap-4">
            <div className="rounded-lg bg-amber-100 p-3 text-amber-700">
              <FileText size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-[var(--color-text)]">Ведомости</h3>
              <p className="text-sm text-[var(--color-text-muted)]">Работа с ведомостями</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => navigate('/disciplines')}>
          <div className="flex items-center gap-4">
            <div className="rounded-lg bg-purple-100 p-3 text-purple-700">
              <BookOpen size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-[var(--color-text)]">Дисциплины</h3>
              <p className="text-sm text-[var(--color-text-muted)]">Список дисциплин</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => navigate('/gradebook')}>
          <div className="flex items-center gap-4">
            <div className="rounded-lg bg-emerald-100 p-3 text-emerald-700">
              <GraduationCap size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-[var(--color-text)]">Журнал оценок</h3>
              <p className="text-sm text-[var(--color-text-muted)]">Выставление оценок</p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};
