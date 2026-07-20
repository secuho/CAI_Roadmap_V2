import { useEffect, useState } from "react";
import { checklistApi } from "../lib/api";
import type { ChecklistResponse } from "../lib/types";

interface Item {
  key: string;
  label: string;
  current: number;
  goal: number;
  done: boolean;
  suffix?: string;
}

const COMMON_AREAS: { key: keyof ChecklistResponse; missingKey: keyof ChecklistResponse; label: string }[] = [
  { key: "commonDonggukAttitude", missingKey: "missingCommonDonggukAttitude", label: "동국인성" },
  { key: "commonSelfDev", missingKey: "missingCommonSelfDev", label: "자기개발" },
  { key: "commonThinkncom", missingKey: "missingCommonThinkncom", label: "사고와소통" },
  { key: "commonCreative", missingKey: "missingCommonCreative", label: "창의융합" },
  { key: "commonDigitaliter", missingKey: "missingCommonDigitaliter", label: "디지털리터러시" },
];

export function ChecklistPanel() {
  const [data, setData] = useState<ChecklistResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [showCommonDetail, setShowCommonDetail] = useState(false);

  useEffect(() => {
    checklistApi
      .get()
      .then(setData)
      .catch(() => setData(null))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="rounded-2xl bg-white p-6 text-center text-sm text-ink-soft shadow-sm">
        체크리스트를 불러오는 중…
      </div>
    );
  }
  if (!data) {
    return (
      <div className="rounded-2xl bg-white p-6 text-center text-sm text-alert shadow-sm">
        체크리스트를 불러오지 못했습니다.
      </div>
    );
  }

  const items: Item[] = [
    { key: "major", label: "전공", current: data.major, goal: 72, done: data.major >= 72, suffix: "학점" },
    { key: "common", label: "공통교양", current: data.common, goal: 25, done: data.common >= 25, suffix: "학점" },
    { key: "basic", label: "기본소양", current: data.basic, goal: 6, done: data.basic >= 6, suffix: "학점" },
    { key: "bsm", label: "BSM", current: data.bsm, goal: 21, done: data.bsm >= 21, suffix: "학점" },
    {
      key: "total",
      label: "취득학점",
      current: data.totalCredit,
      goal: 130,
      done: data.totalCredit >= 130,
      suffix: "학점",
    },
    { key: "gpa", label: "평점평균", current: data.gpa, goal: 2.0, done: data.gpa >= 2.0 },
    { key: "eng", label: "영어강의", current: data.engCredit, goal: 4, done: data.engCredit >= 4, suffix: "개" },
    { key: "design1", label: "종합설계1", current: data.design1, goal: 1, done: data.design1 >= 1 },
    { key: "design2", label: "종합설계2", current: data.design2, goal: 1, done: data.design2 >= 1 },
  ];

  const completedCount = items.filter((i) => i.done).length;
  const radius = 32;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (completedCount / items.length) * circumference;

  return (
    <div className="relative">
      <div className="absolute -top-3 left-1/2 h-6 w-24 -translate-x-1/2 rounded-md bg-gradient-to-b from-ink-soft/30 to-ink-soft/50 shadow-sm" />
      <div className="rounded-2xl bg-white p-5 pt-7 shadow-md shadow-ink/5">
        <div className="mb-4 flex items-center gap-4">
          <div className="relative h-20 w-20 shrink-0">
            <svg viewBox="0 0 80 80" className="h-20 w-20 -rotate-90">
              <circle cx="40" cy="40" r={radius} fill="none" stroke="var(--color-cream-3)" strokeWidth="7" />
              <circle
                cx="40"
                cy="40"
                r={radius}
                fill="none"
                stroke="url(#checklistRingGradient)"
                strokeWidth="7"
                strokeLinecap="round"
                strokeDasharray={circumference}
                strokeDashoffset={offset}
                style={{ transition: "stroke-dashoffset 700ms cubic-bezier(.65,0,.35,1)" }}
              />
              <defs>
                <linearGradient id="checklistRingGradient" x1="0%" y1="100%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#f2711c" />
                  <stop offset="100%" stopColor="#f5b93d" />
                </linearGradient>
              </defs>
            </svg>
            <div className="absolute inset-0 grid place-items-center">
              <span className="text-base font-bold text-ink">
                {completedCount}/{items.length}
              </span>
            </div>
          </div>
          <div>
            <h2 className="text-lg font-bold text-ink">졸업 체크리스트</h2>
            <p className="text-xs text-ink-soft">항목을 눌러 세부 현황을 확인하세요</p>
          </div>
        </div>

        <ul className="space-y-1">
          {items.map((item) => (
            <li key={item.key}>
              <button
                type="button"
                onClick={() => item.key === "common" && setShowCommonDetail((v) => !v)}
                className={`flex w-full items-center gap-2.5 rounded-lg px-2 py-1.5 text-left transition ${
                  item.key === "common" ? "cursor-pointer hover:bg-cream-2/70" : "cursor-default"
                }`}
              >
                <Check done={item.done} />
                <span className={`flex-1 text-[14px] ${item.done ? "text-ink-soft" : "text-ink"}`}>
                  {item.label}
                </span>
                <span className="text-xs text-ink-soft/70">
                  {formatValue(item.current)}/{formatValue(item.goal)}
                  {item.suffix ?? ""}
                </span>
              </button>
            </li>
          ))}
        </ul>

        {showCommonDetail && (
          <div className="mt-3 space-y-2 rounded-xl bg-cream/70 p-3">
            <p className="text-xs font-semibold text-ink-soft">공통교양 세부 영역</p>
            {COMMON_AREAS.map((area) => {
              const done = data[area.key] as boolean;
              const missing = data[area.missingKey] as string[];
              return (
                <div key={area.label} className="flex items-start gap-2 text-[13px]">
                  <Check done={done} small />
                  <div>
                    <span className={done ? "text-ink-soft" : "text-ink"}>{area.label}</span>
                    {!done && missing?.length > 0 && (
                      <p className="text-[11px] text-alert/80">미이수: {missing.join(", ")}</p>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {(data.missingBasicCourses.length > 0 || data.missingBsmCourses.length > 0) && (
          <div className="mt-3 rounded-xl bg-alert-soft p-3 text-[12px] text-alert">
            <p className="mb-1 font-semibold">미이수 필수 과목</p>
            <p>{[...data.missingBasicCourses, ...data.missingBsmCourses].join(", ")}</p>
          </div>
        )}
      </div>
    </div>
  );
}

function Check({ done, small }: { done: boolean; small?: boolean }) {
  const size = small ? "h-4 w-4" : "h-5 w-5";
  return (
    <span
      className={`grid ${size} shrink-0 place-items-center rounded-full border text-[10px] font-bold ${
        done ? "border-verified bg-verified-soft text-verified" : "border-ink/15 text-transparent"
      }`}
    >
      ✓
    </span>
  );
}

function formatValue(n: number) {
  return Number.isInteger(n) ? n : n.toFixed(2);
}
