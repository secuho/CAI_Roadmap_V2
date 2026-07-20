import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { tokenStore, authApi } from "./api";
import { decodeJwtSubject } from "./jwt";

interface AuthContextValue {
  studentId: string | null;
  isAuthenticated: boolean;
  setSession: (accessToken: string, refreshToken: string) => void;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [studentId, setStudentId] = useState<string | null>(() => {
    const token = tokenStore.getAccess();
    return token ? decodeJwtSubject(token) : null;
  });

  const value = useMemo<AuthContextValue>(
    () => ({
      studentId,
      isAuthenticated: !!studentId,
      setSession: (accessToken, refreshToken) => {
        tokenStore.set(accessToken, refreshToken);
        setStudentId(decodeJwtSubject(accessToken));
      },
      logout: async () => {
        await authApi.logout();
        setStudentId(null);
      },
    }),
    [studentId]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}

export function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return <>{children}</>;
}
