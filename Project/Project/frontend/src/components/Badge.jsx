const TONES = {
  green: 'bg-green-100 text-green-800',
  red: 'bg-red-100 text-red-800',
  amber: 'bg-amber-100 text-amber-800',
  blue: 'bg-brand-100 text-brand-800',
  gray: 'bg-gray-100 text-gray-700',
};

// Map common status strings to a tone.
const STATUS_TONE = {
  Active: 'green', Valid: 'green', Pass: 'green', Eligible: 'green', Approved: 'green',
  Published: 'green', Confirmed: 'green', Verified: 'green', Read: 'gray', Finalised: 'green',
  Suspended: 'amber', PendingVerification: 'amber', Submitted: 'amber', UnderReview: 'amber',
  Draft: 'amber', Assigned: 'amber', Allocated: 'amber', Upcoming: 'amber', UnderEvaluation: 'amber',
  Inactive: 'gray', Discontinued: 'gray', Unread: 'blue', Open: 'blue', Distributed: 'blue',
  Fail: 'red', Rejected: 'red', Revoked: 'red', Blacklisted: 'red', Debarred: 'red',
  Ineligible: 'red', NoShow: 'red', Expired: 'red', Absent: 'red', Moderated: 'amber',
};

export default function Badge({ children, tone }) {
  const t = tone || STATUS_TONE[children] || 'gray';
  return (
    <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-semibold ${TONES[t]}`}>
      {children}
    </span>
  );
}
