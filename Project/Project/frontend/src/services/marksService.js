import http from './http';

export async function submit(body) {
  const res = await http.post('/marks-entries', body);
  return res.data.data;
}
export async function list(params) {
  const res = await http.get('/marks-entries', { params });
  return res.data.data;
}
export async function verify(id) {
  const res = await http.patch(`/marks-entries/${id}/verify`);
  return res.data.data;
}
export async function moderate(id) {
  const res = await http.patch(`/marks-entries/${id}/moderate`);
  return res.data.data;
}
