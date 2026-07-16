import http from './http';

export async function assign(body) {
  const res = await http.post('/invigilator-assignments', body);
  return res.data.data;
}
export async function list(params) {
  const res = await http.get('/invigilator-assignments', { params });
  return res.data.data;
}
