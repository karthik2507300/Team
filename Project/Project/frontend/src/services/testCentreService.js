import http from './http';

export async function list(params) {
  const res = await http.get('/test-centres', { params });
  return res.data.data;
}
export async function create(body) {
  const res = await http.post('/test-centres', body);
  return res.data.data;
}
export async function update(id, body) {
  const res = await http.patch(`/test-centres/${id}`, body);
  return res.data.data;
}
