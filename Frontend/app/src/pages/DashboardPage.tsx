import { useSearchParams } from "react-router-dom";
import { AppHeader } from "../components/AppHeader";
import { DashboardSummary } from "../components/DashboardSummary";
import { ChecklistPanel } from "../components/ChecklistPanel";
import { RecommendIcon, TreeIcon, ClockIcon, CheckBadgeIcon, ChartIcon, CalendarIcon } from "../components/TabIcons";
import { useAuth } from "../lib/AuthContext";
import { RecommendTab } from "./dashboard/RecommendTab";
import { CourseTreeTab } from "./dashboard/CourseTreeTab";
import { TimetableTab } from "./dashboard/TimetableTab";
import { CompletedTab } from "./dashboard/CompletedTab";
import { GradeTab } from "./dashboard/GradeTab";
import { CalendarTab } from "./dashboard/CalendarTab";

const TABS = [
  { key: "recommend", label: "추천 강의", icon: RecommendIcon },
  { key: "courses", label: "이수체계도", icon: TreeIcon },
  { key: "timetable", label: "현재 수강 강의", icon: ClockIcon },
  { key: "progress", label: "수강 완료 강의", icon: CheckBadgeIcon },
  { key: "grade", label: "나의 학점", icon: ChartIcon },
  { key: "calendar", label: "학사일정", icon: CalendarIcon },
] as const;

type TabKey = (typeof TABS)[number]["key"];
const DEFAULT_TAB: TabKey = "recommend";

export function DashboardPage() {
  const { studentId } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const active = (searchParams.get("tab") as TabKey) || DEFAULT_TAB;

  function selectTab(key: TabKey) {
    setSearchParams(key === DEFAULT_TAB ? {} : { tab: key });
  }

  return (
    <div className="min-h-screen bg-cream">
      <AppHeader />
      <DashboardSummary studentId={studentId} />

      <nav className="border-b border-ink/8 bg-cream/70 backdrop-blur">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <ul className="no-scrollbar flex gap-1 overflow-x-auto">
            {TABS.map((tab) => {
              const Icon = tab.icon;
              const isActive = active === tab.key;
              return (
                <li key={tab.key} className="shrink-0">
                  <button
                    onClick={() => selectTab(tab.key)}
                    className={`relative flex items-center gap-1.5 px-4 py-3 text-sm font-semibold transition ${
                      isActive ? "text-flame" : "text-ink-soft hover:text-ink"
                    }`}
                  >
                    <Icon className={`h-4 w-4 ${isActive ? "text-flame" : "text-ink-soft/60"}`} />
                    {tab.label}
                    {isActive && <span className="gradient-flame absolute inset-x-3 -bottom-px h-0.5 rounded-full" />}
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      </nav>

      <div className="mx-auto grid max-w-7xl grid-cols-1 gap-5 px-4 py-5 sm:px-6 lg:grid-cols-12">
        <aside className="lg:col-span-4 lg:sticky lg:top-16 lg:h-fit xl:col-span-3">
          <ChecklistPanel />
        </aside>

        <main className="lg:col-span-8 xl:col-span-9">
          <div className="rounded-2xl bg-white p-5 shadow-sm sm:p-6">
            {active === "recommend" && <RecommendTab />}
            {active === "courses" && <CourseTreeTab />}
            {active === "timetable" && <TimetableTab />}
            {active === "progress" && <CompletedTab />}
            {active === "grade" && <GradeTab />}
            {active === "calendar" && <CalendarTab />}
          </div>
        </main>
      </div>
    </div>
  );
}
