import http from './http';

export async function list(params) {
  const res = await http.get('/exam-windows', { params });
  return res.data.data;
}
export async function get(id) {
  const res = await http.get(`/exam-windows/${id}`);
  return res.data.data;
}
export async function create(body) {
  const res = await http.post('/exam-windows', body);
  return res.data.data;
}
export async function update(id, body) {
  const res = await http.patch(`/exam-windows/${id}`, body);
  return res.data.data;
}
