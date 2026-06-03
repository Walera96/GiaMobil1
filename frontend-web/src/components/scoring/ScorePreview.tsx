import React, { useState, useMemo } from 'react';
import { useScoringStore } from '../../store/scoringStore';
import { Input } from '../ui/Input';
import { Progress } from '../ui/Progress';
import { Badge } from '../ui/Badge';
import { Calculator, CheckCircle2 } from 'lucide-react';

/** Определяет итоговую оценку по баллам и порогам */
function calculateGrade(
  score: number,
  thresholds: { excellent?: number; good?: number; satisfactory?: number; pass?: number }
): { label: string; variant: 'success' | 'warning' | 'danger' | 'info' | 'default' } {
  const { excellent = 90, good = 75, satisfactory = 60, pass = 50 } = thresholds;
  if (score >= excellent) return { label: 'Отлично (5)', variant: 'success' };
  if (score >= good) return { label: 'Хорошо (4)', variant: 'info' };
  if (score >= satisfactory) return { label: 'Удовлетворительно (3)', variant: 'warning' };
  if (score >= pass) return { label: 'Зачёт (2)', variant: 'default' };
  return { label: 'Не зачтено', variant: 'danger' };
}

export const ScorePreview: React.FC = () => {
  const { config } = useScoringStore();
  const criteria = config.criteria || [];
  const [scores, setScores] = useState<Record<number, number>>({});

  // Итоговый расчёт: Σ(балл_студента / макс_балл * вес)
  const totalScore = useMemo(() => {
    return criteria.reduce((sum, c, i) => {
      const studentScore = scores[i] || 0;
      const maxPoints = c.maxPoints || 1;
      const weight = c.weight || 0;
      return sum + (studentScore / maxPoints) * weight;
    }, 0);
  }, [criteria, scores]);

  const grade = calculateGrade(Math.round(totalScore), config.thresholds || {});

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        <Calculator size={20} className="text-[var(--color-primary)]" />
        <h3 className="text-lg font-semibold text-[var(--color-text)]">Предпросмотр расчёта</h3>
      </div>

      <p className="text-sm text-[var(--color-text-muted)]">
        Введите баллы студента по каждому критерию, чтобы увидеть итоговую оценку.
      </p>

      {/* Поля ввода баллов */}
      <div className="space-y-3">
        {criteria.map((c, i) => {
          const studentScore = scores[i] || 0;
          const maxPoints = c.maxPoints || 1;
          const percent = Math.min((studentScore / maxPoints) * 100, 100);

          return (
            <div key={i} className="rounded-lg border border-[var(--color-border)] bg-white p-3 space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="font-medium text-[var(--color-text)]">{c.name}</span>
                <span className="text-[var(--color-text-muted)]">
                  {studentScore} / {maxPoints}
                </span>
              </div>
              <div className="flex items-center gap-3">
                <Input
                  type="number"
                  min={0}
                  max={maxPoints}
                  value={studentScore}
                  onChange={(e) =>
                    setScores((prev) => ({ ...prev, [i]: Math.min(Number(e.target.value), maxPoints) }))
                  }
                  className="w-24 text-center"
                />
                <div className="flex-1">
                  <Progress value={percent} max={100} color="blue" size="sm" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Итоговый результат */}
      <div className="rounded-lg bg-[var(--color-bg)] p-4 space-y-3">
        <div className="flex items-center justify-between">
          <span className="text-sm text-[var(--color-text-muted)]">Итоговый балл</span>
          <span className="text-2xl font-bold text-[var(--color-text)]">
            {Math.round(totalScore)}
            <span className="text-sm font-normal text-[var(--color-text-muted)]"> / {config.maxTotalScore || 100}</span>
          </span>
        </div>

        <Progress
          value={Math.round(totalScore)}
          max={config.maxTotalScore || 100}
          color={grade.variant === 'success' ? 'green' : grade.variant === 'info' ? 'blue' : grade.variant === 'warning' ? 'amber' : 'red'}
          size="md"
          showLabel
        />

        <div className="flex items-center justify-between">
          <span className="text-sm text-[var(--color-text-muted)]">Оценка</span>
          <Badge variant={grade.variant} className="text-sm px-3 py-1">
            {grade.label}
          </Badge>
        </div>

        {/* Проходной балл */}
        {config.passingScore && (
          <div className="text-xs text-[var(--color-text-muted)]">
            Проходной балл: {config.passingScore}
            {Math.round(totalScore) >= config.passingScore ? (
              <span className="text-green-600 ml-1 flex items-center gap-1 inline-flex">
                <CheckCircle2 size={12} />
                Зачтено
              </span>
            ) : (
              <span className="text-red-600 ml-1">Не зачтено</span>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
