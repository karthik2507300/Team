import http from './http';

export async function submit(body) {
  const res = await http.post('/renewals', body);
  return res.data.data;
}
export async function get(id) {
  const res = await http.get(`/renewals/${id}`);
  return res.data.data;
}
export async function review(id, body) {
  const res = await http.patch(`/renewals/${id}/review`, body);
  return res.data.data;
}
