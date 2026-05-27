import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentProfileApi, type UpdateStudentProfileRequest } from '../api/studentProfile';

export function useStudentProfile() {
  return useQuery({
    queryKey: ['studentProfile'],
    queryFn: studentProfileApi.getProfile,
  });
}

export function useStudentGrades() {
  return useQuery({
    queryKey: ['studentGrades'],
    queryFn: studentProfileApi.getGrades,
  });
}

export function useStudentAdmission() {
  return useQuery({
    queryKey: ['studentAdmission'],
    queryFn: studentProfileApi.getAdmission,
  });
}

export function useStudentMeetingInfo() {
  return useQuery({
    queryKey: ['studentMeetingInfo'],
    queryFn: studentProfileApi.getMeetingInfo,
  });
}

export function useUploadThesis() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (file: File) => studentProfileApi.uploadThesis(file),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['studentProfile'] });
    },
  });
}

export function useUpdateStudentProfile() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: UpdateStudentProfileRequest) => studentProfileApi.updateProfile(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['studentProfile'] });
    },
  });
}
