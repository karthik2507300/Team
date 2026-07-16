import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout';
import ScriptList from '../pages/evaluator/ScriptList';
import MarksEntry from '../pages/evaluator/MarksEntry';
import DualMarkingView from '../pages/evaluator/DualMarkingView';

export default function EvaluatorRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<ScriptList />} />
        <Route path="marks" element={<MarksEntry />} />
        <Route path="dual" element={<DualMarkingView />} />
        <Route path="*" element={<Navigate to="." replace />} />
      </Route>
    </Routes>
  );
}
