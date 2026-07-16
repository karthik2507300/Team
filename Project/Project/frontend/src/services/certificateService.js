import http from './http';

export async function issue(body) {
  const res = await http.post('/certificates', body);
  return res.data.data;
}
export async function byCandidate(candidateId) {
  const res = await http.get(`/certificates/${candidateId}`);
  return res.data.data;
}
export async function expiring() {
  const res = await http.get('/certificates/expiring');
  return res.data.data;
}
export async function updateStatus(id, status) {
  const res = await http.patch(`/certificates/${id}/status`, { status });
  return res.data.data;
}
