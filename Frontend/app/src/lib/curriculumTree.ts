export interface CourseNode {
  id: string;
  name: string;
  module: number | "capstone";
  prereqs: string[];
  type?: "BSM" | "설계" | "핵심";
}

export const MODULES: Record<string, { title: string }> = {
  "1": { title: "프로그래밍" },
  "2": { title: "프로그래밍 언어" },
  "3": { title: "자료구조 및 알고리즘" },
  "4": { title: "데이터베이스" },
  "5": { title: "네트워크" },
  "6": { title: "컴퓨터 구조" },
  "7": { title: "인공지능" },
  "8": { title: "비주얼컴퓨팅" },
  "9": { title: "시스템 소프트웨어" },
  "10": { title: "컴퓨터 보안" },
  "11": { title: "가상현실" },
  "12": { title: "컴퓨터 비전" },
  "13": { title: "비주얼컴퓨팅 심화" },
  "14": { title: "인공지능 심화" },
  "15": { title: "컴퓨터 보안 심화" },
  "16": { title: "게임 심화" },
  capstone: { title: "캡스톤 디자인" },
};

export const TRACK_MODULES: Record<string, (number | "capstone")[]> = {
  "선택 안 함": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, "capstone"],
  비주얼컴퓨팅트랙: [1, 3, 7, 8, 11, 12, 13, "capstone"],
  "AI/DS트랙": [1, 3, 4, 7, 12, 14, "capstone"],
  컴퓨터보안트랙: [1, 2, 3, 6, 9, 10, 15, "capstone"],
  게임트랙: [1, 3, 4, 7, 8, 10, 11, 12, 16, "capstone"],
  AIoT트랙: [1, 3, 5, 6, 7, 9, 10, 12, "capstone"],
  소프트웨어심화트랙: [1, 2, 3, 4, 5, 6, 7, 8, 9, "capstone"],
};

export const COURSES: CourseNode[] = [
  { id: "미적분학및연습1", name: "미적분학및연습1", module: 5, prereqs: [], type: "BSM" },
  { id: "이산수학", name: "이산수학", module: 2, prereqs: [], type: "BSM" },
  { id: "확률및통계학", name: "확률및통계학", module: 5, prereqs: ["미적분학및연습1"], type: "BSM" },
  { id: "공학선형대수학", name: "공학선형대수학", module: 15, prereqs: [], type: "BSM" },

  { id: "기초프로그래밍", name: "기초프로그래밍", module: 1, prereqs: [], type: "핵심" },
  { id: "심화프로그래밍", name: "심화프로그래밍", module: 1, prereqs: ["기초프로그래밍"] },
  { id: "객체지향프로그래밍", name: "객체지향프로그래밍", module: 1, prereqs: ["심화프로그래밍"] },
  { id: "소프트웨어공학", name: "소프트웨어공학", module: 1, prereqs: ["객체지향프로그래밍"] },

  { id: "자료구조", name: "자료구조", module: 3, prereqs: ["심화프로그래밍"], type: "핵심" },
  { id: "알고리즘", name: "알고리즘", module: 3, prereqs: ["자료구조"] },

  { id: "데이터베이스", name: "데이터베이스", module: 4, prereqs: ["자료구조"] },
  { id: "데이터베이스설계", name: "데이터베이스설계", module: 4, prereqs: ["데이터베이스"] },

  { id: "데이터통신입문", name: "데이터통신입문", module: 5, prereqs: ["확률및통계학"] },
  { id: "컴퓨터네트워크", name: "컴퓨터네트워크", module: 5, prereqs: ["데이터통신입문"] },

  { id: "프로그래밍언어론", name: "프로그래밍언어론", module: 2, prereqs: ["기초프로그래밍"] },
  { id: "형식언어", name: "형식언어", module: 2, prereqs: ["프로그래밍언어론"] },
  { id: "컴파일러", name: "컴파일러", module: 2, prereqs: ["형식언어"] },

  { id: "컴퓨터구성", name: "컴퓨터구성", module: 6, prereqs: ["이산수학"] },
  { id: "컴퓨터구조", name: "컴퓨터구조", module: 6, prereqs: ["운영체제"] },

  { id: "시스템소프트웨어", name: "시스템소프트웨어", module: 9, prereqs: ["컴퓨터구성", "자료구조"] },
  { id: "운영체제", name: "운영체제", module: 9, prereqs: ["시스템소프트웨어"] },
  { id: "임베디드시스템", name: "임베디드시스템", module: 9, prereqs: ["운영체제"] },
  { id: "병렬처리", name: "병렬처리", module: 9, prereqs: ["임베디드시스템"] },

  { id: "인공지능", name: "인공지능", module: 7, prereqs: ["컴퓨터그래픽스"] },
  { id: "머신러닝", name: "머신러닝", module: 7, prereqs: ["인공지능"] },
  { id: "딥러닝입문", name: "딥러닝입문", module: 7, prereqs: ["머신러닝"] },

  { id: "컴퓨터그래픽스", name: "컴퓨터그래픽스", module: 8, prereqs: ["자료구조"] },
  { id: "인간컴퓨터상호작용", name: "인간컴퓨터상호작용", module: 8, prereqs: ["컴퓨터그래픽스", "자료구조"] },

  { id: "암호학과네트워크보안", name: "암호학과네트워크보안", module: 10, prereqs: ["시큐어코딩"] },
  { id: "컴퓨터보안", name: "컴퓨터보안", module: 10, prereqs: ["암호학과네트워크보안"] },

  { id: "시큐어코딩", name: "시큐어코딩", module: 15, prereqs: ["공학선형대수학", "자료구조"] },
  { id: "양자컴퓨팅", name: "양자컴퓨팅", module: 15, prereqs: ["임베디드시스템"] },
  { id: "웹서비스보안", name: "웹서비스보안", module: 15, prereqs: ["암호학과네트워크보안"] },

  { id: "가상현실", name: "가상현실", module: 11, prereqs: ["디지털영상처리"] },
  { id: "컴퓨터비전입문", name: "컴퓨터비전입문", module: 12, prereqs: ["가상현실"] },
  { id: "디지털신호처리", name: "디지털신호처리", module: 13, prereqs: ["공학선형대수학"] },
  { id: "디지털영상처리", name: "디지털영상처리", module: 13, prereqs: ["디지털신호처리"] },
  { id: "인공지능수학", name: "인공지능수학", module: 14, prereqs: ["공학선형대수학"] },
  { id: "다변량및시계열데이터분석", name: "다변량및시계열데이터분석", module: 14, prereqs: ["인공지능수학"] },
  { id: "자연어처리개론", name: "자연어처리개론", module: 14, prereqs: ["다변량및시계열데이터분석"] },
  { id: "게임프로그래밍", name: "게임프로그래밍", module: 16, prereqs: ["자료구조"] },
  { id: "게임엔진프로그래밍", name: "게임엔진프로그래밍", module: 16, prereqs: ["게임프로그래밍"] },

  { id: "어드벤처디자인", name: "어드벤처디자인", module: "capstone", prereqs: [], type: "설계" },
  {
    id: "공개SW프로젝트",
    name: "공개SW프로젝트",
    module: "capstone",
    prereqs: ["어드벤처디자인", "기초프로그래밍"],
    type: "설계",
  },
  { id: "종합설계1", name: "종합설계1", module: "capstone", prereqs: ["공개SW프로젝트"], type: "설계" },
  { id: "종합설계2", name: "종합설계2", module: "capstone", prereqs: ["종합설계1"], type: "설계" },
];
