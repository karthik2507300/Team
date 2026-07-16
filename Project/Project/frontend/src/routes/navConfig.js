// Sidebar navigation per role.
export const NAV = {
  Candidate: [
    { label: 'My Profile', path: '/candidate/profile' },
    { label: 'Programme Catalog', path: '/candidate/programs' },
    { label: 'Exam Registration', path: '/candidate/exam-registration' },
    { label: 'Hall Ticket', path: '/candidate/hall-ticket' },
    { label: 'Results', path: '/candidate/results' },
    { label: 'Re-evaluation', path: '/candidate/re-evaluation' },
    { label: 'Certificate Wallet', path: '/candidate/certificates' },
    { label: 'Renewal', path: '/candidate/renewal' },
  ],
  Admin: [
    { label: 'Dashboard', path: '/admin' },
    { label: 'User Management', path: '/admin/users' },
    { label: 'Programme Builder', path: '/admin/programs' },
    { label: 'Grading Scale', path: '/admin/grading' },
    { label: 'Exam Windows', path: '/admin/windows' },
    { label: 'Test Centres', path: '/admin/centres' },
    { label: 'Eligibility Verifier', path: '/admin/eligibility' },
    { label: 'Audit Logs', path: '/admin/audit' },
    { label: 'Notifications', path: '/admin/notifications' },
  ],
  CentreAdmin: [
    { label: 'Attendance Sheet', path: '/centre/attendance' },
    { label: 'Room Assignment', path: '/centre/rooms' },
    { label: 'Invigilator Deployment', path: '/centre/invigilators' },
  ],
  ExamController: [
    { label: 'Question Bank', path: '/controller/questions' },
    { label: 'Paper Builder', path: '/controller/papers' },
    { label: 'Paper Tracker', path: '/controller/paper-tracker' },
    { label: 'Script Allocation', path: '/controller/scripts' },
    { label: 'Moderation', path: '/controller/moderation' },
    { label: 'Result Computation', path: '/controller/compute' },
    { label: 'Result Publication', path: '/controller/publish' },
    { label: 'Re-evaluations', path: '/controller/re-evaluations' },
  ],
  Evaluator: [
    { label: 'Assigned Scripts', path: '/evaluator' },
    { label: 'Marks Entry', path: '/evaluator/marks' },
    { label: 'Dual Marking', path: '/evaluator/dual' },
  ],
  CertificationOfficer: [
    { label: 'Issuance Queue', path: '/officer' },
    { label: 'Renewal Reviewer', path: '/officer/renewals' },
    { label: 'Validity Tracker', path: '/officer/validity' },
  ],
};
