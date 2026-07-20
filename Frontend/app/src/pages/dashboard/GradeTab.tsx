import { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
  Filler,
} from "chart.js";
import { courseApi } from "../../lib/api";
import type { TermSummaryResponse, CompletedCourseResponse } from "../../lib/types";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Filler);

const GRADE_POINTS: Record<string, number> = {
  "A+": 4.5,
  A0: 4.0,
  A: 4.0,
  "B+": 3.5,
  B0: 3.0,
  B: 3.0,
  "C+": 2.5,
  C0: 2.0,
  C: 2.0,
  "D+": 1.5,
  D0: 1.0,
  D: 1.0,
  F: 0,
};

function weightedGpa(rows: { gradeCd: string | null; credit: number | null }[]) {
  let num = 0;
  let den = 0;
  for (const r of rows) {
    const grade = r.gradeCd?.replace(/\s+/g, "").toUpperCase();
    const point = grade ? GRADE_POINTS[grade] : undefined;
    const credit = r.credit ?? 0;
    if (point === undefined || credit <= 0) continue;
    num += point * credit;
    den += credit;
  }
  return den > 0 ? num / den : null;
}

function fmt(v: number | null) {
  return v !== null ? `${v.toFixed(2)} / 4.5` : "—";
}

export function GradeTab() {
  const [terms, setTerms] = useState<TermSummaryResponse[] | null>(null);
  const [completed, setCompleted] = useState<CompletedCourseResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([courseApi.term(), courseApi.completedCourses()])
      .then(([t, c]) => {
        setTerms(t);
        setCompleted(c);
      })
      .catch(() => setError("학점 정보를 불러오지 못했습니다."));
  }, []);

  if (error) return <p className="p-4 text-alert">{error}</p>;
  if (!terms || !completed) return <p className="p-4 text-ink-soft">불러오는 중…</p>;
  if (terms.length === 0) return <p className="p-4 text-ink-soft">학점 정보가 없습니다.</p>;

  const sorted = [...terms].sort((a, b) => (a.year - b.year) || a.semCd.localeCompare(b.semCd));
  const labels = sorted.map((t) => t.yySemName);
  const values = sorted.map((t) => t.gpa ?? NaN);

  const lastSemGpa = sorted.at(-1)?.gpa ?? null;
  const gpas = sorted.map((t) => t.gpa).filter((g): g is number => g !== null);
  const overallGpa = gpas.length ? gpas.reduce((a, b) => a + b, 0) / gpas.length : null;
  const majorGpa = weightedGpa(completed.filter((c) => c.cpdvNm === "전공"));

  return (
    <div>
      <h3 className="mb-4 text-lg font-bold text-ink">나의 학점</h3>

      <div className="rounded-xl border border-ink/5 bg-white p-4">
        <Line
          data={{
            labels,
            datasets: [
              {
                label: "학기 평점",
                data: values,
                borderColor: "#f2711c",
                backgroundColor: "rgba(242,113,28,0.15)",
                fill: true,
                tension: 0.3,
                pointRadius: 4,
                pointBackgroundColor: "#f2711c",
              },
            ],
          }}
          options={{
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { min: 0, max: 4.5, ticks: { stepSize: 0.5 } } },
          }}
        />
      </div>

      <div className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-3">
        <SummaryCard label="직전 학기" value={fmt(lastSemGpa)} />
        <SummaryCard label="전공 가중 평균" value={fmt(majorGpa)} />
        <SummaryCard label="총 누적 (학기 산술평균)" value={fmt(overallGpa)} />
      </div>
    </div>
  );
}

function SummaryCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-xl border border-flame/15 bg-gradient-to-br from-cream to-cream-2 p-4">
      <p className="text-xs text-ink-soft">{label}</p>
      <p className="mt-1 text-xl font-bold text-ink">{value}</p>
    </div>
  );
}
