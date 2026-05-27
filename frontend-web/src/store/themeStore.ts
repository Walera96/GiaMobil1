import { create } from 'zustand';

type ThemeMode = 'light' | 'dark';

interface ThemeState {
  mode: ThemeMode;
  toggleMode: () => void;
}

export const useThemeStore = create<ThemeState>((set) => ({
  mode: (localStorage.getItem('theme') as ThemeMode) || 'light',
  toggleMode: () =>
    set((state) => {
      const newMode = state.mode === 'light' ? 'dark' : 'light';
      localStorage.setItem('theme', newMode);
      return { mode: newMode };
    }),
}));
