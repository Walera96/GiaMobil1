import { api } from './client';
import type { Vote, VoteRequest } from '../types';

export interface VoteDetailsResponse {
  agendaItemId: string;
  overallAverageScore: number;
  totalVotes: number;
  votes: Vote[];
}

export const votingApi = {
  castVote: (data: VoteRequest) => api.post<Vote>('/votes', data).then((r) => r.data),

  getVotesByAgendaItem: (agendaItemId: string) =>
    api.get<Vote[]>(`/votes/agenda-item/${agendaItemId}`).then((r) => r.data),

  getAverageScore: (agendaItemId: string) =>
    api.get<number>(`/votes/agenda-item/${agendaItemId}/average`).then((r) => r.data),

  getVoteDetails: (agendaItemId: string) =>
    api.get<VoteDetailsResponse>(`/votes/agenda-item/${agendaItemId}/details`).then((r) => r.data),

  finishVoting: (agendaItemId: string) =>
    api.post<VoteDetailsResponse>(`/votes/agenda-item/${agendaItemId}/finish`).then((r) => r.data),

  downloadThesis: (studentId: string) =>
    api.get<Blob>(`/students/${studentId}/thesis`, { responseType: 'blob' }).then((r) => r.data),
};
