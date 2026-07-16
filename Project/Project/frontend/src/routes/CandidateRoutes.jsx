import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout';
import Profile from '../pages/candidate/Profile';
import ProgramCatalog from '../pages/candidate/ProgramCatalog';
import ExamRegistration from '../pages/candidate/ExamRegistration';
import HallTicket from '../pages/candidate/HallTicket';
import Results from '../pages/candidate/Results';
import ReEvaluation from '../pages/candidate/ReEvaluation';
import CertificateWallet from '../pages/candidate/CertificateWallet';
import RenewalForm from '../pages/candidate/RenewalForm';

export default function CandidateRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Navigate to="profile" replace />} />
        <Route path="profile" element={<Profile />} />
        <Route path="programs" element={<ProgramCatalog />} />
        <Route path="exam-registration" element={<ExamRegistration />} />
        <Route path="hall-ticket" element={<HallTicket />} />
        <Route path="results" element={<Results />} />
        <Route path="re-evaluation" element={<ReEvaluation />} />
        <Route path="certificates" element={<CertificateWallet />} />
        <Route path="renewal" element={<RenewalForm />} />
        <Route path="*" element={<Navigate to="profile" replace />} />
      </Route>
    </Routes>
  );
}
