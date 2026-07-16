import http from './http';

export async function list(params) {
  const res = await http.get('/reports', { params });
  return res.data.data;
}
export async function generate(body) {
  const res = await http.post('/reports/generate', body);
  return res.data.data;
}
