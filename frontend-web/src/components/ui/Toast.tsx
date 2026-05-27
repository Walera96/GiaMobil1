import React, { useEffect } from 'react';
import { X, CheckCircle, AlertCircle, AlertTriangle, Info } from 'lucide-react';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastItem {
  id: string;
  message: string;
  type: ToastType;
}

interface ToastContainerProps {
  toasts: ToastItem[];
  onRemove: (id: string) => void;
}

const icons = {
  success: CheckCircle,
  error: AlertCircle,
  warning: AlertTriangle,
  info: Info,
};

const styles = {
  success: 'bg-green-50 text-green-800 border-green-200',
  error: 'bg-red-50 text-red-800 border-red-200',
  warning: 'bg-amber-50 text-amber-800 border-amber-200',
  info: 'bg-blue-50 text-blue-800 border-blue-200',
};

export const ToastContainer: React.FC<ToastContainerProps> = ({ toasts, onRemove }) => {
  return (
    <div className="fixed right-4 top-4 z-50 flex flex-col gap-2">
      {toasts.map((toast) => {
        const Icon = icons[toast.type];
        return (
          <Toast key={toast.id} toast={toast} onRemove={onRemove} icon={<Icon size={18} />} style={styles[toast.type]} />
        );
      })}
    </div>
  );
};

const Toast: React.FC<{
  toast: ToastItem;
  onRemove: (id: string) => void;
  icon: React.ReactNode;
  style: string;
}> = ({ toast, onRemove, icon, style }) => {
  useEffect(() => {
    const timer = setTimeout(() => onRemove(toast.id), 4000);
    return () => clearTimeout(timer);
  }, [toast.id, onRemove]);

  return (
    <div
      className={[
        'flex w-80 items-start gap-3 rounded-lg border p-4 shadow-sm',
        style,
      ].join(' ')}
    >
      {icon}
      <p className="flex-1 text-sm">{toast.message}</p>
      <button onClick={() => onRemove(toast.id)} className="opacity-70 hover:opacity-100">
        <X size={14} />
      </button>
    </div>
  );
};
