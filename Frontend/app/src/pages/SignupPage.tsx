import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { BackgroundSlideshow } from "../components/BackgroundSlideshow";
import { AuthCard } from "../components/AuthCard";
import { authApi, ApiError } from "../lib/api";
import { useAuth } from "../lib/AuthContext";
import { TRACK_OPTIONS } from "../lib/types";

export function SignupPage() {
  const navigate = useNavigate();
  const { setSession } = useAuth();
  const [studentId, setStudentId] = useState("");
  const [password, setPassword] = useState("");
  const [track, setTrack] = useState("");
  const [agree, setAgree] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!agree) {
      setError("개인정보 수집 및 이용에 동의해 주세요.");
      return;
    }
    if (!track) {
      setError("트랙을 선택해 주세요.");
      return;
    }

    setLoading(true);
    try {
      const tokens = await authApi.signup(studentId, password, track);
      setSession(tokens.accessToken, tokens.refreshToken);
      navigate("/");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "서버와 연결할 수 없습니다.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="relative flex min-h-screen items-center justify-center px-4 py-12">
      <BackgroundSlideshow />

      <div className="relative w-full max-w-[440px]">
        <div className="mb-7 flex flex-col items-center text-center">
          <img src="/images/cai-roadmap-logo.png" alt="CAI Roadmap" className="h-14 w-auto drop-shadow-lg" />
          <p className="mt-3 text-[14px] font-medium text-cream/80">회원가입하고 나의 학업 로드맵을 확인해보세요</p>
        </div>

        <AuthCard>
          <form className="space-y-4" onSubmit={handleSubmit}>
            <div className="max-h-40 overflow-y-auto rounded-lg border border-ink/10 bg-white/70 p-3 text-[12px] leading-relaxed text-ink-soft">
              <strong className="text-ink">개인정보 수집 및 이용 동의</strong>
              <p className="mt-1.5">회원가입 및 학사 관리 목적으로 최소한의 개인정보를 수집합니다.</p>
              <p className="mt-2">
                수집 항목
                <br />
                이름, 학번, 전화번호, 이메일, 학점, 수강 이력
              </p>
              <p className="mt-2">
                목적
                <br />
                서비스 제공 및 학사 관리
              </p>
              <p className="mt-2">
                보유 기간
                <br />
                탈퇴 시 즉시 파기
              </p>
            </div>
            <label className="flex items-center gap-2 text-sm text-ink-soft">
              <input
                type="checkbox"
                checked={agree}
                onChange={(e) => setAgree(e.target.checked)}
                className="h-4 w-4 accent-flame"
              />
              위 내용에 동의합니다.
            </label>

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
              <input
                type="password"
                required
                autoComplete="new-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="비밀번호를 입력하세요"
                className="w-full rounded-xl border border-ink/10 bg-white px-4 py-3 text-[15px] text-ink outline-none placeholder:text-ink/30 focus:border-flame focus:ring-2 focus:ring-flame/25"
              />
            </Field>

            <Field label="트랙 선택">
              <select
                required
                value={track}
                onChange={(e) => setTrack(e.target.value)}
                className="w-full rounded-xl border border-ink/10 bg-white px-4 py-3 text-[15px] text-ink outline-none focus:border-flame focus:ring-2 focus:ring-flame/25"
              >
                <option value="" disabled>
                  트랙을 선택하세요
                </option>
                {TRACK_OPTIONS.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </Field>

            {error && (
              <p className="rounded-lg bg-alert-soft px-3 py-2 text-sm text-alert" role="alert">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="gradient-flame w-full rounded-xl py-3 font-semibold text-white shadow-md shadow-flame/30 transition hover:brightness-105 active:brightness-95 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? "가입 처리 중…" : "회원가입"}
            </button>

            <p className="text-center text-[12px] text-ink-soft/70">아이디와 비밀번호는 nDRIMS 정보와 같습니다.</p>

            <p className="text-center text-sm text-ink-soft">
              이미 계정이 있으신가요?{" "}
              <Link to="/login" className="font-semibold text-flame hover:underline">
                로그인
              </Link>
            </p>
          </form>
        </AuthCard>
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
