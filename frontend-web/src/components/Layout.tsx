import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import {
  AppBar, Toolbar, Typography, Button, Box, IconButton, CssBaseline, ThemeProvider, createTheme,
} from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import LogoutIcon from '@mui/icons-material/Logout';
import { useThemeStore } from '../store/themeStore';
import { useAuthStore } from '../store/authStore';

const Layout: React.FC = () => {
  const { mode, toggleMode } = useThemeStore();
  const { logout, isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  const theme = React.useMemo(
    () =>
      createTheme({
        palette: {
          mode,
          primary: { main: '#1976d2' },
          secondary: { main: '#dc004e' },
        },
      }),
    [mode]
  );

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            ГИА — СПбУТУИЭ
          </Typography>
          {isAuthenticated && (
            <>
              <Button color="inherit" onClick={() => navigate('/')}>
                Главная
              </Button>
              <Button color="inherit" onClick={() => navigate('/meetings')}>
                Заседания
              </Button>
              <Button color="inherit" onClick={() => navigate('/protocols')}>
                Протоколы
              </Button>
              <Button color="inherit" onClick={() => navigate('/voting')}>
                Голосование
              </Button>
            </>
          )}
          <IconButton color="inherit" onClick={toggleMode}>
            {mode === 'light' ? <Brightness4Icon /> : <Brightness7Icon />}
          </IconButton>
          {isAuthenticated && (
            <IconButton color="inherit" onClick={handleLogout}>
              <LogoutIcon />
            </IconButton>
          )}
        </Toolbar>
      </AppBar>
      <Box sx={{ p: 3, flex: 1 }}>
        <Outlet />
      </Box>
    </ThemeProvider>
  );
};

export default Layout;
