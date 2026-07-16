// Maps each backend role to its portal base path.
export const ROLE_HOME = {
  Candidate: '/candidate',
  Admin: '/admin',
  CentreAdmin: '/centre',
  ExamController: '/controller',
  Evaluator: '/evaluator',
  CertificationOfficer: '/officer',
};

export const ROLE_LABEL = {
  Candidate: 'Candidate',
  Admin: 'Programme Admin',
  CentreAdmin: 'Test Centre Admin',
  ExamController: 'Examination Controller',
  Evaluator: 'Evaluator',
  CertificationOfficer: 'Certification Officer',
};

export function homeFor(role) {
  return ROLE_HOME[role] || '/login';
}
