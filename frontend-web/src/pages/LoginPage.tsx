import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../api/auth';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Card } from '../components/ui/Card';
import { Vote } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await authApi.login({ username, password });
      login(
        data.accessToken,
        data.refreshToken,
        data.user,
        data.roles,
        data.availablePortals,
        data.primaryPortal
      );
      navigate('/');
    } catch (err: any) {
      if (!err.response) {
        setError('Сервер недоступен. Проверьте, запущен ли бэкенд.');
      } else if (err.response.status === 401 || err.response.status === 403) {
        setError('Неверный логин или пароль');
      } else {
        setError(err.response.data?.message || 'Ошибка входа. Попробуйте позже.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[var(--color-bg)]">
      <Card className="w-full max-w-md">
        <div className="mb-6 flex flex-col items-center">
          <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-full bg-[var(--color-primary)] text-white">
            <Vote size={24} />
          </div>
          <h1 className="text-2xl font-bold text-[var(--color-text)]">ГИА СПбУТУИЭ</h1>
          <p className="text-sm text-[var(--color-text-muted)]">Вход в систему</p>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Логин"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <Input
            label="Пароль"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {error && <p className="text-sm text-[var(--color-danger)]">{error}</p>}
          <Button type="submit" size="lg" className="w-full" isLoading={loading}>
            Войти
          </Button>
        </form>
      </Card>
    </div>
  );
};
