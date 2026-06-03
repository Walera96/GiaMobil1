import { useEffect, useRef, useState, useCallback } from 'react';

export type AssignmentSseEvent =
  | { type: 'new-assignment'; assignmentId: string; title: string }
  | { type: 'submission-reviewed'; assignmentId: string; submissionId: string; score: number }
  | { type: 'deadline-warning'; assignmentId: string; title: string; hoursLeft: number };

/**
 * Хук для real-time уведомлений о заданиях через SSE.
 * Подключается к /api/sse/assignments с JWT-токеном.
 */
export function useAssignmentNotifications() {
  const [lastEvent, setLastEvent] = useState<AssignmentSseEvent | null>(null);
  const [connected, setConnected] = useState(false);
  const esRef = useRef<EventSource | null>(null);

  const connect = useCallback(() => {
    if (esRef.current) {
      esRef.current.close();
    }
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8090/api';
    const es = new EventSource(`${baseUrl}/sse/assignments?token=${token}`);
    esRef.current = es;

    es.onopen = () => setConnected(true);

    es.addEventListener('connected', () => {
      setConnected(true);
    });

    es.addEventListener('new-assignment', (event) => {
      try {
        const parsed = JSON.parse(event.data);
        setLastEvent({ type: 'new-assignment', ...parsed });
      } catch {
        // игнорируем malformed события
      }
    });

    es.addEventListener('submission-reviewed', (event) => {
      try {
        const parsed = JSON.parse(event.data);
        setLastEvent({ type: 'submission-reviewed', ...parsed });
      } catch {
        // игнорируем malformed события
      }
    });

    es.addEventListener('deadline-warning', (event) => {
      try {
        const parsed = JSON.parse(event.data);
        setLastEvent({ type: 'deadline-warning', ...parsed });
      } catch {
        // игнорируем malformed события
      }
    });

    es.onerror = () => {
      setConnected(false);
      es.close();
    };
  }, []);

  const disconnect = useCallback(() => {
    esRef.current?.close();
    esRef.current = null;
    setConnected(false);
  }, []);

  useEffect(() => {
    connect();
    return () => {
      esRef.current?.close();
    };
  }, [connect]);

  return { lastEvent, connected, connect, disconnect };
}
