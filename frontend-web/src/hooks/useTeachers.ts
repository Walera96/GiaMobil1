import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { teachersApi } from '../api/teachers';
import type { CreateTeacherRequest } from '../api/teachers';

export function useTeachers() {
  return useQuery({
    queryKey: ['teachers'],
    queryFn: teachersApi.getAll,
  });
}

export function useCreateTeacher() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: teachersApi.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['teachers'] }),
  });
}

export function useUpdateTeacher() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: CreateTeacherRequest }) => teachersApi.update(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['teachers'] }),
  });
}

export function useDeleteTeacher() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: teachersApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['teachers'] }),
  });
}
