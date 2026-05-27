import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { draftsApi } from '../api/drafts';

export function useDraftsByProtocol(protocolId: string) {
  return useQuery({
    queryKey: ['drafts', 'protocol', protocolId],
    queryFn: () => draftsApi.getByProtocol(protocolId),
    enabled: !!protocolId,
  });
}

export function useDraft(id: string) {
  return useQuery({
    queryKey: ['drafts', id],
    queryFn: () => draftsApi.getById(id),
    enabled: !!id,
  });
}

export function useCreateDraft() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: draftsApi.create,
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['drafts', 'protocol', variables.protocolId] });
    },
  });
}

export function useUpdateDraft() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, content }: { id: string; content: string }) => draftsApi.update(id, content),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['drafts', data.id] });
      qc.invalidateQueries({ queryKey: ['drafts', 'protocol', data.protocolId] });
    },
  });
}

export function useApproveDraft() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: draftsApi.approve,
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['drafts', data.id] });
      qc.invalidateQueries({ queryKey: ['drafts', 'protocol', data.protocolId] });
    },
  });
}
