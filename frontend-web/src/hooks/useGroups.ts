import { useQuery } from '@tanstack/react-query';
import { groupsApi } from '../api/groups';

export function useGroups() {
  return useQuery({
    queryKey: ['groups'],
    queryFn: groupsApi.getAll,
  });
}
