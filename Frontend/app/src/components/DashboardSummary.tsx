import { useEffect, useState } from "react";
import { checklistApi, myPageApi } from "../lib/api";

export function DashboardSummary({ studentId }: { studentId: string | null }) {
  const [totalCredit, setTotalCredit] = useState<number | null>(null);
  const [gpa, setGpa] = useState<number | null>(null);
  const [track, setTrack] = useState<string | null>(null);

  useEffect(() => {
    checklistApi
      .get()
      .then((d) => {
        setTotalCredit(d.totalCredit);
        setGpa(d.gpa);
      })
      .catch(() => {});
    myPageApi
      .get()
      .then((d) => setTrack(d.track))
      .catch(() => {});
  }, []);

  const pct = totalCredit !== null ? Math.min(100, Math.round((totalCredit / 130) * 100)) : 0;
  const remaining = totalCredit !== null ? Math.max(0, 130 - totalCredit) : null;

  return (
    <div className="border-b border-ink/5 bg-white">
      <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6">
        <div className="flex flex-wrap items-end justify-between gap-x-8 gap-y-3">
          <div>
            <p className="text-sm text-ink-soft">{studentId ?? "학생"}님</p>
            <h1 className="text-xl font-bold text-ink sm:text-2xl">
              {remaining === null ? (
                "환영합니다"
              ) : remaining === 0 ? (
                "졸업 요건을 모두 채웠어요"
              ) : (
                <>
                  졸업까지 <span className="text-gradient-flame">{remaining}학점</span> 남았어요
                </>
              )}
            </h1>
          </div>
          <div className="flex flex-wrap gap-x-7 gap-y-2">
            <Stat label="취득학점" value={totalCredit !== null ? `${totalCredit}/130` : "—"} />
            <Stat label="평점평균" value={gpa !== null ? gpa.toFixed(2) : "—"} />
            <Stat label="트랙" value={track ?? "—"} />
          </div>
        </div>
        <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-cream-3">
          <div
            className="gradient-flame h-full rounded-full transition-all duration-700"
            style={{ width: `${pct}%` }}
          />
        </div>
      </div>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="text-right">
      <p className="text-[11px] text-ink-soft">{label}</p>
      <p className="max-w-[9rem] truncate text-base font-bold text-ink">{value}</p>
    </div>
  );
}
