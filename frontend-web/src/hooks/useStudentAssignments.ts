import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentAssignmentsApi, type AttachedFile } from '../api/assignments';
import { useAssignmentNotifications } from './useAssignmentNotifications';

/** Получить список заданий студента (с пагинацией и фильтром по статусу) */
export function useStudentAssignments(status?: string, page = 0, size = 20) {
  return useQuery({
    queryKey: ['student-assignments', status, page, size],
    queryFn: async () => {
      const { data } = await studentAssignmentsApi.getMyAssignments(status, page, size);
      return data;
    },
  });
}

/** Получить детали задания */
export function useStudentAssignment(id: string) {
  return useQuery({
    queryKey: ['student-assignment', id],
    queryFn: async () => {
      const { data } = await studentAssignmentsApi.getById(id);
      return data;
    },
    enabled: !!id,
  });
}

/** Получить мою сдачу по заданию */
export function useMySubmission(assignmentId: string) {
  return useQuery({
    queryKey: ['my-submission', assignmentId],
    queryFn: async () => {
      const { data } = await studentAssignmentsApi.getMySubmissions();
      return data.find((s) => s.assignmentId === assignmentId) ?? null;
    },
    enabled: !!assignmentId,
  });
}

/** Сдать задание */
export function useSubmitAssignment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      assignmentId,
      payload,
    }: {
      assignmentId: string;
      payload: {
        solutionFiles?: AttachedFile[];
        studentComment?: string;
      };
    }) => {
      const { data } = await studentAssignmentsApi.submit(assignmentId, payload);
      return data;
    },
    onSuccess: (_, vars) => {
      queryClient.invalidateQueries({ queryKey: ['student-assignments'] });
      queryClient.invalidateQueries({ queryKey: ['my-submission', vars.assignmentId] });
    },
  });
}

/** Сохранить черновик */
export function useSaveDraft() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      submissionId,
      payload,
    }: {
      submissionId: string;
      payload: {
        solutionFiles?: AttachedFile[];
        studentComment?: string;
      };
    }) => {
      const { data } = await studentAssignmentsApi.saveDraft(submissionId, payload);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-submission'] });
    },
  });
}

/** Подписка на SSE-уведомления о заданиях */
export function useStudentAssignmentNotifications() {
  return useAssignmentNotifications();
}
