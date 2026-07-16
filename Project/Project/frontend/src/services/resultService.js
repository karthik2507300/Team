import http from './http';

export async function compute(windowId) {
  const res = await http.post(`/results/compute/${windowId}`);
  return res.data.data;
}
export async function publish(id) {
  const res = await http.patch(`/results/${id}/publish`);
  return res.data.data;
}
export async function view(params) {
  const res = await http.get('/results', { params });
  return res.data.data;
}
