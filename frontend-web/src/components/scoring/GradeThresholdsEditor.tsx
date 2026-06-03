import React from 'react';
import { useScoringStore } from '../../store/scoringStore';
import { Input } from '../ui/Input';
import { AlertCircle, CheckCircle2 } from 'lucide-react';

/** Проверяет, нет ли пересечений в порогах */
function validateThresholds(t: {
  excellent?: number;
  good?: number;
  satisfactory?: number;
  pass?: number;
}): { valid: boolean; message?: string } {
  const { excellent = 100, good = 0, satisfactory = 0, pass = 0 } = t;
  if (excellent <= good) return { valid: false, message: '«Отлично» должно быть выше «Хорошо»' };
  if (good <= satisfactory) return { valid: false, message: '«Хорошо» должно быть выше «Удовл»' };
  if (satisfactory <= pass) return { valid: false, message: '«Удовл» должно быть выше «Зачёт»' };
  if (pass < 0) return { valid: false, message: '«Зачёт» не может быть отрицательным' };
  return { valid: true };
}

const gradeLabels: Array<{ key: 'excellent' | 'good' | 'satisfactory' | 'pass'; label: string; color: string }> = [
  { key: 'excellent', label: 'Отлично (5)', color: 'bg-green-100 text-green-800 border-green-200' },
  { key: 'good', label: 'Хорошо (4)', color: 'bg-blue-100 text-blue-800 border-blue-200' },
  { key: 'satisfactory', label: 'Удовлетворительно (3)', color: 'bg-amber-100 text-amber-800 border-amber-200' },
  { key: 'pass', label: 'Зачёт (2)', color: 'bg-gray-100 text-gray-700 border-gray-200' },
];

export const GradeThresholdsEditor: React.FC = () => {
  const { config, updateThresholds } = useScoringStore();
  const thresholds = config.thresholds || {};
  const validation = validateThresholds(thresholds);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-[var(--color-text)]">Пороги оценок</h3>
        {validation.valid ? (
          <div className="flex items-center gap-1 text-sm text-green-600">
            <CheckCircle2 size={16} />
            Пороги корректны
          </div>
        ) : (
          <div className="flex items-center gap-1 text-sm text-[var(--color-danger)]">
            <AlertCircle size={16} />
            {validation.message}
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        {gradeLabels.map((g) => (
          <div
            key={g.key}
            className={[
              'rounded-lg border p-3 space-y-2',
              g.color,
            ].join(' ')}
          >
            <div className="text-sm font-medium">{g.label}</div>
            <div className="flex items-center gap-2">
              <span className="text-xs">от</span>
              <Input
                type="number"
                min={0}
                max={100}
                value={thresholds[g.key] ?? ''}
                onChange={(e) => {
                  const val = e.target.value === '' ? undefined : Number(e.target.value);
                  updateThresholds(g.key, val);
                }}
                className="w-20 text-center bg-white/80"
              />
              <span className="text-xs">баллов</span>
            </div>
          </div>
        ))}
      </div>

      {/* Визуальная шкала порогов */}
      <div className="relative h-8 rounded-full bg-gray-100 overflow-hidden flex">
        {/* Отлично */}
        <div
          className="h-full bg-green-400 flex items-center justify-center text-[10px] text-white font-medium"
          style={{ width: `${100 - (thresholds.excellent || 90)}%` }}
        >
          {100 - (thresholds.excellent || 90) > 8 ? '5' : ''}
        </div>
        {/* Хорошо */}
        <div
          className="h-full bg-blue-400 flex items-center justify-center text-[10px] text-white font-medium"
          style={{ width: `${(thresholds.excellent || 90) - (thresholds.good || 75)}%` }}
        >
          {(thresholds.excellent || 90) - (thresholds.good || 75) > 8 ? '4' : ''}
        </div>
        {/* Удовл */}
        <div
          className="h-full bg-amber-400 flex items-center justify-center text-[10px] text-white font-medium"
          style={{ width: `${(thresholds.good || 75) - (thresholds.satisfactory || 60)}%` }}
        >
          {(thresholds.good || 75) - (thresholds.satisfactory || 60) > 8 ? '3' : ''}
        </div>
        {/* Зачёт */}
        <div
          className="h-full bg-gray-400 flex items-center justify-center text-[10px] text-white font-medium"
          style={{ width: `${(thresholds.satisfactory || 60) - (thresholds.pass || 50)}%` }}
        >
          {(thresholds.satisfactory || 60) - (thresholds.pass || 50) > 8 ? '2' : ''}
        </div>
        {/* Не зачёт */}
        <div
          className="h-full bg-red-300 flex items-center justify-center text-[10px] text-white font-medium"
          style={{ width: `${thresholds.pass || 50}%` }}
        >
          {(thresholds.pass || 50) > 8 ? 'н/з' : ''}
        </div>
      </div>
    </div>
  );
};
