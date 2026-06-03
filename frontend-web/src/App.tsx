import React, { Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AppLayout } from './components/layout/AppLayout';
import { AuthGuard } from './components/AuthGuard';
import { PortalSelector } from './components/PortalSelector';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { useAuthStore } from './store/authStore';

/* ─────────────────────────── Lazy loading порталов ─────────────────────────── */

const MeetingsPage = React.lazy(() => import('./pages/MeetingsPage').then(m => ({ default: m.MeetingsPage })));
const MeetingDetailPage = React.lazy(() => import('./pages/MeetingDetailPage').then(m => ({ default: m.MeetingDetailPage })));
const VotingMonitorPage = React.lazy(() => import('./pages/VotingMonitorPage').then(m => ({ default: m.VotingMonitorPage })));
const ProtocolsPage = React.lazy(() => import('./pages/ProtocolsPage').then(m => ({ default: m.ProtocolsPage })));
const AdmissionsPage = React.lazy(() => import('./pages/AdmissionsPage').then(m => ({ default: m.AdmissionsPage })));
const AdminPage = React.lazy(() => import('./pages/AdminPage').then(m => ({ default: m.AdminPage })));
const AuditPage = React.lazy(() => import('./pages/AuditPage').then(m => ({ default: m.AuditPage })));
const GroupsPage = React.lazy(() => import('./pages/GroupsPage').then(m => ({ default: m.GroupsPage })));
const GroupDetailPage = React.lazy(() => import('./pages/GroupDetailPage').then(m => ({ default: m.GroupDetailPage })));
const DisciplinesPage = React.lazy(() => import('./pages/DisciplinesPage').then(m => ({ default: m.DisciplinesPage })));
const GradebookPage = React.lazy(() => import('./pages/GradebookPage').then(m => ({ default: m.GradebookPage })));
const StatementsPage = React.lazy(() => import('./pages/StatementsPage').then(m => ({ default: m.StatementsPage })));
const StatementEditPage = React.lazy(() => import('./pages/StatementEditPage').then(m => ({ default: m.StatementEditPage })));
const StatementCreatePage = React.lazy(() => import('./pages/StatementCreatePage').then(m => ({ default: m.StatementCreatePage })));
const TeachersPage = React.lazy(() => import('./pages/TeachersPage').then(m => ({ default: m.TeachersPage })));
const ReportsPage = React.lazy(() => import('./pages/ReportsPage').then(m => ({ default: m.ReportsPage })));
const GACPage = React.lazy(() => import('./pages/GACPage').then(m => ({ default: m.GACPage })));

// Student portal
const StudentDashboardPage = React.lazy(() => import('./pages/StudentDashboardPage').then(m => ({ default: m.StudentDashboardPage })));
const StudentProfilePage = React.lazy(() => import('./pages/StudentProfilePage').then(m => ({ default: m.StudentProfilePage })));
const StudentAssignmentsPage = React.lazy(() => import('./pages/student/StudentAssignmentsPage').then(m => ({ default: m.StudentAssignmentsPage })));
const StudentAssignmentDetailPage = React.lazy(() => import('./pages/student/StudentAssignmentDetailPage').then(m => ({ default: m.StudentAssignmentDetailPage })));

// Teacher portal
const TeacherDashboardPage = React.lazy(() => import('./pages/TeacherDashboardPage').then(m => ({ default: m.TeacherDashboardPage })));
const TeacherAssignmentsPage = React.lazy(() => import('./pages/teacher/TeacherAssignmentsPage').then(m => ({ default: m.TeacherAssignmentsPage })));
const TeacherAssignmentSubmissionsPage = React.lazy(() => import('./pages/teacher/TeacherAssignmentSubmissionsPage').then(m => ({ default: m.TeacherAssignmentSubmissionsPage })));
const TeacherScoringConfigPage = React.lazy(() => import('./pages/teacher/TeacherScoringConfigPage').then(m => ({ default: m.TeacherScoringConfigPage })));

// Mobile entry point
const MobileAssignmentsPage = React.lazy(() => import('./pages/mobile/MobileAssignmentsPage'));

const PageLoader = () => (
  <div className="flex h-[60vh] w-full items-center justify-center">
    <div className="h-8 w-8 animate-spin rounded-full border-4 border-[var(--color-primary)] border-t-transparent" />
  </div>
);

const queryClient = new QueryClient();

