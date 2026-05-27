import { api } from './client';
import type { StudentProfile, StudentGrade, StudentMeetingInfo, Admission } from '../types';

export interface UpdateStudentProfileRequest {
  thesisTopic?: string;
  supervisorName?: string;
}

export const studentProfileApi = {
  getProfile: () => api.get<StudentProfile>('/student/profile').then((r) => r.data),

  updateProfile: (data: UpdateStudentProfileRequest) =>
    api.put<StudentProfile>('/student/profile', data).then((r) => r.data),

  getGrades: () => api.get<StudentGrade[]>('/student/grades').then((r) => r.data),

  getAdmission: () => api.get<Admission | null>('/student/admission').then((r) => r.data),

  getMeetingInfo: () => api.get<StudentMeetingInfo>('/student/meeting-info').then((r) => r.data),

  uploadThesis: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post<void>('/student/thesis', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data);
  },
};
