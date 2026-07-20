import { useEffect, useState } from "react";
import { courseApi } from "../../lib/api";
import type { CompletedCourseResponse } from "../../lib/types";
import { CATEGORY_ORDER, categoryLabel, groupBy } from "../../lib/courseGroup";
import { CourseTable } from "../../components/CourseTable";

export function CompletedTab() {
  const [courses, setCourses] = useState<CompletedCourseResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    courseApi
      .completedCourses()
      .then(setCourses)
      .catch(() => setError("수강 완료 강의를 불러오지 못했습니다."));
  }, []);

  if (error) return <p className="p-4 text-alert">{error}</p>;
  if (!courses) return <p className="p-4 text-ink-soft">불러오는 중…</p>;
  if (courses.length === 0) return <p className="p-4 text-ink-soft">수강 완료한 강의가 없습니다.</p>;

  const bySemester = groupBy(courses, (c) => c.openYySem ?? "기타");
  const semesters = Object.keys(bySemester).sort((a, b) => b.localeCompare(a));

  return (
    <div>
      <h3 className="mb-4 text-lg font-bold text-ink">수강 완료 강의</h3>
      <div className="space-y-8">
        {semesters.map((sem) => {
          const grouped = groupBy(bySemester[sem], (c) => categoryLabel(c.cpdvNm));
          return (
            <div key={sem}>
              <h4 className="mb-3 border-b border-flame/20 pb-2 text-base font-bold text-ink">{sem}</h4>
              <div className="space-y-5">
                {CATEGORY_ORDER.filter((cat) => grouped[cat]?.length).map((cat) => (
                  <CourseTable
                    key={cat}
                    title={cat}
                    rows={grouped[cat].map((c) => ({
                      code: c.sbjNo,
                      name: c.name,
                      professor: c.mainProfNm,
                      credit: c.credit,
                      extra: [
                        { label: "분반", value: c.dvcls },
                        { label: "성취도", value: c.gradeCd },
                      ],
                    }))}
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
