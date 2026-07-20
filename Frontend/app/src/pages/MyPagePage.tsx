import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AppHeader } from "../components/AppHeader";
import { PasswordModal } from "../components/PasswordModal";
import { myPageApi, authApi, ApiError } from "../lib/api";
import { useAuth } from "../lib/AuthContext";
import type { MyPageResponse } from "../lib/types";
import { TRACK_OPTIONS } from "../lib/types";

export function MyPagePage() {
  const { studentId, logout } = useAuth();
  const navigate = useNavigate();
  const [data, setData] = useState<MyPageResponse | null>(null);
  const [editingTrack, setEditingTrack] = useState(false);
  const [trackDraft, setTrackDraft] = useState("");
  const [savingTrack, setSavingTrack] = useState(false);
  const [withdrawOpen, setWithdrawOpen] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    myPageApi.get().then(setData).catch(() => setData(null));
  }, []);

  async function handleSaveTrack() {
    setSavingTrack(true);
    try {
      await myPageApi.updateTrack(trackDraft);
      setData((d) => (d ? { ...d, track: trackDraft } : d));
      setEditingTrack(false);
      setNotice("트랙 정보가 수정되었습니다.");
    } catch (err) {
      setNotice(err instanceof ApiError ? err.message : "수정에 실패했습니다.");
    } finally {
      setSavingTrack(false);
    }
  }

  async function handleWithdraw(password: string) {
    await authApi.withdraw(password);
    await logout();
    navigate("/login");
  }

  return (
    <div className="min-h-screen bg-cream">
      <AppHeader />

      <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
        <div className="rounded-2xl bg-white p-6 shadow-sm sm:p-8">
          <h2 className="mb-6 border-b border-ink/10 pb-4 text-2xl font-bold text-ink">나의 정보</h2>

          {!data ? (
            <p className="text-ink-soft">불러오는 중…</p>
          ) : (
            <div className="space-y-8">
              <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
                <InfoField label="학번" value={studentId ?? "-"} />
                <InfoField label="이메일" value={data.email ?? "-"} />
                <InfoField label="전화번호" value={data.phone ?? "-"} />
              </div>

              <div>
                <div className="mb-3 flex items-center justify-between border-b border-ink/10 pb-3">
                  <h3 className="text-lg font-bold text-ink">부가 정보</h3>
                  {!editingTrack && (
                    <button
                      onClick={() => {
                        setTrackDraft(data.track ?? "선택 안 함");
                        setEditingTrack(true);
                      }}
                      className="text-sm font-semibold text-flame hover:underline"
                    >
                      수정
                    </button>
                  )}
                </div>

                {!editingTrack ? (
                  <InfoField label="선택한 트랙" value={data.track ?? "선택 안 함"} />
                ) : (
                  <div className="space-y-3">
                    <select
                      value={trackDraft}
                      onChange={(e) => setTrackDraft(e.target.value)}
                      className="w-full rounded-xl border border-ink/10 px-3 py-2.5 text-[15px] outline-none focus:border-flame focus:ring-2 focus:ring-flame/25"
                    >
                      {TRACK_OPTIONS.map((t) => (
                        <option key={t} value={t}>
                          {t}
                        </option>
                      ))}
                    </select>
                    <div className="flex justify-end gap-2">
                      <button
                        onClick={() => setEditingTrack(false)}
                        className="rounded-xl bg-cream-2 px-4 py-2 text-sm font-semibold text-ink-soft hover:bg-cream-3"
                      >
                        취소
                      </button>
                      <button
                        onClick={handleSaveTrack}
                        disabled={savingTrack}
                        className="gradient-flame rounded-xl px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
                      >
                        {savingTrack ? "저장 중…" : "수정 완료"}
                      </button>
                    </div>
                  </div>
                )}
              </div>

              {notice && <p className="rounded-lg bg-cream-2 px-3 py-2 text-sm text-ink-soft">{notice}</p>}

              <div className="border-t border-ink/10 pt-6">
                <h3 className="mb-3 text-lg font-bold text-ink">서비스</h3>
                <button
                  onClick={() => setWithdrawOpen(true)}
                  className="rounded-lg bg-alert px-4 py-2 text-sm font-semibold text-white hover:brightness-95"
                >
                  서비스 탈퇴
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      <PasswordModal
        open={withdrawOpen}
        title="회원 탈퇴 확인"
        confirmLabel="탈퇴하기"
        onCancel={() => setWithdrawOpen(false)}
        onConfirm={handleWithdraw}
      />
    </div>
  );
}

function InfoField({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p className="text-sm font-semibold text-ink-soft">{label}</p>
      <p className="mt-1 text-[15px] text-ink">{value}</p>
    </div>
  );
}
