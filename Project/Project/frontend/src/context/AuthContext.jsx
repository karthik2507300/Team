import { createContext, useContext, useState, useMemo } from 'react';
import { getAuth, clearAuth } from '../services/http';
import * as authService from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuthState] = useState(() => getAuth());

  const value = useMemo(
    () => ({
      auth,
      isAuthenticated: !!auth?.accessToken,
      role: auth?.role,
      async login(email, password) {
        const data = await authService.login(email, password);
        setAuthState(data);
        return data;
      },
      async register(payload) {
        const data = await authService.register(payload);
        setAuthState(data);
        return data;
      },
      async logout() {
        await authService.logout();
        setAuthState(null);
      },
      refreshFromStorage() {
        setAuthState(getAuth());
      },
      forceLogout() {
        clearAuth();
        setAuthState(null);
      },
    }),
    [auth]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
