import http from './http';

export async function list(params) {
  const res = await http.get('/audit-logs', { params });
  return res.data.data;
}
