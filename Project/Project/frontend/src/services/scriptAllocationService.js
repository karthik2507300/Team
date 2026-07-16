import http from './http';

export async function assign(body) {
  const res = await http.post('/script-allocations', body);
  return res.data.data;
}
export async function list(params) {
  const res = await http.get('/script-allocations', { params });
  return res.data.data;
}
