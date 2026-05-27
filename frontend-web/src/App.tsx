import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AppLayout } from './components/layout/AppLayout';
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
import { StudentProfilePage } from './pages/StudentProfilePage';
import { useAuthStore } from './store/authStore';

const queryClient = new QueryClient();

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardPage />} />
            <Route path="meetings" element={<MeetingsPage />} />
            <Route path="meetings/:id" element={<MeetingDetailPage />} />
            <Route path="voting-monitor" element={<VotingMonitorPage />} />
            <Route path="voting-monitor/:meetingId" element={<VotingMonitorPage />} />
            <Route path="protocols" element={<ProtocolsPage />} />
            <Route path="admissions" element={<AdmissionsPage />} />
            <Route path="admin" element={<AdminPage />} />
            <Route path="audit" element={<AuditPage />} />
            <Route path="notifications" element={<NotificationsPage />} />
            <Route path="student/profile" element={<StudentDashboardPage />} />
            <Route path="groups" element={<GroupsPage />} />
            <Route path="disciplines" element={<DisciplinesPage />} />
            <Route path="gradebook" element={<GradebookPage />} />
            <Route path="statements" element={<StatementsPage />} />
            <Route path="statements/new" element={<StatementCreatePage />} />
            <Route path="statements/:id" element={<StatementEditPage />} />
            <Route path="groups/:id" element={<GroupDetailPage />} />
            <Route path="students/:id" element={<StudentProfilePage />} />
            <Route path="teachers" element={<TeachersPage />} />
            <Route path="reports" element={<ReportsPage />} />
            <Route path="gac" element={<GACPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
