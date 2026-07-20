import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, Link } from "react-router-dom";
import { BackgroundSlideshow } from "../components/BackgroundSlideshow";
import { AuthCard } from "../components/AuthCard";
import { authApi, ApiError } from "../lib/api";
import { useAuth } from "../lib/AuthContext";

const REMEMBER_KEY = "cai_remember_id";

export function LoginPage() {
  const navigate = useNavigate();
  const { setSession } = useAuth();
  const [studentId, setStudentId] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [remember, setRemember] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [slowNotice, setSlowNotice] = useState(false);

  useEffect(() => {
    const savedId = localStorage.getItem(REMEMBER_KEY);
    if (savedId) {
      setStudentId(savedId);
      setRemember(true);
    }
  }, []);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);

    const slowTimer = setTimeout(() => setSlowNotice(true), 5000);

    try {
      const tokens = await authApi.login(studentId, password);
      setSession(tokens.accessToken, tokens.refreshToken);

      if (remember) localStorage.setItem(REMEMBER_KEY, studentId);
      else localStorage.removeItem(REMEMBER_KEY);

      navigate("/");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "서버와 연결할 수 없습니다.");
    } finally {
      clearTimeout(slowTimer);
      setSlowNotice(false);
      setLoading(false);
    }
  }

  return (
    <div className="relative flex min-h-screen items-center justify-center px-4 py-12">
      <BackgroundSlideshow />

      <div className="relative w-full max-w-[400px]">
        <div className="mb-7 flex flex-col items-center text-center">
          <img src="/images/cai-roadmap-logo.png" alt="CAI Roadmap" className="h-16 w-auto drop-shadow-lg" />
          <p className="mt-3 text-[14px] font-medium text-cream/80">동국대 컴퓨터·AI학부 학업 설계도</p>
        </div>

        <AuthCard>
          <form className="space-y-4" onSubmit={handleSubmit}>
            <Field label="학번">
              <input
                type="text"
                required
                autoComplete="username"
                value={studentId}
                onChange={(e) => setStudentId(e.target.value)}
                placeholder="학번을 입력하세요"
                className="w-full rounded-xl border border-ink/10 bg-white px-4 py-3 text-[15px] text-ink outline-none placeholder:text-ink/30 focus:border-flame focus:ring-2 focus:ring-flame/25"
              />
            </Field>

            <Field label="비밀번호">
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  required
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="비밀번호를 입력하세요"
                  className="w-full rounded-xl border border-ink/10 bg-white px-4 py-3 pr-11 text-[15px] text-ink outline-none placeholder:text-ink/30 focus:border-flame focus:ring-2 focus:ring-flame/25"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  aria-label="비밀번호 표시 전환"
                  className="absolute inset-y-0 right-3 flex items-center text-ink/35 hover:text-ink/70"
                >
                  <EyeIcon open={showPassword} />
                </button>
              </div>
            </Field>

            <label className="flex items-center gap-2 text-sm text-ink-soft">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
                className="h-4 w-4 accent-flame"
              />
              학번 저장
            </label>

            {error && (
              <p className="rounded-lg bg-alert-soft px-3 py-2 text-sm text-alert" role="alert">
                {error}
              </p>
            )}
            {slowNotice && (
              <p className="rounded-lg bg-cream-3 px-3 py-2 text-sm text-ink-soft">
                첫 로그인은 데이터 동기화로 30초 정도 걸릴 수 있어요.
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="gradient-flame w-full rounded-xl py-3 font-semibold text-white shadow-md shadow-flame/30 transition hover:brightness-105 active:brightness-95 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? "확인 중…" : "로그인"}
            </button>

            <p className="text-center text-sm text-ink-soft">
              계정이 없으신가요?{" "}
              <Link to="/signup" className="font-semibold text-flame hover:underline">
                회원가입
              </Link>
            </p>
          </form>
        </AuthCard>

        <p className="mt-6 text-center text-[12px] text-cream/50">© TEAM NEXCODE · Dongguk University</p>
      </div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-[13px] font-medium text-ink-soft">{label}</span>
      {children}
    </label>
  );
}

function EyeIcon({ open }: { open: boolean }) {
  if (open) {
    return (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-5 w-5">
        <path d="M3 3l18 18M10.58 10.58a3 3 0 104.24 4.24" strokeLinecap="round" />
        <path
          d="M9.88 5.09A10.94 10.94 0 0112 5c7 0 11 7 11 7a19.79 19.79 0 01-4.27 4.73M6.61 6.61A19.79 19.79 0 001 12s4 7 11 7a10.94 10.94 0 003.12-.46"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    );
  }
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-5 w-5">
      <path d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
      <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
    </svg>
  );
}
