import http from './http';

export async function allocate(body) {
  const res = await http.post('/seat-allocations', body);
  return res.data.data;
}
export async function byCandidate(candidateId) {
  const res = await http.get(`/seat-allocations/${candidateId}`);
  return res.data.data;
}
export async function listByWindowCentre(params) {
  const res = await http.get('/seat-allocations', { params });
  return res.data.data;
}
export async function updateStatus(id, status) {
  const res = await http.patch(`/seat-allocations/${id}/status`, { status });
  return res.data.data;
}
export async function downloadHallTicket(id) {
  const res = await http.get(`/seat-allocations/${id}/hall-ticket/pdf`, { responseType: 'blob' });
  return res.data;
}
