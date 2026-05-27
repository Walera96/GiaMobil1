import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { disciplinesApi } from '../api/disciplines';
import type { CreateDisciplineRequest } from '../api/disciplines';

export function useDisciplines() {
  return useQuery({
    queryKey: ['disciplines'],
    queryFn: disciplinesApi.getAll,
  });
}

export function useCreateDiscipline() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: disciplinesApi.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['disciplines'] }),
  });
}

export function useUpdateDiscipline() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: CreateDisciplineRequest }) => disciplinesApi.update(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['disciplines'] }),
  });
}

export function useDeleteDiscipline() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: disciplinesApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['disciplines'] }),
  });
}
