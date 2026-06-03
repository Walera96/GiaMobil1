import React from 'react';

interface ProgressProps {
  value: number;        // Текущее значение (0–100)
  max?: number;         // Максимум (по умолчанию 100)
  size?: 'sm' | 'md' | 'lg';
  color?: 'blue' | 'green' | 'amber' | 'red';
  showLabel?: boolean;  // Показывать процент справа
}

const sizeClasses = {
  sm: 'h-1.5',
  md: 'h-2.5',
  lg: 'h-4',
};

const colorClasses = {
  blue: 'bg-blue-500',
  green: 'bg-green-500',
  amber: 'bg-amber-500',
  red: 'bg-red-500',
};

export const Progress: React.FC<ProgressProps> = ({
  value,
  max = 100,
  size = 'md',
  color = 'blue',
  showLabel = false,
}) => {
  const percent = Math.min(Math.max((value / max) * 100, 0), 100);

  return (
    <div className="flex items-center gap-3">
      <div className={['w-full rounded-full bg-gray-100 overflow-hidden', sizeClasses[size]].join(' ')}>
        <div
          className={['h-full rounded-full transition-all duration-500', colorClasses[color]].join(' ')}
          style={{ width: `${percent}%` }}
        />
      </div>
      {showLabel && (
        <span className="text-xs font-medium text-[var(--color-text-muted)] whitespace-nowrap">
          {Math.round(percent)}%
        </span>
      )}
    </div>
  );
};
