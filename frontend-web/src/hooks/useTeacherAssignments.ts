import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { teacherAssignmentsApi, type AssignmentCreateRequest, type ReviewRequest, type SubmissionStatus } from '../api/assignments';

/** Получить список заданий преподавателя (с пагинацией) */
export function useTeacherAssignments(page = 0, size = 20, sort = 'createdAt,desc') {
  return useQuery({
    queryKey: ['teacher-assignments', page, size, sort],
    queryFn: async () => {
      const { data } = await teacherAssignmentsApi.getMyAssignments(page, size, sort);
      return data;
    },
  });
}

/** Получить детали задания */
export function useTeacherAssignment(id: string) {
  return useQuery({
    queryKey: ['teacher-assignment', id],
    queryFn: async () => {
      const { data } = await teacherAssignmentsApi.getById(id);
      return data;
    },
    enabled: !!id,
  });
}

/** Создать задание */
export function useCreateAssignment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (payload: AssignmentCreateRequest) => {
      const { data } = await teacherAssignmentsApi.create(payload);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teacher-assignments'] });
    },
  });
}

/** Удалить задание */
export function useDeleteAssignment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await teacherAssignmentsApi.delete(id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teacher-assignments'] });
    },
  });
}

/** Получить сдачи по заданию */
export function useAssignmentSubmissions(assignmentId: string, status?: SubmissionStatus) {
  return useQuery({
    queryKey: ['assignment-submissions', assignmentId, status],
    queryFn: async () => {
      const { data } = await teacherAssignmentsApi.getSubmissions(assignmentId, status);
      return data;
    },
    enabled: !!assignmentId,
  });
}

/** Проверить сдачу */
export function useReviewSubmission() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      assignmentId,
      submissionId,
      payload,
    }: {
      assignmentId: string;
      submissionId: string;
      payload: ReviewRequest;
    }) => {
      const { data } = await teacherAssignmentsApi.reviewSubmission(assignmentId, submissionId, payload);
      return data;
    },
    onSuccess: (_, vars) => {
      queryClient.invalidateQueries({ queryKey: ['assignment-submissions', vars.assignmentId] });
    },
  });
}
