import http from './http';

export async function create(body) {
  const res = await http.post('/enrolments', body);
  return res.data.data;
}
export async function get(id) {
  const res = await http.get(`/enrolments/${id}`);
  return res.data.data;
}
export async function list(params) {
  const res = await http.get('/enrolments', { params });
  return res.data.data;
}
export async function updateEligibility(id, eligibilityStatus) {
  const res = await http.patch(`/enrolments/${id}/eligibility`, { eligibilityStatus });
  return res.data.data;
}
