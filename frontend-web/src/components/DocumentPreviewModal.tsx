import React from 'react';
import { Modal } from './ui/Modal';
import { Button } from './ui/Button';
import { Download, X, FileType, FileSpreadsheet } from 'lucide-react';

interface DocumentPreviewModalProps {
  isOpen: boolean;
  onClose: () => void;
  blob: Blob | null;
  filename: string;
  mimeType: string;
}

export const DocumentPreviewModal: React.FC<DocumentPreviewModalProps> = ({
  isOpen,
  onClose,
  blob,
  filename,
  mimeType,
}) => {
  const handleDownload = () => {
    if (!blob) return;
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  };

  const isPdf = mimeType.includes('pdf');
  const isDocx = mimeType.includes('word') || filename.endsWith('.docx');
  const isExcel = mimeType.includes('excel') || mimeType.includes('sheet') || filename.endsWith('.xls') || filename.endsWith('.xlsx');

  const objectUrl = blob ? window.URL.createObjectURL(blob) : null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Предпросмотр: ${filename}`}>
      <div className="space-y-4">
        {isPdf && objectUrl && (
          <div className="w-full h-[70vh] border rounded-lg overflow-hidden">
            <iframe src={objectUrl} className="w-full h-full" title={filename} />
          </div>
        )}

        {(isDocx || isExcel) && (
          <div className="text-center py-12 space-y-4">
            <div className="flex justify-center">
              {isDocx ? (
                <FileType size={64} className="text-blue-500" />
              ) : (
                <FileSpreadsheet size={64} className="text-green-500" />
              )}
            </div>
            <p className="text-gray-600">
              Предпросмотр {isDocx ? 'DOCX' : 'Excel'} недоступен в браузере
            </p>
            <p className="text-sm text-gray-500">
              Скачайте файл для просмотра в Microsoft Word / Excel
            </p>
          </div>
        )}

        {!isPdf && !isDocx && !isExcel && objectUrl && (
          <div className="w-full h-[70vh] border rounded-lg overflow-hidden">
            <iframe src={objectUrl} className="w-full h-full" title={filename} />
          </div>
        )}

        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={onClose}>
            <X size={16} className="mr-1" /> Закрыть
          </Button>
          <Button onClick={handleDownload}>
            <Download size={16} className="mr-1" /> Скачать
          </Button>
        </div>
      </div>
    </Modal>
  );
};
