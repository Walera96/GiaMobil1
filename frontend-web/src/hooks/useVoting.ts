import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { votingApi } from '../api/voting';
import type { VoteRequest } from '../types';

export function useVotesByAgendaItem(agendaItemId: string) {
  return useQuery({
    queryKey: ['votes', agendaItemId],
    queryFn: () => votingApi.getVotesByAgendaItem(agendaItemId),
    enabled: !!agendaItemId,
  });
}

export function useAverageScore(agendaItemId: string) {
  return useQuery({
    queryKey: ['average', agendaItemId],
    queryFn: () => votingApi.getAverageScore(agendaItemId),
    enabled: !!agendaItemId,
  });
}

export function useVoteDetails(agendaItemId: string) {
  return useQuery({
    queryKey: ['voteDetails', agendaItemId],
    queryFn: () => votingApi.getVoteDetails(agendaItemId),
    enabled: !!agendaItemId,
  });
}

export function useCastVote() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: VoteRequest) => votingApi.castVote(data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['votes', variables.agendaItemId] });
      qc.invalidateQueries({ queryKey: ['average', variables.agendaItemId] });
      qc.invalidateQueries({ queryKey: ['voteDetails', variables.agendaItemId] });
      qc.invalidateQueries({ queryKey: ['meetings'] });
    },
  });
}

export function useFinishVoting() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (agendaItemId: string) => votingApi.finishVoting(agendaItemId),
    onSuccess: (_data, agendaItemId) => {
      qc.invalidateQueries({ queryKey: ['voteDetails', agendaItemId] });
      qc.invalidateQueries({ queryKey: ['votes', agendaItemId] });
    },
  });
}
