import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout';
import Dashboard from '../pages/admin/Dashboard';
import UserManagement from '../pages/admin/UserManagement';
import ProgramBuilder from '../pages/admin/ProgramBuilder';
import GradingScale from '../pages/admin/GradingScale';
import ExamWindowManager from '../pages/admin/ExamWindowManager';
import TestCentreManager from '../pages/admin/TestCentreManager';
import EligibilityVerifier from '../pages/admin/EligibilityVerifier';
import AuditLogViewer from '../pages/admin/AuditLogViewer';
import NotificationManager from '../pages/admin/NotificationManager';

export default function AdminRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Dashboard />} />
        <Route path="users" element={<UserManagement />} />
        <Route path="programs" element={<ProgramBuilder />} />
        <Route path="grading" element={<GradingScale />} />
        <Route path="windows" element={<ExamWindowManager />} />
        <Route path="centres" element={<TestCentreManager />} />
        <Route path="eligibility" element={<EligibilityVerifier />} />
        <Route path="audit" element={<AuditLogViewer />} />
        <Route path="notifications" element={<NotificationManager />} />
        <Route path="*" element={<Navigate to="." replace />} />
      </Route>
    </Routes>
  );
}
