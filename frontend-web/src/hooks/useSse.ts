import { useEffect, useRef, useState, useCallback } from 'react';

export function useSse<T>(url: string) {
  const [data, setData] = useState<T | null>(null);
  const [connected, setConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const esRef = useRef<EventSource | null>(null);

  const connect = useCallback(() => {
    if (esRef.current) {
      esRef.current.close();
    }
    const token = localStorage.getItem('accessToken');
    const fullUrl = token ? `${url}?token=${token}` : url;
    const es = new EventSource(fullUrl);
    esRef.current = es;

    es.onopen = () => {
      setConnected(true);
      setError(null);
    };

    es.onmessage = (event) => {
      try {
        const parsed = JSON.parse(event.data);
        setData(parsed);
      } catch {
        setData(event.data as unknown as T);
      }
    };

    es.onerror = () => {
      setConnected(false);
      setError('SSE connection error');
      es.close();
    };
  }, [url]);

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

  return { data, connected, error, connect, disconnect };
}
