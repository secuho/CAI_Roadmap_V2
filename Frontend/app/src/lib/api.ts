import type {
  ChecklistResponse,
  TermSummaryResponse,
  CurrentCourseResponse,
  CompletedCourseResponse,
  MyPageResponse,
  RecommendResponse,
} from "./types";

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export interface ApiResponse<T> {
  status: "success" | "fail";
  message: string | null;
  data: T;
}

export class ApiError extends Error {
  status: number;
  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}

const ACCESS_KEY = "cai_access_token";
const REFRESH_KEY = "cai_refresh_token";

export const tokenStore = {
  getAccess: () => localStorage.getItem(ACCESS_KEY),
  getRefresh: () => localStorage.getItem(REFRESH_KEY),
  set(accessToken: string, refreshToken: string) {
    localStorage.setItem(ACCESS_KEY, accessToken);
    localStorage.setItem(REFRESH_KEY, refreshToken);
  },
  clear() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
  },
  isAuthenticated: () => !!localStorage.getItem(ACCESS_KEY),
};

interface RequestOptions {
  method?: string;
  body?: unknown;
  auth?: boolean;
  params?: Record<string, string | number | undefined>;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, auth = true, params } = options;

  let url = `${API_BASE}${path}`;
  if (params) {
    const qs = new URLSearchParams(
      Object.entries(params).filter(([, v]) => v !== undefined) as [string, string][]
    ).toString();
    if (qs) url += `?${qs}`;
  }

  const headers: Record<string, string> = { "Content-Type": "application/json" };
  if (auth) {
    const token = tokenStore.getAccess();
    if (token) headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(url, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  // 401 on an authenticated call → try one silent refresh, then retry once.
  if (res.status === 401 && auth) {
    const refreshed = await tryRefresh();
    if (refreshed) {
      return request<T>(path, options);
    }
    tokenStore.clear();
  }

  const json: ApiResponse<T> = await res.json().catch(() => ({
    status: "fail",
    message: `서버 응답을 해석할 수 없습니다. (HTTP ${res.status})`,
    data: null as T,
  }));

  if (!res.ok || json.status === "fail") {
    throw new ApiError(json.message ?? "요청 처리 중 오류가 발생했습니다.", res.status);
  }

  return json.data;
}

let refreshInFlight: Promise<boolean> | null = null;

async function tryRefresh(): Promise<boolean> {
  const refreshToken = tokenStore.getRefresh();
  if (!refreshToken) return false;

  if (!refreshInFlight) {
    refreshInFlight = (async () => {
      try {
        const res = await fetch(`${API_BASE}/auth/refresh`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refreshToken }),
        });
        if (!res.ok) return false;
        const json: ApiResponse<TokenResponse> = await res.json();
        if (json.status !== "success") return false;
        tokenStore.set(json.data.accessToken, json.data.refreshToken);
        return true;
      } catch {
        return false;
      } finally {
        refreshInFlight = null;
      }
    })();
  }
  return refreshInFlight;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export const authApi = {
  login: (studentId: string, password: string) =>
    request<TokenResponse>("/auth/login", { method: "POST", auth: false, body: { studentId, password } }),

  signup: (studentId: string, password: string, track: string) =>
    request<TokenResponse>("/auth/account", { method: "POST", auth: false, body: { studentId, password, track } }),

  logout: async () => {
    const refreshToken = tokenStore.getRefresh();
    try {
      if (refreshToken) {
        await request<void>("/auth/logout", { method: "POST", body: { refreshToken } });
      }
    } finally {
      tokenStore.clear();
    }
  },

  withdraw: (password: string) => request<void>("/auth/account", { method: "DELETE", body: { password } }),
};

export const checklistApi = {
  get: () => request<ChecklistResponse>("/api/checklist"),
};

export const courseApi = {
  term: () => request<TermSummaryResponse[]>("/api/term"),

  currentCourses: (year: number, term: string) =>
    request<CurrentCourseResponse[]>("/api/current-courses", { params: { year, term } }),

  completedCourses: () => request<CompletedCourseResponse[]>("/api/completed-courses"),

  allCourses: () => request<string[]>("/api/all-courses"),
};

export const myPageApi = {
  get: () => request<MyPageResponse>("/api/mypage"),
  updateTrack: (track: string) => request<void>("/api/mytrack", { method: "PATCH", body: { track } }),
};

export const recommendApi = {
  get: (nextTerm: string) => request<RecommendResponse>("/api/recommends", { params: { nextTerm } }),
};

export { request };
