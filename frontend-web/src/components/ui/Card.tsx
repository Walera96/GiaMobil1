import React from 'react';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export const Card: React.FC<CardProps> = ({ children, className = '', ...props }) => {
  return (
    <div
      className={[
        'rounded-lg bg-white p-4 shadow-sm',
        'border border-[var(--color-border)]',
        className,
      ].join(' ')}
      {...props}
    >
      {children}
    </div>
  );
};
