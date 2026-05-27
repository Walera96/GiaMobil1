import { useQuery } from '@tanstack/react-query';
import { geksApi } from '../api/geks';

export function useGeks() {
  return useQuery({
    queryKey: ['geks'],
    queryFn: geksApi.getAll,
  });
}