function AppRoutes() {
  const { isAuthenticated, primaryPortal } = useAuthStore();

  if (!isAuthenticated) {
    return <LoginPage />;
  }

  if (!primaryPortal) {
    return <PortalSelector />;
  }

  return (
    <AppLayout>
      <Suspense fallback={<PageLoader />}>
        <Routes>
          {/* === ОБЩИЕ === */}
          <Route path="/" element={<DashboardPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />

          {/* === ПОРТАЛ: АДМИНИСТРАЦИЯ === */}
          <Route
            path="/admin"
            element={
              <AuthGuard requiredPortal="admin">
                <AdminPage />
              </AuthGuard>
            }
          />
          <Route
            path="/audit"
            element={
              <AuthGuard requiredPortal="admin">
                <AuditPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: МЕТОДИСТ === */}
          <Route
            path="/groups"
            element={
              <AuthGuard requiredPortal="methodist">
                <GroupsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/groups/:id"
            element={
              <AuthGuard requiredPortal="methodist">
                <GroupDetailPage />
              </AuthGuard>
            }
          />
          <Route
            path="/disciplines"
            element={
              <AuthGuard requiredPortal="methodist">
                <DisciplinesPage />
              </AuthGuard>
            }
          />
          <Route
            path="/gradebook"
            element={
              <AuthGuard requiredPortal="methodist">
                <GradebookPage />
              </AuthGuard>
            }
          />
          <Route
            path="/statements"
            element={
              <AuthGuard requiredPortal="methodist">
                <StatementsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/statements/create"
            element={
              <AuthGuard requiredPortal="methodist">
                <StatementCreatePage />
              </AuthGuard>
            }
          />
          <Route
            path="/statements/:id"
            element={
              <AuthGuard requiredPortal="methodist">
                <StatementEditPage />
              </AuthGuard>
            }
          />
          <Route
            path="/statements/:id/edit"
            element={
              <AuthGuard requiredPortal="methodist">
                <StatementEditPage />
              </AuthGuard>
            }
          />
          <Route
            path="/teachers"
            element={
              <AuthGuard requiredPortal="methodist">
                <TeachersPage />
              </AuthGuard>
            }
          />
          <Route
            path="/admissions"
            element={
              <AuthGuard requiredPortal="methodist">
                <AdmissionsPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: ГЭК === */}
          <Route
            path="/gac"
            element={
              <AuthGuard requiredPortal="gek">
                <GACPage />
              </AuthGuard>
            }
          />
          <Route
            path="/meetings"
            element={
              <AuthGuard requiredPortal="gek">
                <MeetingsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/meetings/:id"
            element={
              <AuthGuard requiredPortal="gek">
                <MeetingDetailPage />
              </AuthGuard>
            }
          />
          <Route
            path="/protocols"
            element={
              <AuthGuard requiredPortal="gek">
                <ProtocolsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/voting-monitor"
            element={
              <AuthGuard requiredPortal="gek">
                <VotingMonitorPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: СТУДЕНТ === */}
          <Route
            path="/student"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentDashboardPage />
              </AuthGuard>
            }
          />
          <Route
            path="/student/profile"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentProfilePage />
              </AuthGuard>
            }
          />
          <Route
            path="/student/assignments"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentAssignmentsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/student/assignments/:id"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentAssignmentDetailPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: СТУДЕНТ (альтернативный путь /portal/student) === */}
          <Route
            path="/portal/student"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentDashboardPage />
              </AuthGuard>
            }
          />
          <Route
            path="/portal/student/assignments"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentAssignmentsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/portal/student/assignments/:id"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentAssignmentDetailPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: ПРЕПОДАВАТЕЛЬ === */}
          <Route
            path="/teacher"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherDashboardPage />
              </AuthGuard>
            }
          />
          <Route
            path="/teacher/assignments"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherAssignmentsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/teacher/assignments/:id/submissions"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherAssignmentSubmissionsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/teacher/scoring"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherScoringConfigPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: ПРЕПОДАВАТЕЛЬ (альтернативный путь /portal/teacher) === */}
          <Route
            path="/portal/teacher"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherDashboardPage />
              </AuthGuard>
            }
          />
          <Route
            path="/portal/teacher/assignments"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherAssignmentsPage />
              </AuthGuard>
            }
          />
          <Route
            path="/portal/teacher/assignments/:id/submissions"
            element={
              <AuthGuard requiredPortal="teacher_portal">
                <TeacherAssignmentSubmissionsPage />
              </AuthGuard>
            }
          />

          {/* === ПОРТАЛ: СТУДЕНТ (задания) — legacy === */}
          <Route
            path="/student-portal"
            element={
              <AuthGuard requiredPortal="student_portal">
                <StudentDashboardPage />
              </AuthGuard>
            }
          />

          {/* === ДЕКАНАТ (временно — отчёты) === */}
          <Route
            path="/reports"
            element={
              <AuthGuard requiredPortal="deanery">
                <ReportsPage />
              </AuthGuard>
            }
          />

          {/* === MOBILE ENTRY POINT === */}
          <Route path="/m" element={<MobileAssignmentsPage />} />
          <Route path="/m/assignments/:id" element={<MobileAssignmentsPage />} />

          {/* Перенаправление по primaryPortal */}
          <Route
            path="*"
            element={
              <Navigate
                to={
                  primaryPortal === 'student'
                    ? '/student'
                    : primaryPortal === 'student_portal'
                      ? '/student-portal'
                      : primaryPortal === 'teacher_portal'
                        ? '/teacher'
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
      </Suspense>
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
