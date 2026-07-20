export interface ChecklistResponse {
  major: number;
  common: number;
  gpa: number;
  totalCredit: number;
  engCredit: number;
  basic: number;
  bsm: number;
  design1: number;
  design2: number;
  basicCompleted: boolean;
  missingBasicCourses: string[];
  bsmCompleted: boolean;
  missingBsmCourses: string[];
  experimentCourseStatus: string;
  commonDonggukAttitude: boolean;
  commonSelfDev: boolean;
  commonThinkncom: boolean;
  commonCreative: boolean;
  commonDigitaliter: boolean;
  missingCommonDonggukAttitude: string[];
  missingCommonSelfDev: string[];
  missingCommonThinkncom: string[];
  missingCommonCreative: string[];
  missingCommonDigitaliter: string[];
}

export interface TermSummaryResponse {
  year: number;
  semCd: string;
  yySemName: string;
  appliedCdt: number | null;
  gainedCdt: number | null;
  gpa: number | null;
  rankText: string | null;
  deptRank: string | null;
  perSco: number | null;
}

export interface CurrentCourseResponse {
  name: string;
  mainProfNm: string | null;
  credit: number | null;
  timetable: string | null;
  classroom: string | null;
  sbjNo: string;
  dvcls: string;
  cpdvNm: string | null;
}

export interface CompletedCourseResponse {
  openYySem: string | null;
  year: number;
  gradeCd: string | null;
  credit: number | null;
  sbjNo: string;
  dvcls: string;
  name: string;
  cpdvNm: string | null;
  mainProfNm: string | null;
}

export interface MyPageResponse {
  email: string | null;
  phone: string | null;
  track: string | null;
}

export interface MajorRecommendation {
  name: string;
  code: string;
  recommendGrade: number;
  tracks: string[];
  credit: number | null;
  description: string | null;
  score: number;
}

export interface LiberalArtsRecommendation {
  code: string;
  name: string;
  credit: number | null;
  totalCredit: number | null;
  midArea: string | null;
  category: string | null;
  description: string | null;
}

export interface RecommendResponse {
  majors: MajorRecommendation[];
  liberalArts: LiberalArtsRecommendation[];
}

export const TRACK_OPTIONS = [
  "선택 안 함",
  "비주얼컴퓨팅트랙",
  "AI/DS트랙",
  "컴퓨터보안트랙",
  "게임트랙",
  "AIoT트랙",
  "소프트웨어심화트랙",
] as const;
