import http from './http';

export async function submit(body) {
  const res = await http.post('/re-evaluation-requests', body);
  return res.data.data;
}
export async function resolve(id) {
  const res = await http.patch(`/re-evaluation-requests/${id}/resolve`);
  return res.data.data;
}
