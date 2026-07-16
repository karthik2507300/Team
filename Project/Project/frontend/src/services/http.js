import axios from 'axios';

// All requests go through the API gateway (single entry point for all microservices).
const BASE_URL = 'http://localhost:8098';
const STORAGE_KEY = 'certifypro_auth';

export function getAuth() {
  const raw = localStorage.getItem(STORAGE_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function setAuth(data) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
}

export function clearAuth() {
  localStorage.removeItem(STORAGE_KEY);
}

const http = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

// Attach access token on every request.
http.interceptors.request.use((config) => {
  const auth = getAuth();
  if (auth?.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`;
  }
  return config;
});

// On 401, try a one-shot refresh, then retry the original request.
let refreshing = null;

http.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    const status = error.response?.status;
    const isAuthCall = original?.url?.includes('/auth/');

    if (status === 401 && !original._retry && !isAuthCall) {
      original._retry = true;
      const auth = getAuth();
      if (!auth?.refreshToken) {
        clearAuth();
        redirectToLogin();
        return Promise.reject(error);
      }
      try {
        if (!refreshing) {
          refreshing = axios.post(`${BASE_URL}/api/auth/refresh-token`, {
            refreshToken: auth.refreshToken,
          });
        }
        const resp = await refreshing;
        refreshing = null;
        const data = resp.data.data;
        setAuth({ ...auth, accessToken: data.accessToken, refreshToken: data.refreshToken });
        original.headers.Authorization = `Bearer ${data.accessToken}`;
        return http(original);
      } catch (e) {
        refreshing = null;
        clearAuth();
        redirectToLogin();
        return Promise.reject(e);
      }
    }
    return Promise.reject(error);
  }
);

function redirectToLogin() {
  if (window.location.pathname !== '/login') {
    window.location.href = '/login';
  }
}

/** Extracts a friendly message from the API error envelope. */
export function errorMessage(err, fallback = 'Something went wrong') {
  const data = err?.response?.data;
  if (data?.errors?.length) return data.errors.join(', ');
  return data?.message || err?.message || fallback;
}

export default http;
