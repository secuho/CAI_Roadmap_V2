import { CalendarIcon } from "../../components/TabIcons";

export function CalendarTab() {
  return (
    <div className="flex flex-col items-center justify-center rounded-xl bg-cream-2 px-6 py-16 text-center">
      <div className="grid h-14 w-14 place-items-center rounded-full bg-white text-flame shadow-sm">
        <CalendarIcon className="h-7 w-7" />
      </div>
      <h3 className="mt-4 text-lg font-bold text-ink">학사일정 서비스 준비중입니다</h3>
      <p className="mt-2 max-w-sm text-sm leading-relaxed text-ink-soft">조금만 기다려주세요.</p>
    </div>
  );
}
