import React, { useRef, useState } from 'react';
import { useStudentProfile, useStudentGrades, useStudentAdmission, useStudentMeetingInfo, useUploadThesis, useUpdateStudentProfile } from '../hooks/useStudentProfile';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import {
  GraduationCap, BookOpen, ClipboardCheck, FileText, CalendarDays,
  Upload, Download, MapPin, Clock, User, Hash, Award, AlertCircle,
  CheckCircle2, XCircle, Building2
} from 'lucide-react';

const ScoreBadge: React.FC<{ score: number }> = ({ score }) => {
  let variant: 'success' | 'warning' | 'danger' | 'default' = 'default';
  if (score >= 4) variant = 'success';
  else if (score === 3) variant = 'warning';
  else if (score < 3) variant = 'danger';
  return <Badge variant={variant}>{score}</Badge>;
};

export const StudentDashboardPage: React.FC = () => {
  const { data: profile, isLoading: profileLoading } = useStudentProfile();
  const { data: grades, isLoading: gradesLoading } = useStudentGrades();
  const { data: admission, isLoading: admissionLoading } = useStudentAdmission();
  const { data: meetingInfo, isLoading: meetingLoading } = useStudentMeetingInfo();
  const uploadThesis = useUploadThesis();
  const updateProfile = useUpdateStudentProfile();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [editThesisTopic, setEditThesisTopic] = useState('');
  const [editSupervisor, setEditSupervisor] = useState('');

  const startEditing = () => {
    setEditThesisTopic(profile?.thesisTopic || '');
    setEditSupervisor(profile?.supervisorName || '');
    setIsEditingProfile(true);
  };

  const saveProfile = () => {
    updateProfile.mutate(
      { thesisTopic: editThesisTopic || undefined, supervisorName: editSupervisor || undefined },
      {
        onSuccess: () => {
          setIsEditingProfile(false);
          alert('Профиль обновлён');
        },
        onError: () => alert('Ошибка обновления профиля'),
      }
    );
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    uploadThesis.mutate(file, {
      onSuccess: () => alert('Файл ВКР успешно загружен'),
      onError: () => alert('Ошибка загрузки файла ВКР'),
    });
  };

  const handleDownloadThesis = () => {
    if (!profile?.thesisFilePath) return;
    const link = document.createElement('a');
    link.href = `${import.meta.env.VITE_API_URL || 'http://localhost:8090/api'}/students/${profile.id}/thesis`;
    link.download = profile.thesisFileName || 'thesis.pdf';
    link.click();
  };

  if (profileLoading) {
    return (
      <div className="flex items-center justify-center py-16 text-gray-500">
        <div className="mr-3 h-6 w-6 animate-spin rounded-full border-2 border-[var(--color-primary)] border-t-transparent" />
        Загрузка профиля...
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="flex items-center justify-center py-16 text-gray-500">
        <AlertCircle className="mr-2" size={20} />
        Профиль не найден
      </div>
    );
  }

  const initials = profile.fullName
    .split(' ')
    .map((w) => w[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();

  return (
    <div className="space-y-6">
      {/* Заголовок */}
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-[var(--color-primary)] text-white">
          <GraduationCap size={22} />
        </div>
        <h2 className="text-2xl font-bold text-[var(--color-text)]">Личный кабинет студента</h2>
      </div>

      {/* Карточка профиля */}
      <Card className="relative overflow-hidden">
        <div className="absolute inset-x-0 top-0 h-24 bg-gradient-to-r from-blue-600 to-indigo-600" />
        <div className="relative flex flex-col gap-4 sm:flex-row sm:items-end">
          <div className="flex h-20 w-20 items-center justify-center rounded-xl border-4 border-white bg-[var(--color-primary)] text-2xl font-bold text-white shadow-md">
            {initials}
          </div>
          <div className="flex-1 pb-1">
            <h3 className="text-xl font-bold text-[var(--color-text)]">{profile.fullName}</h3>
            <div className="mt-2 flex flex-wrap gap-x-4 gap-y-1 text-sm text-[var(--color-text-muted)]">
              <span className="flex items-center gap-1">
                <Building2 size={14} />
                {profile.groupName || '—'}
              </span>
              <span className="flex items-center gap-1">
                <Hash size={14} />
                {profile.recordBookNumber || '—'}
              </span>
              <span className="flex items-center gap-1">
                <Award size={14} />
                Средний балл: <strong className="text-[var(--color-text)]">{profile.averageGrade ?? '—'}</strong>
              </span>
            </div>
          </div>
        </div>
        <div className="relative mt-4 grid grid-cols-1 gap-2 text-sm text-[var(--color-text-muted)] sm:grid-cols-2">
          <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2">
            <BookOpen size={16} className="text-[var(--color-primary)]" />
            <span>Направление: <strong className="text-[var(--color-text)]">{profile.directionCode || '—'} — {profile.directionName || '—'}</strong></span>
          </div>
          <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2">
            <User size={16} className="text-[var(--color-primary)]" />
            <span>Руководитель: <strong className="text-[var(--color-text)]">{profile.supervisorName || 'Не назначен'}</strong></span>
          </div>
        </div>
      </Card>

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        {/* Мои оценки */}
        <Card>
          <div className="mb-3 flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-100 text-blue-600">
              <BookOpen size={18} />
            </div>
            <h3 className="font-semibold text-[var(--color-text)]">Мои оценки</h3>
          </div>
          {gradesLoading ? (
            <div className="flex items-center gap-2 text-sm text-gray-500">
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-blue-400 border-t-transparent" />
              Загрузка...
            </div>
          ) : grades && grades.length > 0 ? (
            <div className="overflow-hidden rounded-md border border-[var(--color-border)]">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                    <th className="px-3 py-2 text-left font-semibold">Предмет</th>
                    <th className="px-3 py-2 text-center font-semibold">Оценка</th>
                    <th className="px-3 py-2 text-center font-semibold">Семестр</th>
                  </tr>
                </thead>
                <tbody>
                  {grades.map((g) => (
                    <tr key={g.id} className="border-t border-[var(--color-border)] transition-colors hover:bg-blue-50/50">
                      <td className="px-3 py-2">{g.subjectName}</td>
                      <td className="px-3 py-2 text-center">
                        <ScoreBadge score={g.score} />
                      </td>
                      <td className="px-3 py-2 text-center text-gray-500">{g.semester || '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="flex items-center gap-2 rounded-md bg-slate-50 py-4 text-sm text-gray-500">
              <AlertCircle size={16} />
              Оценки не найдены
            </div>
          )}
        </Card>

        {/* Допуск к ГИА */}
        <Card>
          <div className="mb-3 flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-100 text-emerald-600">
              <ClipboardCheck size={18} />
            </div>
            <h3 className="font-semibold text-[var(--color-text)]">Допуск к ГИА</h3>
          </div>
          {admissionLoading ? (
            <div className="flex items-center gap-2 text-sm text-gray-500">
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-emerald-400 border-t-transparent" />
              Загрузка...
            </div>
          ) : admission ? (
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                {admission.eligible ? (
                  <>
                    <div className="flex h-12 w-12 items-center justify-center rounded-full bg-green-100 text-green-600">
                      <CheckCircle2 size={24} />
                    </div>
                    <div>
                      <Badge variant="success">Допущен к ГИА</Badge>
                      <p className="mt-0.5 text-xs text-green-700">Все требования выполнены</p>
                    </div>
                  </>
                ) : (
                  <>
                    <div className="flex h-12 w-12 items-center justify-center rounded-full bg-red-100 text-red-600">
                      <XCircle size={24} />
                    </div>
                    <div>
                      <Badge variant="danger">Не допущен к ГИА</Badge>
                      <p className="mt-0.5 text-xs text-red-700">Есть невыполненные требования</p>
                    </div>
                  </>
                )}
              </div>

              {admission.brsScore !== undefined && admission.brsScore !== null && (
                <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2 text-sm">
                  <Award size={16} className="text-[var(--color-primary)]" />
                  <span className="text-[var(--color-text-muted)]">Баллы БРС:</span>
                  <span className="font-semibold text-[var(--color-text)]">{admission.brsScore}</span>
                </div>
              )}

              {admission.hasDebt && (
                <div className="flex items-center gap-2 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
                  <AlertCircle size={16} />
                  Есть академические задолженности
                </div>
              )}

              {admission.reason && (
                <div className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-800">
                  <span className="font-medium">Причина:</span> {admission.reason}
                </div>
              )}
            </div>
          ) : (
            <div className="flex items-center gap-2 rounded-md bg-slate-50 py-4 text-sm text-gray-500">
              <AlertCircle size={16} />
              Данные о допуске не найдены
            </div>
          )}
        </Card>

        {/* Моя ВКР */}
        <Card>
          <div className="mb-3 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-purple-100 text-purple-600">
                <FileText size={18} />
              </div>
              <h3 className="font-semibold text-[var(--color-text)]">Моя ВКР</h3>
            </div>
            {!isEditingProfile ? (
              <Button size="sm" variant="ghost" onClick={startEditing}>
                Редактировать
              </Button>
            ) : (
              <div className="flex gap-2">
                <Button size="sm" variant="secondary" onClick={() => setIsEditingProfile(false)}>
                  Отмена
                </Button>
                <Button size="sm" onClick={saveProfile} disabled={updateProfile.isPending}>
                  {updateProfile.isPending ? 'Сохранение...' : 'Сохранить'}
                </Button>
              </div>
            )}
          </div>

          {isEditingProfile ? (
            <div className="space-y-3 text-sm">
              <div>
                <label className="mb-1 block text-xs font-medium text-[var(--color-text-muted)]">Тема ВКР</label>
                <input
                  type="text"
                  className="w-full rounded-md border border-[var(--color-border)] bg-white px-3 py-2 text-sm text-[var(--color-text)] focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
                  value={editThesisTopic}
                  onChange={(e) => setEditThesisTopic(e.target.value)}
                  placeholder="Введите тему ВКР"
                />
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-[var(--color-text-muted)]">Руководитель</label>
                <input
                  type="text"
                  className="w-full rounded-md border border-[var(--color-border)] bg-white px-3 py-2 text-sm text-[var(--color-text)] focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
                  value={editSupervisor}
                  onChange={(e) => setEditSupervisor(e.target.value)}
                  placeholder="ФИО руководителя"
                />
              </div>
            </div>
          ) : (
            <div className="space-y-2 text-sm">
              <div className="flex items-start gap-2 rounded-md bg-slate-50 px-3 py-2">
                <FileText size={16} className="mt-0.5 shrink-0 text-[var(--color-primary)]" />
                <div>
                  <span className="text-[var(--color-text-muted)]">Тема:</span>
                  <p className="font-medium text-[var(--color-text)]">{profile.thesisTopic || 'Не указана'}</p>
                </div>
              </div>
              <div className="flex items-start gap-2 rounded-md bg-slate-50 px-3 py-2">
                <User size={16} className="mt-0.5 shrink-0 text-[var(--color-primary)]" />
                <div>
                  <span className="text-[var(--color-text-muted)]">Руководитель:</span>
                  <p className="font-medium text-[var(--color-text)]">{profile.supervisorName || 'Не назначен'}</p>
                </div>
              </div>
            </div>
          )}

          <div className="flex items-center gap-2 pt-3">
            {profile.thesisFilePath ? (
              <>
                <Badge variant="success">
                  <CheckCircle2 size={12} className="mr-1 inline" />
                  Файл загружен
                </Badge>
                <Button size="sm" variant="ghost" onClick={handleDownloadThesis}>
                  <Download size={14} className="mr-1" />
                  Скачать
                </Button>
              </>
            ) : (
              <Badge variant="warning">
                <AlertCircle size={12} className="mr-1 inline" />
                Файл не загружен
              </Badge>
            )}
            <Button
              size="sm"
              variant="secondary"
              onClick={() => fileInputRef.current?.click()}
              disabled={uploadThesis.isPending}
            >
              <Upload size={14} className="mr-1" />
              {uploadThesis.isPending ? 'Загрузка...' : 'Загрузить'}
            </Button>
            <input
              ref={fileInputRef}
              type="file"
              className="hidden"
              accept=".pdf,.doc,.docx,.zip"
              onChange={handleFileSelect}
            />
          </div>
        </Card>

        {/* Заседание ГЭК */}
        <Card>
          <div className="mb-3 flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-orange-100 text-orange-600">
              <CalendarDays size={18} />
            </div>
            <h3 className="font-semibold text-[var(--color-text)]">Заседание ГЭК</h3>
          </div>
          {meetingLoading ? (
            <div className="flex items-center gap-2 text-sm text-gray-500">
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-orange-400 border-t-transparent" />
              Загрузка...
            </div>
          ) : meetingInfo ? (
            <div className="space-y-2">
              <div className="flex items-center gap-3 rounded-md bg-slate-50 px-3 py-2">
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-orange-100 text-orange-600">
                  <CalendarDays size={18} />
                </div>
                <div>
                  <div className="text-lg font-bold text-[var(--color-text)]">
                    {new Date(meetingInfo.meetingDate).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })}
                  </div>
                  <div className="text-xs text-[var(--color-text-muted)]">
                    {new Date(meetingInfo.meetingDate).toLocaleDateString('ru-RU', { weekday: 'long' })}
                  </div>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2">
                  <Clock size={16} className="text-[var(--color-primary)]" />
                  <span className="text-[var(--color-text-muted)]">Время:</span>
                  <span className="font-medium">{meetingInfo.startTime || '—'} — {meetingInfo.endTime || '—'}</span>
                </div>
                <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2">
                  <MapPin size={16} className="text-[var(--color-primary)]" />
                  <span className="text-[var(--color-text-muted)]">Место:</span>
                  <span className="font-medium">{meetingInfo.location || '—'}</span>
                </div>
                <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2">
                  <Building2 size={16} className="text-[var(--color-primary)]" />
                  <span className="text-[var(--color-text-muted)]">ГЭК:</span>
                  <span className="font-medium">{meetingInfo.gekName || '—'}</span>
                </div>
                <div className="flex items-center gap-2 rounded-md bg-slate-50 px-3 py-2">
                  <Hash size={16} className="text-[var(--color-primary)]" />
                  <span className="text-[var(--color-text-muted)]">Номер:</span>
                  <span className="font-medium">{meetingInfo.orderNumber || '—'}</span>
                </div>
              </div>
            </div>
          ) : (
            <div className="flex items-center gap-2 rounded-md bg-slate-50 py-4 text-sm text-gray-500">
              <AlertCircle size={16} />
              Информация о заседании не найдена
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};
