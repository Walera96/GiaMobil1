import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useStatement, useUpdateStatementRecord } from '../hooks/useStatements';

import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { ArrowLeft, Save, FileDown, FileSpreadsheet, FileType2, Eye } from 'lucide-react';
import type { StatementRecordDto } from '../api/statements';
import { vedomostApi, downloadBlob } from '../api/vedomost';
import type { VedomostDto } from '../api/vedomost';
import { DocumentPreviewModal } from '../components/DocumentPreviewModal';

const getGradeColor = (percent: number) => {
  if (percent >= 85) return 'bg-emerald-100 text-emerald-800';
  if (percent >= 70) return 'bg-blue-100 text-blue-800';
  if (percent >= 60) return 'bg-amber-100 text-amber-800';
  return 'bg-red-100 text-red-800';
};

export const StatementEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: statement, isLoading } = useStatement(id || '');
  const updateRecordMutation = useUpdateStatementRecord();

  const [editMode, setEditMode] = useState(false);
  const [localRecords, setLocalRecords] = useState<StatementRecordDto[]>([]);
  const [selectedRecordIds, setSelectedRecordIds] = useState<Set<string>>(new Set());
  const [preview, setPreview] = useState<{ blob: Blob; filename: string; mimeType: string } | null>(null);

  useEffect(() => {
    if (statement?.records) {
      setLocalRecords(statement.records);
      setSelectedRecordIds(new Set());
    }
  }, [statement]);

  const handleRecordChange = (recordId: string, field: keyof StatementRecordDto, value: number) => {
    setLocalRecords((prev) =>
      prev.map((r) => {
        if (r.id !== recordId) return r;
        const updated = { ...r, [field]: value };
        const total = (updated.currentControl || 0) + (updated.attendance || 0) + (updated.activity || 0) + (updated.examScore || 0);
        updated.totalScore = total;
        const percent = Math.round((total / 130) * 100);
        if (percent >= 85) updated.fivePointGrade = 5;
        else if (percent >= 70) updated.fivePointGrade = 4;
        else if (percent >= 60) updated.fivePointGrade = 3;
        else updated.fivePointGrade = 2;
        if (percent >= 90) updated.ectsGrade = 'A';
        else if (percent >= 82) updated.ectsGrade = 'B';
        else if (percent >= 74) updated.ectsGrade = 'C';
        else if (percent >= 64) updated.ectsGrade = 'D';
        else if (percent >= 60) updated.ectsGrade = 'E';
        else updated.ectsGrade = 'F';
        return updated;
      })
    );
  };

  const saveRecord = (record: StatementRecordDto) => {
    updateRecordMutation.mutate({
      statementId: id!,
      recordId: record.id,
      data: record,
    });
  };

  const getPercent = (total: number | undefined) => {
    if (!total) return 0;
    return Math.round((total / 130) * 100);
  };

  const buildVedomostDto = (): VedomostDto => {
    const records = selectedRecordIds.size > 0
      ? localRecords.filter((r) => selectedRecordIds.has(r.id))
      : localRecords;
    return {
      documentNumber: statement?.statementNumber,
      academicYear: statement?.academicYear,
      directionCode: '09.03.03',
      directionName: 'Прикладная информатика',
      directionShort: 'ПИ',
      department: statement?.disciplineName,
      giaForm: 'Выполнение и защита выпускной квалификационной работы',
      course: 4,
      groupName: statement?.groupName,
      date: new Date().toISOString().split('T')[0],
      instituteName: 'Институт управления и информационных технологий',
      directorName: '',
      chairmanName: '',
      chairmanDegree: '',
      totalStudents: records.length,
      countZachteno: 0,
      countNeZachteno: 0,
      countOtlichno: records.filter((r) => r.fivePointGrade === 5).length,
      countHorosho: records.filter((r) => r.fivePointGrade === 4).length,
      countUdov: records.filter((r) => r.fivePointGrade === 3).length,
      countNeud: records.filter((r) => r.fivePointGrade === 2).length,
      countAbsent: 0,
      students: records.map((r, i) => ({
        seqNumber: i + 1,
        fullName: r.studentName,
        recordBookNumber: r.recordBookNumber,
        scorePoints: r.totalScore,
        scoreClassic: r.fivePointGrade?.toString(),
      })),
      committeeMembers: [],
    };
  };

  const handleDownloadDocx = async () => {
    try {
      const { blob, filename } = await vedomostApi.generateWord(buildVedomostDto());
      downloadBlob(blob, filename);
    } catch (e: any) {
      console.error(e);
      alert('Ошибка генерации DOCX: ' + (e?.message || 'Неизвестная ошибка'));
    }
  };

  const handleDownloadExcel = async () => {
    try {
      const { blob, filename } = await vedomostApi.generateExcel(buildVedomostDto());
      downloadBlob(blob, filename);
    } catch (e: any) {
      console.error(e);
      alert('Ошибка генерации Excel: ' + (e?.message || 'Неизвестная ошибка'));
    }
  };

  const handleDownloadPdf = async () => {
    try {
      const { blob, filename } = await vedomostApi.generatePdf(buildVedomostDto());
      downloadBlob(blob, filename);
    } catch (e: any) {
      console.error(e);
      alert('Ошибка генерации PDF: ' + (e?.message || 'Неизвестная ошибка'));
    }
  };

  const handlePreview = async (format: 'pdf' | 'word' | 'excel') => {
    try {
      const dto = buildVedomostDto();
      if (format === 'pdf') {
        const { blob, filename } = await vedomostApi.generatePdf(dto);
        setPreview({ blob, filename, mimeType: 'application/pdf' });
      } else if (format === 'word') {
        const { blob, filename } = await vedomostApi.generateWord(dto);
        setPreview({ blob, filename, mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
      } else {
        const { blob, filename } = await vedomostApi.generateExcel(dto);
        setPreview({ blob, filename, mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      }
    } catch (e: any) {
      console.error(e);
      alert('Ошибка генерации предпросмотра: ' + (e?.message || 'Неизвестная ошибка'));
    }
  };

  const toggleSelectAll = () => {
    if (selectedRecordIds.size === localRecords.length) {
      setSelectedRecordIds(new Set());
    } else {
      setSelectedRecordIds(new Set(localRecords.map((r) => r.id)));
    }
  };

  const toggleSelectRecord = (recordId: string) => {
    setSelectedRecordIds((prev) => {
      const next = new Set(prev);
      if (next.has(recordId)) next.delete(recordId);
      else next.add(recordId);
      return next;
    });
  };

  if (isLoading) return <div className="text-center py-12 text-gray-500">Загрузка...</div>;
  if (!statement) return <div className="text-center py-12 text-gray-500">Ведомость не найдена</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate('/statements')} className="p-2 hover:bg-gray-100 rounded-md">
            <ArrowLeft size={20} />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-[var(--color-text)]">Ведомость №{statement.statementNumber || '—'}</h1>
            <p className="text-sm text-gray-600">{statement.groupName} · {statement.disciplineName} · {statement.academicYear}</p>
          </div>
        </div>
        <div className="flex gap-2 items-center">
          {selectedRecordIds.size > 0 && (
            <span className="text-sm text-gray-600 mr-2">
              Выбрано: {selectedRecordIds.size} / {localRecords.length}
            </span>
          )}
          <Button variant={editMode ? 'primary' : 'ghost'} onClick={() => setEditMode(!editMode)}>
            {editMode ? 'Готово' : 'Редактировать'}
          </Button>
          <Button variant="ghost" className="flex items-center gap-2" onClick={handleDownloadDocx}>
            <FileType2 size={16} /> DOCX
          </Button>
          <Button variant="ghost" className="flex items-center gap-2" onClick={handleDownloadExcel}>
            <FileSpreadsheet size={16} /> Excel
          </Button>
          <Button variant="ghost" className="flex items-center gap-2" onClick={handleDownloadPdf}>
            <FileDown size={16} /> PDF
          </Button>
          <Button variant="secondary" className="flex items-center gap-2" onClick={() => handlePreview('pdf')}>
            <Eye size={16} /> Предпросмотр
          </Button>
        </div>
      </div>

      <Card className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">
                    <input
                      type="checkbox"
                      checked={localRecords.length > 0 && selectedRecordIds.size === localRecords.length}
                      onChange={toggleSelectAll}
                      className="w-4 h-4 cursor-pointer"
                    />
                  </th>
                  <th className="px-4 py-3 text-left font-medium text-gray-700">№</th>
                  <th className="px-4 py-3 text-left font-medium text-gray-700">Студент</th>
                  <th className="px-4 py-3 text-left font-medium text-gray-700">Зач. книжка</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">Текущий</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">Посещ.</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">Активн.</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">Экзамен</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">Итого</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">ECTS</th>
                  <th className="px-4 py-3 text-center font-medium text-gray-700">5-балл</th>
                  {editMode && <th className="px-4 py-3 text-center font-medium text-gray-700">Действия</th>}
                </tr>
              </thead>
              <tbody className="divide-y">
                {localRecords.map((record, index) => {
                  const percent = getPercent(record.totalScore);
                  return (
                    <tr key={record.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-center">
                        <input
                          type="checkbox"
                          checked={selectedRecordIds.has(record.id)}
                          onChange={() => toggleSelectRecord(record.id)}
                          className="w-4 h-4 cursor-pointer"
                        />
                      </td>
                      <td className="px-4 py-3 text-gray-500">{index + 1}</td>
                      <td className="px-4 py-3 font-medium">{record.studentName}</td>
                      <td className="px-4 py-3 text-gray-600">{record.recordBookNumber || '—'}</td>
                      <td className="px-4 py-3 text-center">
                        {editMode ? (
                          <Input
                            type="number"
                            min={0}
                            max={70}
                            className="w-16 text-center mx-auto"
                            value={record.currentControl || 0}
                            onChange={(e) => handleRecordChange(record.id, 'currentControl', parseInt(e.target.value) || 0)}
                          />
                        ) : (
                          record.currentControl ?? '—'
                        )}
                      </td>
                      <td className="px-4 py-3 text-center">
                        {editMode ? (
                          <Input
                            type="number"
                            min={0}
                            max={10}
                            className="w-16 text-center mx-auto"
                            value={record.attendance || 0}
                            onChange={(e) => handleRecordChange(record.id, 'attendance', parseInt(e.target.value) || 0)}
                          />
                        ) : (
                          record.attendance ?? '—'
                        )}
                      </td>
                      <td className="px-4 py-3 text-center">
                        {editMode ? (
                          <Input
                            type="number"
                            min={0}
                            max={20}
                            className="w-16 text-center mx-auto"
                            value={record.activity || 0}
                            onChange={(e) => handleRecordChange(record.id, 'activity', parseInt(e.target.value) || 0)}
                          />
                        ) : (
                          record.activity ?? '—'
                        )}
                      </td>
                      <td className="px-4 py-3 text-center">
                        {editMode ? (
                          <Input
                            type="number"
                            min={0}
                            max={30}
                            className="w-16 text-center mx-auto"
                            value={record.examScore || 0}
                            onChange={(e) => handleRecordChange(record.id, 'examScore', parseInt(e.target.value) || 0)}
                          />
                        ) : (
                          record.examScore ?? '—'
                        )}
                      </td>
                      <td className="px-4 py-3 text-center font-bold">{record.totalScore ?? '—'}</td>
                      <td className="px-4 py-3 text-center">
                        {record.ectsGrade && <Badge className={getGradeColor(percent)}>{record.ectsGrade}</Badge>}
                      </td>
                      <td className="px-4 py-3 text-center">
                        {record.fivePointGrade && <Badge className={getGradeColor(percent)}>{record.fivePointGrade}</Badge>}
                      </td>
                      {editMode && (
                        <td className="px-4 py-3 text-center">
                          <Button size="sm" onClick={() => saveRecord(record)}>
                            <Save size={14} />
                          </Button>
                        </td>
                      )}
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Card>

      {preview && (
        <DocumentPreviewModal
          isOpen={!!preview}
          blob={preview.blob}
          filename={preview.filename}
          mimeType={preview.mimeType}
          onClose={() => setPreview(null)}
        />
      )}
    </div>
  );
};
