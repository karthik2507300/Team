import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout';
import IssuanceQueue from '../pages/certOfficer/IssuanceQueue';
import RenewalReviewer from '../pages/certOfficer/RenewalReviewer';
import ValidityTracker from '../pages/certOfficer/ValidityTracker';

export default function CertOfficerRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<IssuanceQueue />} />
        <Route path="renewals" element={<RenewalReviewer />} />
        <Route path="validity" element={<ValidityTracker />} />
        <Route path="*" element={<Navigate to="." replace />} />
      </Route>
    </Routes>
  );
}
