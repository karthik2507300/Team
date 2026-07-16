export default function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null;
  return (
    <div className="flex items-center justify-between px-2 py-3 text-sm">
      <span className="text-gray-500">
        Page {page + 1} of {totalPages}
      </span>
      <div className="flex gap-2">
        <button className="btn-secondary" disabled={page <= 0} onClick={() => onChange(page - 1)}>
          Previous
        </button>
        <button className="btn-secondary" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}>
          Next
        </button>
      </div>
    </div>
  );
}
