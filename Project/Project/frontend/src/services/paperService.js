import http from './http';

export async function create(body) {
  const res = await http.post('/question-papers', body);
  return res.data.data;
}
export async function get(id) {
  const res = await http.get(`/question-papers/${id}`);
  return res.data.data;
}
export async function addQuestions(id, items) {
  const res = await http.post(`/question-papers/${id}/questions`, { items });
  return res.data.data;
}
export async function updateStatus(id, status) {
  const res = await http.patch(`/question-papers/${id}/status`, { status });
  return res.data.data;
}
