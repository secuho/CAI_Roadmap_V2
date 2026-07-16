# CAI Roadmap V2 — API 명세 및 마이그레이션 가이드

## 프로젝트 개요

CAI Roadmap은 컴퓨터·AI학부 학생을 위한 수강 로드맵 및 과목 추천 서비스입니다.
V1은 Flask + PyMySQL 기반으로 운영되었고, V2에서는 **Spring Boot + MySQL** 스택으로 전면 재작성합니다.

### V1 → V2 핵심 변경 사항

1. **인증 방식 전환**: V1은 모든 요청 body에 `student_id`를 평문으로 담아 전달했음 (인가 개념 없음, 누구나 다른 사람 student_id를 넣으면 접근 가능한 구조였음).
   V2는 **JWT (Access Token + Refresh Token)** 기반으로 전환. 사용자 식별은 `Authorization: Bearer <token>` 헤더에서 추출.
2. **DB 커넥션 관리**: V1은 요청마다 `pymysql.connect()`로 커넥션을 직접 열고 닫았음. V2는 Spring Data JPA + HikariCP 커넥션 풀 사용.
3. **네이밍 컨벤션 통일**: V1은 snake_case, 축약어, 비일관적 경로가 혼재되어 있었음. V2는 REST 관례에 맞춰 kebab-case로 통일.
4. **리소스 중심 설계**: 회원가입/탈퇴처럼 같은 리소스에 대한 동작은 메서드로 구분 (`POST /auth/account` = 가입, `DELETE /auth/account` = 탈퇴).
5. **NDRIMS 동기화**: V1은 Thread + 메모리 딕셔너리(`progress_map`)로 비동기 job 진행률을 폴링 방식으로 제공했음. V2에서는 이 부분을 Spring `@Async` 또는 SSE로 재구현 예정.

---

## API 엔드포인트 매핑 (V1 → V2)

### 인가 (Auth)

| 기능 | V1 (Flask) | V2 (Spring Boot) | Method | 비고 |
|---|---|---|---|---|
| 로그인 | `/login` | `/auth/login` | POST | V1은 응답으로 개인정보 JSON을 바로 반환. V2는 Access/Refresh Token 반환으로 변경 |
| 로그아웃 | *(없음)* | `/auth/logout` | POST | V1에 없던 신규 엔드포인트. Refresh Token 무효화 처리 |
| 회원가입 | `/signup` | `/auth/account` | POST | V1은 NDRIMS 크롤링(`get_ndrims`)과 트랙 설정(`set_track`)을 가입 로직에 포함. 동일 로직 유지 (현재는 Mock, [ROADMAP.md](./ROADMAP.md) 참고) |
| 회원 탈퇴 | `/delete` | `/auth/account` | DELETE | V1은 body의 password로 재검증 후 탈퇴. V2도 JWT 인증 + 비밀번호 재확인 유지 (결정사항, ROADMAP 참고) |
| 토큰 재발급 | *(없음)* | `/auth/refresh` | POST | V1에 없던 신규 엔드포인트 |

### 서비스 (학적/수강 데이터 조회)

| 기능 | V1 (Flask) | V2 (Spring Boot) | Method | 비고 |
|---|---|---|---|---|
| 체크리스트 조회 | `/data/checklist` | `/api/checklist` | GET | V1은 POST + body의 student_id. V2는 GET + JWT에서 사용자 추출 |
| 나의 학점 탭 조회 | `/data/term` | `/api/term` | GET | 동일 |
| 현재 수강 강의 조회 | `/data/curcourses` | `/api/current-courses` | GET | V1은 `year`, `term` 파라미터를 body로 받음 → V2는 query parameter로 전환 (`?year=2026&term=1`) |
| 수강 완료 강의 조회 | `/data/completed` | `/api/completed-courses` | GET | 동일 |
| 전체 과목 조회 | `/data/maincourses` | `/api/all-courses` | GET | 이름이 `maincourses`에서 `all-courses`로 변경됨. 의미상 동일한 데이터인지 Phase 2 구현 시 재확인 필요 |

### 마이페이지

| 기능 | V1 (Flask) | V2 (Spring Boot) | Method | 비고 |
|---|---|---|---|---|
| 마이페이지 조회 | `/data/mydata` | `/api/mypage` | GET | V1은 email, phone, track만 반환. V2에서 필드 확장 여부 Phase 2에서 확인 |
| 사용자 트랙 수정 | `/update/track` | `/api/mytrack` | PATCH | 동일 |
| 교과목 추천 | `/recommendations` | `/api/recommends` | GET | V1은 POST + body(`student_id`, `next_term`). V2는 GET으로 전환, `next_term`을 query parameter로 전달 (`?next_term=2026-2`) |

### 데이터 동기 (NDRIMS 연동)

