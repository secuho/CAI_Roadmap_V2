import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../lib/AuthContext";

export function AppHeader() {
  const { studentId, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  return (
    <>
      <div className="gradient-flame h-1 w-full" />
      <header className="sticky top-0 z-20 w-full border-b border-ink/5 bg-cream/95 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-2.5 sm:px-6">
          <Link to="/" className="flex items-center gap-2">
            <img src="/images/cai-roadmap-logo.png" alt="CAI Roadmap" className="h-8 w-auto" />
          </Link>
          <div className="flex items-center gap-4 text-sm">
            {studentId && (
              <span className="hidden rounded-full bg-cream-2 px-3 py-1 font-medium text-ink-soft sm:inline">
                {studentId}
              </span>
            )}
            <Link to="/mypage" className="font-semibold text-ink-soft transition hover:text-flame">
              나의 정보
            </Link>
            <button onClick={handleLogout} className="font-semibold text-flame hover:underline">
              로그아웃
            </button>
          </div>
        </div>
      </header>
    </>
  );
}
