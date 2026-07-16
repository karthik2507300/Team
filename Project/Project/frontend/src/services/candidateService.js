import http from './http';

export async function create(body) {
  const res = await http.post('/candidates', body);
  return res.data.data;
}
export async function get(id) {
  const res = await http.get(`/candidates/${id}`);
  return res.data.data;
}
export async function me() {
  const res = await http.get('/candidates/me');
  return res.data.data;
}
export async function update(id, body) {
  const res = await http.patch(`/candidates/${id}`, body);
  return res.data.data;
}
