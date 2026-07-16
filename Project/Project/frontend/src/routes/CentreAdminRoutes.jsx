import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout';
import AttendanceSheet from '../pages/centreAdmin/AttendanceSheet';
import RoomAssignment from '../pages/centreAdmin/RoomAssignment';
import InvigilatorDeployment from '../pages/centreAdmin/InvigilatorDeployment';

export default function CentreAdminRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Navigate to="attendance" replace />} />
        <Route path="attendance" element={<AttendanceSheet />} />
        <Route path="rooms" element={<RoomAssignment />} />
        <Route path="invigilators" element={<InvigilatorDeployment />} />
        <Route path="*" element={<Navigate to="attendance" replace />} />
      </Route>
    </Routes>
  );
}
