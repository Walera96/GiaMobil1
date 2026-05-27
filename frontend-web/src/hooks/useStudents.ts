import { useQuery } from '@tanstack/react-query';
import { studentsApi } from '../api/students';

export function useStudents(groupId?: string) {
  return useQuery({
    queryKey: ['students', groupId],
    queryFn: () => studentsApi.getAll(groupId),
  });
}
