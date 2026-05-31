import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AppLayout } from './components/layout/AppLayout';
import { PortalSelector } from './components/PortalSelector';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { MeetingsPage } from './pages/MeetingsPage';
import { MeetingDetailPage } from './pages/MeetingDetailPage';
import { VotingMonitorPage } from './pages/VotingMonitorPage';
import { ProtocolsPage } from './pages/ProtocolsPage';
import { AdmissionsPage } from './pages/AdmissionsPage';
import { AdminPage } from './pages/AdminPage';
import { AuditPage } from './pages/AuditPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { StudentDashboardPage } from './pages/StudentDashboardPage';
import { StudentProfilePage } from './pages/StudentProfilePage';
import { GroupsPage } from './pages/GroupsPage';
import { DisciplinesPage } from './pages/DisciplinesPage';
import { GradebookPage } from './pages/GradebookPage';
import { StatementsPage } from './pages/StatementsPage';
import { StatementEditPage } from './pages/StatementEditPage';
import { StatementCreatePage } from './pages/StatementCreatePage';
import { TeachersPage } from './pages/TeachersPage';
import { ReportsPage } from './pages/ReportsPage';
import { GACPage } from './pages/GACPage';
import { GroupDetailPage } from './pages/GroupDetailPage';
import { useAuthStore } from './store/authStore';

const queryClient = new QueryClient();

function AppRoutes() {
  const { isAuthenticated, primaryPortal } = useAuthStore();

  if (!isAuthenticated) {
    return <LoginPage />;
  }

  // Если ещё не выбран портал — показать селектор
  if (!primaryPortal) {
    return <PortalSelector />;
  }

  return (
    <AppLayout>
      <Routes>
        {/* === ОБЩИЕ === */}
        <Route path="/" element={<DashboardPage />} />
        <Route path="/notifications" element={<NotificationsPage />} />

        {/* === ПОРТАЛ: АДМИНИСТРАЦИЯ === */}
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/audit" element={<AuditPage />} />

        {/* === ПОРТАЛ: МЕТОДИСТ === */}
        <Route path="/groups" element={<GroupsPage />} />
        <Route path="/groups/:id" element={<GroupDetailPage />} />
        <Route path="/disciplines" element={<DisciplinesPage />} />
        <Route path="/gradebook" element={<GradebookPage />} />
        <Route path="/statements" element={<StatementsPage />} />
        <Route path="/statements/create" element={<StatementCreatePage />} />
        <Route path="/statements/:id/edit" element={<StatementEditPage />} />
        <Route path="/teachers" element={<TeachersPage />} />
        <Route path="/admissions" element={<AdmissionsPage />} />

        {/* === ПОРТАЛ: ГЭК === */}
        <Route path="/gac" element={<GACPage />} />
        <Route path="/meetings" element={<MeetingsPage />} />
        <Route path="/meetings/:id" element={<MeetingDetailPage />} />
        <Route path="/protocols" element={<ProtocolsPage />} />
        <Route path="/voting-monitor" element={<VotingMonitorPage />} />

        {/* === ПОРТАЛ: СТУДЕНТ === */}
        <Route path="/student" element={<StudentDashboardPage />} />
        <Route path="/student/profile" element={<StudentProfilePage />} />

        {/* === ДЕКАНАТ (временно — отчёты) === */}
        <Route path="/reports" element={<ReportsPage />} />

        {/* Перенаправление по primaryPortal */}
        <Route
          path="*"
          element={
            <Navigate
              to={
                primaryPortal === 'student'
                  ? '/student'
                  : primaryPortal === 'gek'
                    ? '/meetings'
                    : primaryPortal === 'methodist'
                      ? '/groups'
                      : primaryPortal === 'admin'
                        ? '/admin'
                        : '/'
              }
              replace
            />
          }
        />
      </Routes>
    </AppLayout>
  );
}

export default function App() {
  const { initFromStorage } = useAuthStore();

  React.useEffect(() => {
    initFromStorage();
  }, []);

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </QueryClientProvider>
  );
}
