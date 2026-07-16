import http from './http';

export async function create(body) {
  const res = await http.post('/questions', body);
  return res.data.data;
}
export async function list(params) {
  const res = await http.get('/questions', { params });
  return res.data.data;
}
export async function update(id, body) {
  const res = await http.patch(`/questions/${id}`, body);
  return res.data.data;
}
