import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { gradesApi } from '../api/grades';
import type { CreateGradeRequest } from '../api/grades';

export function useGrades() {
  return useQuery({
    queryKey: ['grades'],
    queryFn: gradesApi.getAll,
  });
}

export function useGradesByStudent(studentId: string) {
  return useQuery({
    queryKey: ['grades', 'student', studentId],
    queryFn: () => gradesApi.getByStudent(studentId),
    enabled: !!studentId,
  });
}

export function useGradesByGroup(groupId: string | null) {
  return useQuery({
    queryKey: ['grades', 'group', groupId],
    queryFn: () => gradesApi.getByGroup(groupId!),
    enabled: !!groupId,
  });
}

export function useCreateGrade() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: gradesApi.create,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['grades'] });
    },
  });
}

export function useUpdateGrade() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: CreateGradeRequest }) => gradesApi.update(id, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['grades'] });
    },
  });
}

export function useDeleteGrade() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: gradesApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['grades'] }),
  });
}
