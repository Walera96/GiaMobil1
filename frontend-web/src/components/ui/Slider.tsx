import React from 'react';

interface SliderProps {
  label?: string;
  min?: number;
  max?: number;
  step?: number;
  value: number;
  onChange: (value: number) => void;
}

export const Slider: React.FC<SliderProps> = ({
  label,
  min = 0,
  max = 100,
  step = 1,
  value,
  onChange,
}) => {
  return (
    <div className="w-full">
      {label && (
        <div className="mb-2 flex items-center justify-between">
          <label className="text-sm font-medium text-[var(--color-text)]">{label}</label>
          <span className="text-sm font-semibold text-[var(--color-primary)]">{value}</span>
        </div>
      )}
      <input
        type="range"
        min={min}
        max={max}
        step={step}
        value={value}
        onChange={(e) => onChange(Number(e.target.value))}
        className="w-full accent-[var(--color-primary)]"
      />
      <div className="mt-1 flex justify-between text-xs text-[var(--color-text-muted)]">
        <span>{min}</span>
        <span>{max}</span>
      </div>
    </div>
  );
};
