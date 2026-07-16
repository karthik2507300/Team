import http, { setAuth, clearAuth, getAuth } from './http';

export async function login(email, password) {
  const res = await http.post('/auth/login', { email, password });
  const data = res.data.data;
  setAuth(data);
  return data;
}

export async function register(payload) {
  const res = await http.post('/auth/register', payload);
  const data = res.data.data;
  setAuth(data);
  return data;
}

export async function logout() {
  try {
    await http.post('/auth/logout');
  } catch (_) {
    /* ignore network errors on logout */
  }
  clearAuth();
}

export { getAuth };
