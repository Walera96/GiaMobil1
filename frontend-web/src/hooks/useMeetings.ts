import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { meetingsApi } from '../api/meetings';

export function useMeetings() {
  return useQuery({
    queryKey: ['meetings'],
    queryFn: meetingsApi.getAll,
  });
}

export function useMeeting(id: string) {
  return useQuery({
    queryKey: ['meetings', id],
    queryFn: () => meetingsApi.getById(id),
    enabled: !!id,
  });
}

export function useMeetingAgenda(meetingId: string) {
  return useQuery({
    queryKey: ['meetings', meetingId, 'agenda'],
    queryFn: () => meetingsApi.getAgenda(meetingId),
    enabled: !!meetingId,
  });
}

export function useCreateMeeting() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: meetingsApi.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['meetings'] }),
  });
}

export function useActivateMeeting() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: meetingsApi.activate,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['meetings'] }),
  });
}

export function useCloseMeeting() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: meetingsApi.close,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['meetings'] }),
  });
}
