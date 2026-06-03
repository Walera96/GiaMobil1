import React from 'react';
import { useScoringStore } from '../../store/scoringStore';
import { Input } from '../ui/Input';
import { Slider } from '../ui/Slider';
import { Button } from '../ui/Button';
import { X, GripVertical, AlertCircle } from 'lucide-react';

export const ScoringCriteriaEditor: React.FC = () => {
  const { config, updateCriteria, addCriterion, removeCriterion, totalWeight } = useScoringStore();
  const criteria = config.criteria || [];
  const weightSum = totalWeight();
  const isValidWeight = weightSum === 100;

  return (
    <div className="space-y-4">
      {/* Заголовок + валидация весов */}
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-[var(--color-text)]">Критерии оценивания</h3>
        <div className="flex items-center gap-2">
          {!isValidWeight && (
            <div className="flex items-center gap-1 text-sm text-[var(--color-danger)]">
              <AlertCircle size={16} />
              <span>Сумма весов: {weightSum}% (нужно 100%)</span>
            </div>
          )}
          {isValidWeight && (
            <span className="text-sm text-green-600 font-medium">Сумма весов: 100%</span>
          )}
        </div>
      </div>

      {/* Список критериев */}
      <div className="space-y-3">
        {criteria.map((criterion, index) => (
          <div
            key={index}
            className="flex items-start gap-3 rounded-lg border border-[var(--color-border)] bg-white p-3"
          >
            {/* Drag handle (визуальный) */}
            <div className="mt-2 text-gray-300">
              <GripVertical size={16} />
            </div>

            {/* Поля критерия */}
            <div className="flex-1 space-y-3">
              <div className="flex flex-col sm:flex-row gap-3">
                {/* Название */}
                <div className="flex-1">
                  <Input
                    value={criterion.name || ''}
                    onChange={(e) => updateCriteria(index, 'name', e.target.value)}
                    placeholder="Название критерия"
                  />
                </div>
                {/* Макс. балл */}
                <div className="w-full sm:w-24">
                  <Input
                    type="number"
                    min={0}
                    value={criterion.maxPoints || 0}
                    onChange={(e) => updateCriteria(index, 'maxPoints', Number(e.target.value))}
                    placeholder="Макс."
                  />
                </div>
              </div>

              {/* Вес — слайдер + инпут */}
              <div className="flex items-center gap-4">
                <div className="flex-1">
                  <Slider
                    label={`Вес: ${criterion.weight || 0}%`}
                    min={0}
                    max={100}
                    step={1}
                    value={criterion.weight || 0}
                    onChange={(v) => updateCriteria(index, 'weight', v)}
                  />
                </div>
                <Input
                  type="number"
                  min={0}
                  max={100}
                  value={criterion.weight || 0}
                  onChange={(e) => updateCriteria(index, 'weight', Number(e.target.value))}
                  className="w-16 text-center"
                />
              </div>
            </div>

            {/* Удалить */}
            <button
              onClick={() => removeCriterion(index)}
              className="mt-2 rounded-md p-1 text-gray-400 hover:bg-red-50 hover:text-red-600 transition-colors"
              aria-label="Удалить критерий"
            >
              <X size={16} />
            </button>
          </div>
        ))}
      </div>

      {/* Кнопка добавить */}
      <Button variant="ghost" onClick={addCriterion} className="w-full">
        + Добавить критерий
      </Button>
    </div>
  );
};
