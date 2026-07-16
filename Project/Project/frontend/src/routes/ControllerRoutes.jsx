import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout';
import QuestionBank from '../pages/controller/QuestionBank';
import PaperBuilder from '../pages/controller/PaperBuilder';
import PaperTracker from '../pages/controller/PaperTracker';
import ScriptAllocation from '../pages/controller/ScriptAllocation';
import ModerationWorkflow from '../pages/controller/ModerationWorkflow';
import ResultComputation from '../pages/controller/ResultComputation';
import ResultPublication from '../pages/controller/ResultPublication';
import ReEvaluationManager from '../pages/controller/ReEvaluationManager';

export default function ControllerRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Navigate to="questions" replace />} />
        <Route path="questions" element={<QuestionBank />} />
        <Route path="papers" element={<PaperBuilder />} />
        <Route path="paper-tracker" element={<PaperTracker />} />
        <Route path="scripts" element={<ScriptAllocation />} />
        <Route path="moderation" element={<ModerationWorkflow />} />
        <Route path="compute" element={<ResultComputation />} />
        <Route path="publish" element={<ResultPublication />} />
        <Route path="re-evaluations" element={<ReEvaluationManager />} />
        <Route path="*" element={<Navigate to="questions" replace />} />
      </Route>
    </Routes>
  );
}