| 기능 | V1 (Flask) | V2 (Spring Boot) | Method | 비고 |
|---|---|---|---|---|
| SSE 연결 | *(없음, 폴링 방식이었음)* | `/api/sse` | GET | V1은 `/update/ndrims_status`를 클라이언트가 주기적으로 GET 폴링. V2는 SSE로 실시간 push 전환 |
| NDRIMS 업데이트 시작 | `/update/ndrims_start` | `/api/ndrims-sync/update` | POST | V1은 UUID 기반 job_id 발급 후 백그라운드 Thread 실행 구조 |
| NDRIMS 업데이트 상태 조회 | `/update/ndrims_status` | `/api/ndrims-sync/status` | GET | SSE 전환 시 fallback용으로만 유지될 수 있음 |

> V1의 `/s/apply`, `/s/list`, `/s/clear` (밥약 신청 관련 별도 도메인)는 이 V2 명세에 포함되지 않으며, 마이그레이션 범위에서 제외되었습니다. ([ROADMAP.md](./ROADMAP.md) 참고)

---

## V2 최종 API 명세 (전체)

```
[인가]
POST   /auth/login
POST   /auth/logout
POST   /auth/account          (회원가입)
DELETE /auth/account          (회원 탈퇴)
POST   /auth/refresh

[서비스]
GET    /api/checklist
GET    /api/term
GET    /api/current-courses
GET    /api/completed-courses
GET    /api/all-courses

[마이페이지]
GET    /api/mypage
PATCH  /api/mytrack
GET    /api/recommends

[데이터 동기]
GET    /api/sse
POST   /api/ndrims-sync/update
GET    /api/ndrims-sync/status
```

---

## 구현 시 참고할 V1 로직 (재구현 대상)

V2 구현 시 아래 V1 로직의 의도를 참고해서 동일한 비즈니스 로직을 Spring Boot 스타일로 옮긴다. (V1 원본은 `Backend/extra/` 참고용 보관)

1. **로그인 (`/login` → `/auth/login`)**
   - bcrypt로 비밀번호 해시 비교 (`bcrypt.checkpw`)
   - 로그인 성공 시 `./data/{student_id}_*` 패턴의 폴더에서 `extracted_personal+info.json` 파일을 찾아 반환하던 구조 → V2에서는 이 개인정보를 파일이 아닌 DB 테이블에서 조회하는 구조로 전환 (Phase 2, `/api/mypage`)

2. **회원가입 (`/signup` → `POST /auth/account`)**
   - 아이디 중복 확인 → NDRIMS 포털에 실제 로그인 시도(`get_ndrims`) → 성공 시 트랙 설정(`set_track`)
   - 이 흐름 자체는 유지하되, 응답 구조를 JWT 토큰 발급까지 포함하도록 확장. NDRIMS 실 연동 전까지는 Mock으로 대체 (ROADMAP 참고)

3. **회원 탈퇴 (`/delete` → `DELETE /auth/account`)**
   - V1은 비밀번호 재확인 후 삭제. V2도 JWT + 비밀번호 재확인 유지 (결정사항)

4. **NDRIMS 동기화 (`/update/ndrims_start`, `/update/ndrims_status` → `/api/ndrims-sync/update`, `/api/ndrims-sync/status`, `/api/sse`)**
   - V1은 `progress_map`(전역 dict + Lock)으로 진행 상태를 관리하고 클라이언트가 폴링
   - V2에서는 SSE(`/api/sse`)로 실시간 전달하는 방식으로 개선 예정 (Phase 4)
   - 진행 단계: 준비 중 → 포털 접속/로그인 중 → 로그인 완료 → 렌더링/초기 준비 완료 → 모든 요청 수집 및 저장 완료 (5단계)

5. **추천 로직 (`/recommendations` → `/api/recommends`)**
   - 전공 추천(`get_recommendations`, 점수순 정렬)과 교양 추천(`get_cat`, 미이수 과목 필터링) 두 가지를 결합해서 반환
   - 응답 구조: `{ majors: [...], liberal_arts: [...] }` 형태 유지 검토 (Phase 3)

---

## V1 소스 요약 (참고, `Backend/extra/`)

- `extra/Server.py` — Flask 앱 본체, 위 모든 라우트의 V1 구현
- `extra/utils/db_utils.py` — DB 접근 계층. `get_checklist()`가 가장 복잡한 로직 (전공/공통교양 학점, GPA, 기본소양/BSM/실험/종합설계/디지털리터러시 이수 여부를 하드코딩된 과목명 리스트로 판정)
- `extra/utils/ndrims_utils.py` — Selenium(seleniumwire)으로 NDrims 포털 로그인 후 수강/성적 데이터 크롤링
- `extra/utils/recommend_utils.py` — 가중치 기반 추천 스코어링 로직
- `extra/data/`, `extra/utils/courses_data/` — 크롤링 결과 JSON을 DB에 적재하는 1회성 스크립트
- `extra/public/`, `extra/debug+tools/` — 초기 프로토타입/디버그용 스크립트 (중복 이터레이션, 참고용)
