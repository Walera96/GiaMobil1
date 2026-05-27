import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { studentsApi } from '../api/students';
import { gradesApi } from '../api/grades';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { ArrowLeft, BookOpen } from 'lucide-react';

const getGradeColor = (grade: number | undefined) => {
  if (!grade) return 'bg-gray-100 text-gray-600';
  if (grade >= 85) return 'bg-emerald-100 text-emerald-800';
  if (grade >= 70) return 'bg-blue-100 text-blue-800';
  if (grade >= 60) return 'bg-amber-100 text-amber-800';
  return 'bg-red-100 text-red-800';
};

export const StudentProfilePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: student, isLoading: studentLoading } = useQuery({
    queryKey: ['student', id, 'detail'],
    queryFn: () => studentsApi.getById(id!),
    enabled: !!id,
  });

  const { data: grades, isLoading: gradesLoading } = useQuery({
    queryKey: ['grades', 'student', id],
    queryFn: () => gradesApi.getByStudent(id!),
    enabled: !!id,
  });

  const avgTotal = grades?.length
    ? (grades.reduce((sum, g) => sum + (g.totalScore || 0), 0) / grades.length).toFixed(1)
    : '0';

  const avgFivePoint = grades?.length
    ? (grades.reduce((sum, g) => sum + (g.fivePointGrade || 0), 0) / grades.length).toFixed(1)
    : '0';

  if (studentLoading) return <div className="text-center py-12 text-gray-500">Загрузка...</div>;
  if (!student) return <div className="text-center py-12 text-gray-500">Студент не найден</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 rounded-md">
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text)]">
            {student.lastName} {student.firstName} {student.middleName || ''}
          </h1>
          <p className="text-sm text-gray-600">
            Группа: {student.group?.name || '—'} · {student.group?.course || '—'} курс
            {student.group?.direction && (
              <span> · {student.group.direction.code} {student.group.direction.name}</span>
            )}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4">
          <div className="text-sm text-gray-600">Зачетная книжка</div>
          <div className="text-lg font-bold">{student.recordBookNumber || '—'}</div>
        </Card>
        <Card className="p-4">
          <div className="text-sm text-gray-600">Средний балл</div>
          <div className="text-lg font-bold">{avgTotal}</div>
        </Card>
        <Card className="p-4">
          <div className="text-sm text-gray-600">Средняя оценка</div>
          <div className="text-lg font-bold">{avgFivePoint}</div>
        </Card>
        <Card className="p-4">
          <div className="text-sm text-gray-600">Всего оценок</div>
          <div className="text-lg font-bold">{grades?.length || 0}</div>
        </Card>
      </div>

      {student.thesisTopic && (
        <Card className="p-4">
          <div className="flex items-center gap-2 mb-2">
            <BookOpen size={18} className="text-[var(--color-primary)]" />
            <h3 className="font-semibold">Тема ВКР</h3>
          </div>
          <p className="text-gray-700">{student.thesisTopic}</p>
          {student.supervisorName && (
            <p className="text-sm text-gray-600 mt-1">Научный руководитель: {student.supervisorName}</p>
          )}
        </Card>
      )}

      <h2 className="text-lg font-semibold">Оценки</h2>
      {gradesLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка оценок...</div>
      ) : (
        <div className="space-y-2">
          {grades?.map((grade) => (
            <Card key={grade.id} className="p-4">
              <div className="flex items-center justify-between">
                <div>
                  <div className="font-medium">{grade.subjectName}</div>
                  <div className="text-sm text-gray-600">{grade.semester || '—'}</div>
                </div>
                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <div className="text-sm text-gray-600">Итого</div>
                    <div className="font-bold">{grade.totalScore || 0} / 130</div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm text-gray-600">ECTS</div>
                    <Badge className={getGradeColor(grade.totalScore)}>{grade.ectsGrade || '—'}</Badge>
                  </div>
                  <div className="text-right">
                    <div className="text-sm text-gray-600">5-балл</div>
                    <Badge className={getGradeColor(grade.totalScore)}>{grade.fivePointGrade || '—'}</Badge>
                  </div>
                </div>
              </div>
              <div className="grid grid-cols-4 gap-4 mt-3 pt-3 border-t text-center text-sm">
                <div>
                  <div className="text-gray-500">Текущий</div>
                  <div className="font-medium">{grade.currentControl || 0}</div>
                </div>
                <div>
                  <div className="text-gray-500">Посещ.</div>
                  <div className="font-medium">{grade.attendance || 0}</div>
                </div>
                <div>
                  <div className="text-gray-500">Активн.</div>
                  <div className="font-medium">{grade.activity || 0}</div>
                </div>
                <div>
                  <div className="text-gray-500">Экзамен</div>
                  <div className="font-medium">{grade.examScore || 0}</div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
