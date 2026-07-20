import { useMemo, useState } from "react";
import { COURSES, MODULES, TRACK_MODULES, type CourseNode } from "../../lib/curriculumTree";

export function CourseTreeTab() {
  const [track, setTrack] = useState<string>("선택 안 함");
  const [selected, setSelected] = useState<string | null>(null);

  const postreqMap = useMemo(() => {
    const map = new Map<string, string[]>();
    COURSES.forEach((c) => {
      c.prereqs.forEach((p) => {
        map.set(p, [...(map.get(p) ?? []), c.id]);
      });
    });
    return map;
  }, []);

  const visibleModules = TRACK_MODULES[track] ?? TRACK_MODULES["선택 안 함"];
  const selectedCourse = selected ? COURSES.find((c) => c.id === selected) : null;

  return (
    <div>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <h3 className="text-lg font-bold text-ink">컴퓨터·AI학부 이수체계도</h3>
        <select
          value={track}
          onChange={(e) => {
            setTrack(e.target.value);
            setSelected(null);
          }}
          className="rounded-lg border border-ink/10 bg-white px-3 py-1.5 text-sm text-ink outline-none focus:border-flame focus:ring-2 focus:ring-flame/25"
        >
          {Object.keys(TRACK_MODULES).map((t) => (
            <option key={t} value={t}>
              {t}
            </option>
          ))}
        </select>
      </div>

      <div className="mb-5 flex flex-wrap items-center gap-3 text-xs text-ink-soft">
        <Legend swatch="border-flame bg-cream-2" label="선택한 과목" />
        <Legend swatch="border-blue-400 bg-blue-50" label="선수 과목" />
        <Legend swatch="border-verified bg-verified-soft" label="후수 과목" />
        <Legend swatch="border-purple-300 bg-purple-50" label="BSM" />
      </div>

      <div className="space-y-5" onClick={(e) => e.target === e.currentTarget && setSelected(null)}>
        {visibleModules.map((moduleKey) => {
          const courses = COURSES.filter((c) => c.module === moduleKey);
          if (courses.length === 0) return null;
          const title = MODULES[String(moduleKey)]?.title ?? String(moduleKey);
          return (
            <div key={moduleKey} className="rounded-xl border border-ink/5 bg-cream/60 p-4">
              <h4 className="mb-3 border-b border-ink/10 pb-2 font-bold text-ink">{title}</h4>
              <div className="flex flex-wrap gap-2.5">
                {courses.map((c) => (
                  <CourseCard
                    key={c.id}
                    course={c}
                    state={cardState(c.id, selected, selectedCourse, postreqMap)}
                    onClick={() => setSelected(selected === c.id ? null : c.id)}
                  />
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

type CardState = "selected" | "prereq" | "postreq" | "dim" | "normal";

function cardState(
  id: string,
  selected: string | null,
  selectedCourse: CourseNode | null | undefined,
  postreqMap: Map<string, string[]>
): CardState {
  if (!selected) return "normal";
  if (id === selected) return "selected";
  if (selectedCourse?.prereqs.includes(id)) return "prereq";
  if (postreqMap.get(id)?.includes(selected)) return "postreq";
  return "dim";
}

function CourseCard({
  course,
  state,
  onClick,
}: {
  course: CourseNode;
  state: CardState;
  onClick: () => void;
}) {
  const typeStyle =
    course.type === "BSM"
      ? "bg-purple-50 border-purple-200"
      : course.type === "설계"
        ? "bg-amber-50 border-amber-300"
        : course.type === "핵심"
          ? "bg-flame/10 border-flame/40 font-bold"
          : "bg-white border-ink/10";

  const stateStyle =
    state === "selected"
      ? "border-flame! bg-cream-2! ring-2 ring-flame/30 scale-[1.03]"
      : state === "prereq"
        ? "border-blue-400! bg-blue-50! ring-2 ring-blue-200"
        : state === "postreq"
          ? "border-verified! bg-verified-soft! ring-2 ring-verified/25"
          : state === "dim"
            ? "opacity-30"
            : "";

  return (
    <button
      onClick={onClick}
      className={`min-w-[128px] flex-1 rounded-lg border px-3 py-2.5 text-sm text-ink shadow-sm transition-all ${typeStyle} ${stateStyle}`}
    >
      {course.name}
    </button>
  );
}

function Legend({ swatch, label }: { swatch: string; label: string }) {
  return (
    <span className="inline-flex items-center gap-1.5">
      <span className={`h-3.5 w-3.5 rounded border ${swatch}`} />
      {label}
    </span>
  );
}
