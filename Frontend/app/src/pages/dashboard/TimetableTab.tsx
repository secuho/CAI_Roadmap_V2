import { useEffect, useState } from "react";
import { courseApi } from "../../lib/api";
import type { CurrentCourseResponse } from "../../lib/types";
import { CATEGORY_ORDER, categoryLabel, groupBy } from "../../lib/courseGroup";
import { CourseTable } from "../../components/CourseTable";

function currentYearTerm() {
  const now = new Date();
  const month = now.getMonth() + 1;
  const term = month >= 3 && month <= 8 ? "1" : "2";
  const year = now.getFullYear() - (month < 3 ? 1 : 0);
  return { year, term };
}

export function TimetableTab() {
  const [courses, setCourses] = useState<CurrentCourseResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const { year, term } = currentYearTerm();

  useEffect(() => {
    courseApi
      .currentCourses(year, term)
      .then(setCourses)
      .catch(() => setError("현재 수강 강의를 불러오지 못했습니다."));
  }, [year, term]);

  if (error) return <p className="p-4 text-alert">{error}</p>;
  if (!courses) return <p className="p-4 text-ink-soft">불러오는 중…</p>;
  if (courses.length === 0) return <p className="p-4 text-ink-soft">현재 수강 중인 강의가 없습니다.</p>;

  const grouped = groupBy(courses, (c) => categoryLabel(c.cpdvNm));

  return (
    <div>
      <h3 className="mb-4 text-lg font-bold text-ink">
        현재 수강 강의 <span className="text-sm font-normal text-ink-soft">{year}-{term}학기</span>
      </h3>
      <div className="space-y-6">
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
                { label: "강의시간", value: c.timetable },
                { label: "강의실", value: c.classroom },
              ],
            }))}
          />
        ))}
      </div>
    </div>
  );
}
