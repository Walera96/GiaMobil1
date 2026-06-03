import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useScoringStore, scoringTemplates } from '../../store/scoringStore';
import { ScoringCriteriaEditor } from '../../components/scoring/ScoringCriteriaEditor';
import { GradeThresholdsEditor } from '../../components/scoring/GradeThresholdsEditor';
import { ScorePreview } from '../../components/scoring/ScorePreview';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Select } from '../../components/ui/Select';
import { Slider } from '../../components/ui/Slider';
import { Input } from '../../components/ui/Input';
import { Tabs } from '../../components/ui/Tabs';
import { ArrowLeft, Save, RotateCcw, AlertCircle } from 'lucide-react';

const templateOptions = [
  { value: '', label: 'Выберите шаблон...' },
  { value: 'standard', label: 'Стандартный (5 критериев)' },
  { value: 'exam', label: 'Экзамен (3 критерия)' },
  { value: 'project', label: 'Проектный (5 критериев)' },
];

const typeTabs = [
  { id: 'weighted', label: 'Взвешенный' },
  { id: 'criteria', label: 'По критериям' },
  { id: 'pass_fail', label: 'Зачёт/незачёт' },
  { id: 'exam_5point', label: 'Экзамен 5-балл' },
];

export const TeacherScoringConfigPage: React.FC = () => {
  const navigate = useNavigate();
  const {
    config,
    setConfig,
    setType,
    setMaxTotalScore,
    setPassingScore,
    setAllowRetake,
    isValid,
    reset,
  } = useScoringStore();

  const valid = isValid();

  const handleTemplateChange = (value: string) => {
    if (value && scoringTemplates[value]) {
      setConfig({ ...scoringTemplates[value] });
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      {/* Шапка */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate(-1)}
            className="rounded-md p-2 text-[var(--color-text-muted)] hover:bg-[var(--color-bg)] transition-colors"
            aria-label="Назад"
          >
            <ArrowLeft size={20} />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-[var(--color-text)]">Конфигурация оценивания</h1>
            <p className="text-sm text-[var(--color-text-muted)]">
              Настройте веса, пороги и критерии оценки заданий
            </p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          {!valid && (
            <div className="flex items-center gap-1 text-sm text-[var(--color-danger)]">
              <AlertCircle size={16} />
              Проверьте конфигурацию
            </div>
          )}
          <Button variant="ghost" onClick={reset}>
            <RotateCcw size={16} className="mr-2" />
            Сбросить
          </Button>
          <Button disabled={!valid}>
            <Save size={16} className="mr-2" />
            Сохранить
          </Button>
        </div>
      </div>

      {/* Быстрый выбор шаблона */}
      <Card className="p-4">
        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium text-[var(--color-text)] mb-1">
              Шаблон конфигурации
            </label>
            <Select
              options={templateOptions}
              value=""
              onChange={(e) => handleTemplateChange(e.target.value)}
            />
          </div>
          <div className="flex items-end">
            <div className="text-xs text-[var(--color-text-muted)]">
              Шаблон можно отредактировать после выбора
            </div>
          </div>
        </div>
      </Card>

      {/* Основные настройки */}
      <Card className="p-4 space-y-4">
        <h2 className="text-lg font-semibold text-[var(--color-text)]">Основные параметры</h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Тип оценивания */}
          <div>
            <label className="block text-sm font-medium text-[var(--color-text)] mb-2">
              Тип системы оценивания
            </label>
            <Tabs tabs={typeTabs} activeTab={config.type || 'weighted'} onChange={(id) => setType(id)} />
          </div>

          {/* Макс. балл + проходной */}
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-[var(--color-text)] mb-1">
                Максимальный балл: {config.maxTotalScore}
              </label>
              <Slider
                min={10}
                max={100}
                step={5}
                value={config.maxTotalScore || 100}
                onChange={setMaxTotalScore}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-[var(--color-text)] mb-1">
                Проходной балл
              </label>
              <Input
                type="number"
                min={0}
                max={config.maxTotalScore || 100}
                value={config.passingScore || 60}
                onChange={(e) => setPassingScore(Number(e.target.value))}
                className="w-32"
              />
            </div>
          </div>
        </div>

        {/* Пересдачи */}
        <label className="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            checked={config.allowRetake}
            onChange={(e) => setAllowRetake(e.target.checked)}
            className="h-4 w-4 rounded border-gray-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
          />
          <span className="text-sm text-[var(--color-text)]">Разрешить пересдачу</span>
          {config.allowRetake && (
            <span className="text-xs text-[var(--color-text-muted)]">
              (макс. 70% от полного балла при пересдаче)
            </span>
          )}
        </label>
      </Card>

      {/* Двухколоночный layout: критерии + пороги | предпросмотр */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Левая колонка */}
        <div className="space-y-6">
          <Card className="p-4">
            <ScoringCriteriaEditor />
          </Card>
          <Card className="p-4">
            <GradeThresholdsEditor />
          </Card>
        </div>

        {/* Правая колонка — предпросмотр */}
        <div>
          <Card className="p-4 sticky top-4">
            <ScorePreview />
          </Card>
        </div>
      </div>
    </div>
  );
};
