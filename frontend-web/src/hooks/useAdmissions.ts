import { useQuery } from '@tanstack/react-query';
import { admissionsApi } from '../api/admissions';

export function useAdmissions() {
  return useQuery({
    queryKey: ['admissions'],
    queryFn: admissionsApi.getAll,
  });
}
