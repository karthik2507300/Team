import http from './http';

export async function getUser(id) {
  const res = await http.get(`/users/${id}`);
  return res.data.data;
}
export async function listUsers(params) {
  const res = await http.get('/users', { params });
  return res.data.data;
}
export async function createStaff(body) {
  const res = await http.post('/users', body);
  return res.data.data;
}
export async function updateUser(id, body) {
  const res = await http.patch(`/users/${id}`, body);
  return res.data.data;
}
export async function updateUserStatus(id, status) {
  const res = await http.patch(`/users/${id}/status`, { status });
  return res.data.data;
}
