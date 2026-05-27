import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: React.ReactNode;
  children: React.ReactNode;
  size?: 'sm' | 'md' | 'lg' | 'xl';
}

export const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children, size = 'md' }) => {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const sizeClasses = {
    sm: 'max-w-sm',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-5xl',
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/50" onClick={onClose} />
      <div
        className={[
          'relative z-10 w-full rounded-xl bg-white p-6 shadow-xl',
          sizeClasses[size],
        ].join(' ')}
      >
        <div className="mb-4 flex items-center justify-between">
          {title && <h3 className="text-lg font-semibold text-[var(--color-text)]">{title}</h3>}
          <button
            onClick={onClose}
            className="rounded-md p-1 text-[var(--color-secondary)] hover:bg-[var(--color-bg)]"
          >
            <X size={20} />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
};
