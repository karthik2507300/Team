import http from './http';

export async function list(params) {
  const res = await http.get('/programs', { params });
  return res.data.data;
}
export async function create(body) {
  const res = await http.post('/programs', body);
  return res.data.data;
}
export async function update(id, body) {
  const res = await http.patch(`/programs/${id}`, body);
  return res.data.data;
}
export async function setGradingScale(id, bands) {
  const res = await http.post(`/programs/${id}/grading-scale`, { bands });
  return res.data.data;
}
export async function getGradingScale(id) {
  const res = await http.get(`/programs/${id}/grading-scale`);
  return res.data.data;
}
