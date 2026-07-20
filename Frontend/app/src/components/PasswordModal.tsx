import { useEffect, useRef, useState, type FormEvent } from "react";

interface PasswordModalProps {
  open: boolean;
  title: string;
  confirmLabel?: string;
  onCancel: () => void;
  onConfirm: (password: string) => Promise<void>;
}

export function PasswordModal({ open, title, confirmLabel = "확인", onCancel, onConfirm }: PasswordModalProps) {
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (open) {
      setPassword("");
      setError(null);
      setTimeout(() => inputRef.current?.focus(), 0);
    }
  }, [open]);

  if (!open) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!password.trim()) {
      setError("비밀번호를 입력해주세요.");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await onConfirm(password);
    } catch (err) {
      setError(err instanceof Error ? err.message : "처리 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink/40 px-4">
      <div className="absolute inset-0" onClick={onCancel} />
      <div role="dialog" aria-modal="true" className="relative w-full max-w-[400px] rounded-2xl bg-white p-5 shadow-xl">
        <h3 className="mb-3 text-lg font-bold text-ink">{title}</h3>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div className="relative">
            <input
              ref={inputRef}
              type={show ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
              className="w-full rounded-xl border border-ink/15 px-4 py-2.5 pr-11 text-[15px] outline-none focus:border-flame focus:ring-2 focus:ring-flame/25"
            />
            <button
              type="button"
              onClick={() => setShow((v) => !v)}
              className="absolute inset-y-0 right-3 flex items-center text-ink/35 hover:text-ink/70"
            >
              {show ? "숨김" : "표시"}
            </button>
          </div>
          {error && <p className="text-sm text-alert">{error}</p>}
          <div className="flex justify-end gap-2 pt-1">
            <button
              type="button"
              onClick={onCancel}
              className="rounded-xl border border-ink/15 px-4 py-2 text-sm hover:bg-cream-2"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={loading}
              className="gradient-flame rounded-xl px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
            >
              {loading ? "처리 중…" : confirmLabel}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
