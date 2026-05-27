import { useQuery } from '@tanstack/react-query';
import { directionsApi } from '../api/directions';

export function useDirections() {
  return useQuery({
    queryKey: ['directions'],
    queryFn: directionsApi.getAll,
  });
}
