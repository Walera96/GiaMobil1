import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { statementsApi } from '../api/statements';
import type { CreateStatementRequest, StatementRecordDto } from '../api/statements';

export function useStatements() {
  return useQuery({
    queryKey: ['statements'],
    queryFn: statementsApi.getAll,
  });
}

export function useStatement(id: string) {
  return useQuery({
    queryKey: ['statements', id],
    queryFn: () => statementsApi.getById(id),
    enabled: !!id,
  });
}

export function useCreateStatement() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: statementsApi.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['statements'] }),
  });
}

export function useUpdateStatement() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: CreateStatementRequest }) => statementsApi.update(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['statements'] }),
  });
}

export function useUpdateStatementRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ statementId, recordId, data }: { statementId: string; recordId: string; data: StatementRecordDto }) =>
      statementsApi.updateRecord(statementId, recordId, data),
    onSuccess: (_, vars) => {
      qc.invalidateQueries({ queryKey: ['statements', vars.statementId] });
      qc.invalidateQueries({ queryKey: ['statements'] });
    },
  });
}

export function useChangeStatementStatus() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }: { id: string; status: string }) => statementsApi.changeStatus(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['statements'] }),
  });
}

export function useDeleteStatement() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: statementsApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['statements'] }),
  });
}
