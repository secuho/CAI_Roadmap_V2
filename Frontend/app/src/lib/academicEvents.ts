export interface AcademicEvent {
  title: string;
  start: string;
  end: string;
}

export const ACADEMIC_EVENTS: AcademicEvent[] = [
  { title: "개강/학기개시일", start: "2025-09-01", end: "2025-09-01" },
  { title: "학부 수강신청 확인 및 정정", start: "2025-09-02", end: "2025-09-07" },
  { title: "2025학년도 2학기 휴학 신청(2차)", start: "2025-09-03", end: "2025-09-05" },
  { title: "취득교과목 학점포기 신청", start: "2025-09-10", end: "2025-09-12" },
  { title: "수강신청교과목 취소", start: "2025-09-17", end: "2025-09-19" },
  { title: "2025학년도 2학기 휴학 신청(3차)", start: "2025-09-18", end: "2025-09-22" },
  { title: "다전공(복수∙연계∙융합∙학생설계전공) 포기 신청", start: "2025-09-22", end: "2025-09-24" },
  { title: "조기졸업 신청", start: "2025-09-22", end: "2025-09-24" },
  { title: "학기 1/4 기준일", start: "2025-09-26", end: "2025-09-26" },
  { title: "2025학년도 2학기 중간시험", start: "2025-10-20", end: "2025-10-24" },
  { title: "학기 1/2 기준일", start: "2025-10-22", end: "2025-10-22" },
  { title: "겨울 계절학기 수강신청", start: "2025-11-12", end: "2025-11-14" },
  { title: "학기 3/4 기준일", start: "2025-11-17", end: "2025-11-17" },
  { title: "전과(전공변경) 신청", start: "2025-11-26", end: "2025-11-28" },
  { title: "졸업연기 신청", start: "2025-11-26", end: "2025-11-28" },
  {
    title: "2026학년도 1학기 교내장학신청(복지,새터민,국가유공자,종단추천)",
    start: "2025-12-01",
    end: "2025-12-19",
  },
  { title: "2025학년도 2학기 성적처리(입력)", start: "2025-12-01", end: "2025-12-22" },
  { title: "취득교과목 학점포기 신청", start: "2025-12-03", end: "2025-12-05" },
  { title: "다전공(복수∙연계∙융합∙학생설계전공) 신청", start: "2025-12-03", end: "2025-12-19" },
  { title: "나노∙마이크로디그리 신청", start: "2025-12-03", end: "2025-12-19" },
  { title: "2025학년도 2학기 기말시험", start: "2025-12-05", end: "2025-12-11" },
  { title: "종강", start: "2025-12-12", end: "2025-12-12" },
  { title: "2025학년도 2학기 보강 시행기간", start: "2025-12-15", end: "2025-12-19" },
  { title: "겨울 방학", start: "2025-12-20", end: "2026-02-28" },
  { title: "2026학년도 1학기 학·석사연계과정 신청", start: "2025-12-22", end: "2025-12-26" },
  { title: "겨울 계절학기", start: "2025-12-22", end: "2026-01-13" },
  { title: "2025학년도 2학기 성적처리(공시,정정)", start: "2025-12-23", end: "2025-12-26" },
  { title: "2026학년도 1학기 복학 신청(1차)", start: "2026-01-02", end: "2026-01-05" },
  { title: "2026년 봄 졸업대상자 영어성적 제출 마감", start: "2026-01-09", end: "2026-01-09" },
  { title: "2026학년도 1학기 복학 신청(2차)", start: "2026-01-12", end: "2026-01-16" },
  { title: "2026학년도 1학기 휴학 신청(1차)", start: "2026-01-12", end: "2026-01-16" },
  { title: "2026학년도 1학기 학부 수강 신청", start: "2026-02-02", end: "2026-02-06" },
  { title: "2026년 봄 학위수여식(서울캠퍼스)", start: "2026-02-12", end: "2026-02-12" },
  { title: "2026학년도 입학식(서울캠퍼스)", start: "2026-02-13", end: "2026-02-13" },
  { title: "2026학년도 1학기 등록", start: "2026-02-19", end: "2026-02-24" },
];

export function eventColor(title: string): string {
  if (/개강|종강|시험|보강|계절학기/.test(title)) return "bg-flame/10 text-flame border-flame/20";
  if (/수강신청|정정|취소/.test(title)) return "bg-blue-50 text-blue-700 border-blue-200";
  if (/휴학|복학|졸업|연기/.test(title)) return "bg-verified-soft text-verified border-verified/25";
  if (/장학|포기|신청/.test(title)) return "bg-purple-50 text-purple-700 border-purple-200";
  if (/입학식|학위수여식/.test(title)) return "bg-alert-soft text-alert border-alert/25";
  if (/성적처리/.test(title)) return "bg-cream-2 text-ink-soft border-ink/10";
  return "bg-cream-3 text-ink-soft border-ink/10";
}
