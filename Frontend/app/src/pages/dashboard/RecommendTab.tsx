import { useEffect, useState } from "react";
import { recommendApi } from "../../lib/api";
import type { MajorRecommendation, LiberalArtsRecommendation } from "../../lib/types";

function nextTermLabel(): string {
  const now = new Date();
  const month = now.getMonth() + 1;
  const year = now.getFullYear();
  if (month >= 3 && month <= 8) return `${year}-2`;
  if (month >= 9 && month <= 12) return `${year + 1}-1`;
  return `${year}-1`;
}

export function RecommendTab() {
  const [majors, setMajors] = useState<MajorRecommendation[] | null>(null);
  const [liberalArts, setLiberalArts] = useState<LiberalArtsRecommendation[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const nextTerm = nextTermLabel();

  useEffect(() => {
    recommendApi
      .get(nextTerm)
      .then((data) => {
        setMajors(data.majors);
        setLiberalArts(data.liberalArts);
      })
      .catch(() => setError("추천 강의를 불러오지 못했습니다."));
  }, [nextTerm]);

  if (error) return <p className="p-4 text-alert">{error}</p>;
  if (!majors || !liberalArts) return <p className="p-4 text-ink-soft">추천 데이터를 불러오는 중…</p>;

  return (
    <div className="space-y-8">
      <div>
        <div className="mb-3 flex items-center gap-2">
          <h3 className="text-lg font-bold text-ink">전공 추천</h3>
          <span className="text-sm text-ink-soft">{nextTerm}학기 · {majors.length}건</span>
        </div>
        {majors.length === 0 ? (
          <EmptyState text="아직 추천할 전공 과목이 없습니다. 개설 정보가 갱신되면 다시 확인해보세요." />
        ) : (
          <div className="grid gap-3 sm:grid-cols-2">
            {majors.map((c, i) => (
              <MajorCard key={c.code} rank={i + 1} course={c} />
            ))}
          </div>
        )}
      </div>

      <div>
        <div className="mb-3 flex items-center gap-2">
          <h3 className="text-lg font-bold text-ink">교양 추천</h3>
          <span className="text-sm text-ink-soft">{liberalArts.length}건</span>
        </div>
        {liberalArts.length === 0 ? (
          <EmptyState text="아직 추천할 교양 과목이 없습니다." />
        ) : (
          <div className="grid gap-3 sm:grid-cols-2">
            {liberalArts.map((c) => (
              <LiberalArtsCard key={c.code} course={c} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function MajorCard({ rank, course }: { rank: number; course: MajorRecommendation }) {
  return (
    <div className="rounded-xl border border-flame/15 bg-gradient-to-br from-cream to-cream-2 p-4 transition hover:border-flame/40 hover:shadow-sm">
      <div className="mb-1.5 flex items-center justify-between gap-2">
        <div className="flex min-w-0 items-center gap-2">
          <span className="grid h-6 w-6 shrink-0 place-items-center rounded-full bg-flame text-xs font-bold text-white">
            {rank}
          </span>
          <span className="truncate font-bold text-ink">{course.name}</span>
        </div>
        <span className="shrink-0 text-sm font-medium text-flame">{course.credit ?? "-"}학점</span>
      </div>
      <p className="mb-1 text-xs text-ink-soft">{course.code} · 추천 학년 {course.recommendGrade}학년</p>
      {course.tracks.length > 0 && (
        <p className="mb-1.5 text-xs text-ink-soft">트랙: {course.tracks.join(", ")}</p>
      )}
      {course.description && <p className="text-sm text-ink-soft line-clamp-2">{course.description}</p>}
    </div>
  );
}

function LiberalArtsCard({ course }: { course: LiberalArtsRecommendation }) {
  return (
    <div className="rounded-xl border border-verified/20 bg-verified-soft/40 p-4 transition hover:border-verified/40 hover:shadow-sm">
      <div className="mb-1.5 flex items-center justify-between gap-2">
        <span className="truncate font-bold text-ink">{course.name}</span>
        <span className="shrink-0 text-sm font-medium text-verified">{course.credit ?? "-"}학점</span>
      </div>
      <p className="mb-1.5 text-xs text-ink-soft">
        {course.code}
        {course.category ? ` · ${course.category}` : ""}
        {course.midArea ? ` · ${course.midArea}` : ""}
      </p>
      {course.description && <p className="text-sm text-ink-soft line-clamp-2">{course.description}</p>}
    </div>
  );
}

function EmptyState({ text }: { text: string }) {
  return <p className="rounded-xl bg-cream-2 px-4 py-6 text-center text-sm text-ink-soft">{text}</p>;
}
