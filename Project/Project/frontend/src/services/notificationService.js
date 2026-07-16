import http from './http';

export async function list(params) {
  const res = await http.get('/notifications', { params });
  return res.data.data;
}
export async function markRead(id) {
  const res = await http.patch(`/notifications/${id}/read`);
  return res.data.data;
}
export async function create(body) {
  const res = await http.post('/notifications', body);
  return res.data.data;
}
