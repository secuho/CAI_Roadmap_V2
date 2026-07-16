# CAI Roadmap V2 — 마이그레이션 로드맵 & 결정사항

이 문서는 세션이 바뀌어도 진행 상황과 결정 근거를 이어갈 수 있도록 유지하는 살아있는 문서다.
전체 API 명세는 [API_SPEC.md](./API_SPEC.md) 참고.

## 확정된 결정사항

| 항목 | 결정 | 이유 |
|---|---|---|
| NDRIMS 크롤링 재구현 | **일단 보류, Mock 처리**. `domain/ndrims/NdrimsClient` 인터페이스로 분리해두고, 향후 기존 Python/Selenium 크롤러를 내부 마이크로서비스로 두고 REST로 호출하는 방식으로 교체 예정 | Java Selenium은 seleniumwire 같은 네트워크 응답 인터셉션을 기본 제공하지 않아 완전 재구현은 난이도/리스크가 큼. 검증된 Python 코드를 재사용하는 편이 가장 안전 |
| Refresh Token 저장 | **MySQL 테이블**에 저장 (Redis 미도입) | 현재 인프라에 Redis가 없고, 이 규모에서는 DB 테이블로 충분. 필요해지면 Redis로 교체 가능하도록 Repository 계층으로 캡슐화 |
| Refresh Token 저장 형태 | DB에는 **SHA-256 해시**로 저장, 클라이언트에는 원본 반환 | 비밀번호와 마찬가지로 DB 유출 시에도 토큰 자체가 노출되지 않도록 |
| Access / Refresh Token 만료 | Access 30분 / Refresh 14일 (application.yaml의 `jwt.*`로 설정 가능) | 일반적인 관례값. 추후 서비스 특성에 맞게 조정 가능 |
| 로그아웃 시 무효화 전략 | Refresh Token DB row 삭제. Access Token은 별도 블랙리스트 없이 짧은 만료(30분)에 의존 | Redis 등 별도 인프라 없이 구현 가능한 범위에서 실용적 선택. 로그아웃 직후에도 이미 발급된 Access Token은 만료 전까지 유효하다는 한계는 인지하고 있음 |
| 회원 탈퇴 시 비밀번호 재확인 | **유지** (JWT 인증 + 비밀번호 재확인 모두 요구) | 파괴적 작업이라 JWT 탈취만으로 탈퇴가 가능해지는 것은 리스크가 큼. V1과 동일한 안전장치 |
| `/s/apply`, `/s/list`, `/s/clear` (밥약 신청) | **마이그레이션 범위에서 제외** | V2 API 명세에 포함되어 있지 않음. 필요 시 별도 논의 |
| 인증 스키마 범위 | 인증에 필요한 최소 필드만 가진 단일 `users` 테이블로 시작 (V1의 `userdb.users` + `test.students` 통합은 아직 하지 않음) | 이번 세션은 인증 도메인만 범위. 학과/이수과목 등 프로필 전체는 Phase 2에서 `Student` 엔티티로 별도 설계 |
| 로컬 개발/검증 DB | `local` 프로파일에서 H2 인메모리 사용, 운영은 MySQL(`mysql-connector-j`) 유지 | 실제 MySQL 없이도 즉시 기동/검증 가능하게 하기 위함 |
| 개인정보(Student) 저장 방침 | **암호화 없이 V1 필드 전부 + 세분화된 원본 필드까지 평문 저장** (`bank_account_no` 포함) | 사용자가 "관리자용 프로필 다양화를 위해 최대한 많은 정보를 유지하고, 개인정보 보호는 추후 별도로 다루겠다"고 명시적으로 결정. **⚠️ 보안 부채로 인지** — 계좌번호 등 민감정보 평문 저장은 실제 운영 전 반드시 암호화/마스킹 처리 필요 (아래 미해결 사항 참고) |
| V1 스키마 필드 보존 범위 | 5개 서비스 API가 안 쓰는 컬럼도 "지금 안 쓴다"는 이유로 드롭하지 않고 세분화해서 전부 컬럼화 | 사용자가 "JSON에서 가져올 수 있는 정보는 최대한 세분화해서 컬럼으로 저장, 필요없는 게 아니면 다 수집"으로 결정. 단, 값이 100% 동일하게 중복 저장되는 필드(`enrollments.credit` 등 진짜 중복)는 예외 |
| 추천 알고리즘 `to_count`(정원) | V1의 하드코딩 50 → **실제 정원값 사용** | 사용자가 제공한 실 JSON(`extra/db_dump/data.json`)에 `TKCRS_PCNT`(정원, 0~440 다양하게 분포)가 실재함을 확인. V1은 이 필드를 그냥 안 썼던 것으로 판단, V2는 정확히 반영 |
| 추천 개설여부(is_opened) 판정 | 학생 수강이력과 무관하게, **courses 테이블에 그 학기 그 과목 코드가 존재하면 개설로 간주** | 사용자가 "전체 과목 테이블에 등록되어 있으면 무조건 개설"이라고 확정. 별도의 개설과목 카탈로그 테이블 없이 기존 `courses`(Course 엔티티)로 충분 |
| 전공/교양 추천 마스터 데이터(`CourseCandidate`/`LiberalArtsCourse`) | **스키마만 생성, 데이터는 비어 있음** | V1의 `courses.courses`+`curriculum.*`(전공 후보/트랙/선수과목)과 `courses.course_cat`(교양 후보)에 해당하는 실 데이터가 아직 없음. Course/Enrollment와 동일하게 "스키마 준비 → 나중에 채움" 패턴 적용 |
| Course 엔티티 추가 확장(정원/폐강/다국어 등) | V1 원본 JSON(108개 필드) 중 **고가치 필드만 선별 추가**(~9개), 성적평가 비율/시스템 내부 플래그(~90개)는 제외 | "최대한 세분화" 원칙과 별개로, 순수 UI/시스템 설정용으로 보이는 필드까지 다 담는 건 실익 대비 엔티티 비대화가 크다고 판단해 사용자와 협의 후 범위를 좁힘 |

## Phase 진행 체크리스트

- [x] **Phase 0** — V1 소스(`extra/`) 전수 분석, API 명세/결정사항 문서화 (`API_SPEC.md`, `ROADMAP.md`)
- [x] **Phase 1** — 프로젝트 스캐폴딩 + 인증 도메인 (`/auth/**`) 전체 구현
- [x] **Phase 2** — 서비스 도메인: `Student`/`Course`/`Enrollment`/`TermSummary` 엔티티 설계, `/api/checklist`, `/api/term`, `/api/current-courses`, `/api/completed-courses`, `/api/all-courses`
- [x] **Phase 3** — 마이페이지/추천: `/api/mypage`, `/api/mytrack`, `/api/recommends`
- [ ] **Phase 4** — NDRIMS 실 연동: Mock → 실제 크롤러(내부 마이크로서비스) 연결, `/api/sse`, `/api/ndrims-sync/update`, `/api/ndrims-sync/status`

## 미해결/향후 확인 필요 사항

- [ ] **[보안] `students.bank_account_no` 등 민감정보 평문 저장** — 실 운영 배포 전 암호화(예: 애플리케이션 레벨 AES) 또는 접근 통제 필요. 지금은 사용자 결정으로 의도적으로 미룸
- [x] `/data/maincourses`(V1) → `/api/all-courses`(V2)는 "학생의 전체 수강 이력(등록+이수) distinct 과목명"으로 확정, V1과 동일하게 구현 완료
- [x] `/data/mydata`(V1, email/phone/track만 반환) → `/api/mypage`도 동일하게 email/phone/track만 반환하는 것으로 확정 구현 (Student 엔티티는 넓지만, mypage 응답은 V1과 동일 범위 유지)
- [ ] NDRIMS 실 연동 시 Python 크롤러를 어떤 방식(내부 REST, 메시지 큐 등)으로 호출할지 Phase 4에서 결정. 이때 크롤링 결과를 `Student`/`Course`의 세분화된 컬럼에 실제로 채우는 매핑 로직도 함께 구현 필요
- [ ] Refresh Token을 Redis로 옮길 필요가 생기는 시점(트래픽/멀티 인스턴스 등) 재검토
- [x] "전체 개설과목 카탈로그" 문제는 사용자가 "courses 테이블에 존재하면 개설로 간주"로 확정, 별도 테이블 불필요 — 대신 실 데이터(`extra/db_dump`) 적재 경로(`CourseCatalogSeeder`, local 전용) 마련
- [ ] `CourseCandidate`/`LiberalArtsCourse`(전공/교양 추천 마스터) 실 데이터 적재 — V1의 `curriculum.*`/`courses.course_cat`에 해당하는 덤프가 아직 없음. 확보되면 `CourseCatalogSeeder`와 유사한 방식으로 적재 필요
- [ ] Course 엔티티에서 제외한 나머지 ~90개 필드(성적평가 비율, 원격/혼합강의 플래그 등)가 실제로 필요해지는 기능이 생기면 그때 추가 검토

## 세션 로그

- **2026-07-16**: `extra/` 폴더 전수 분석 완료. 사용자가 V1→V2 API 매핑 문서 제공. 위 결정사항 확정 후 Phase 1(스캐폴딩 + 인증 도메인) 착수 및 완료.
  - 구현: `global`(ApiResponse, BusinessException/ErrorCode/GlobalExceptionHandler, JwtProvider/JwtProperties/JwtAuthenticationFilter/JwtAuthenticationEntryPoint, SecurityConfig) + `domain/auth`(User/RefreshToken 엔티티, Repository, DTO, AuthService, AuthController) + `domain/ndrims`(NdrimsClient/MockNdrimsClient)
  - 스키마: `users`(id, student_id, password, track, role, created_at), `refresh_tokens`(id, user_id, token_hash, expires_at, created_at)
  - 트러블슈팅: JWT의 `iat`/`exp`가 초 단위 정밀도라 같은 초 안에 같은 사용자에게 토큰을 두 번 발급하면 완전히 동일한 토큰이 생성되어 `refresh_tokens.token_hash` UNIQUE 제약을 위반하는 문제 발견 → 모든 토큰에 랜덤 `jti` 클레임 추가로 해결. 겸사겸사 `refresh()`에서 Hibernate가 같은 트랜잭션 내 INSERT를 DELETE보다 먼저 flush하는 특성 때문에 생길 수 있는 동일 이슈를 막기 위해 delete 직후 명시적 flush 추가
  - Spring Security 기본 익명 인증으로 인해 미인증 요청이 403으로 응답되던 것을 `JwtAuthenticationEntryPoint`로 401 JSON 응답하도록 수정 (계획에는 없었지만 REST 컨벤션에 맞춰 즉시 보완)
  - 검증: `local` 프로파일(H2)로 기동 후 curl + `AuthControllerTest`(MockMvc)로 회원가입/중복가입 거부/로그인/미인증 401/리프레시 회전/로그아웃 무효화/탈퇴/탈퇴 후 재로그인 실패까지 전체 플로우 확인, `./gradlew test` 전체 통과
- **2026-07-16 (계속)**: Phase 2(체크리스트/학점/과목 API) 착수 및 완료. 구현 전 API별 사용 테이블/컬럼을 사용자와 표로 검토.
  - 스키마 리뷰: `terms` 테이블(미사용) 제거, V1의 학기별 동적 테이블(`courses_2025termN`) 패턴 제거하고 `courses`에 컬럼으로 통합, `enrollments.credit`(완전 중복) 제거 등을 제안 → 사용자가 "V1이 실제로 참조한 필드는 세분화해서 최대한 컬럼화"로 결정, 그 결과 `courses`에 학기별 카탈로그 필드(강의실/외국어여부/영문시간표 등)까지 전부 컬럼으로 흡수(약 29개 컬럼)
  - 사용자 결정으로 범위 확장: `Student` 엔티티(V1 `students` 전체 + 원본 raw 필드까지 세분화, 예: `stdNm`/`dptMjrNmRaw`/`rsdnDtSexRaw`/`mrksAvg`)를 이번 Phase에 추가. **계좌번호 등 암호화 없이 평문 저장** — 의도적 결정, 보안 부채로 남김(위 미해결 사항 참고)
  - 구현: `domain/auth`(Student 엔티티+Repository, AuthService가 signup/withdraw 시 Student·Enrollment·TermSummary까지 함께 생성/정리) + `domain/course`(Course/Enrollment/EnrollmentStatus/TermSummary 엔티티, Repository, DTO, CourseQueryService, CourseController) + `domain/checklist`(CurriculumRequirements 상수, ChecklistService, ChecklistResponse, ChecklistController)
  - 체크리스트는 V1처럼 SQL을 여러 번 나눠 던지지 않고 "완료 수강 목록 1회 + 평균 GPA 1회" 조회 후 자바에서 인메모리 집계하는 방식으로 단순화
  - 트러블슈팅: H2에서 `year`가 예약어라 컬럼 매핑 충돌 → `enroll_year`/`summary_year`로 컬럼명 변경. `/api/current-courses`처럼 필수 쿼리파라미터 누락 시 발생하는 `MissingServletRequestParameterException`이 Spring Boot의 내부 `/error` 포워드를 타면서 SecurityConfig가 `/error` 경로를 인증 필요로 막고 있어 의도한 400 대신 401이 반환되는 문제 발견 → `/error`를 permitAll로 추가하고 `GlobalExceptionHandler`에 해당 예외 핸들러를 추가해 일관된 400 응답으로 정리
  - 검증: `CourseControllerTest`/`ChecklistControllerTest`(MockMvc, Repository에 직접 fixture 저장 — 실 크롤링 파이프라인이 없어 이 방식으로 검증)로 5개 엔드포인트 + 체크리스트 세부 카테고리(기본소양 2/3, BSM 3/4, 동국인성 3/3, F학점 제외 등) 검증, curl로도 재확인, `./gradlew test` 전체 통과
- **2026-07-16 (계속)**: Phase 3(마이페이지/추천) 착수 및 완료.
  - `/api/mypage`, `/api/mytrack`은 기존 User/Student로 바로 구현. 3개 컨트롤러에서 반복되던 "JWT→User 조회" 코드를 `domain/auth/CurrentUserResolver`로 추출해 정리
  - 추천(`/api/recommends`) 설계 중 사용자가 실 데이터 2종을 제공: `extra/db_dump/courses_courses_2025term1.sql`/`term2.sql`(학기별 개설강좌, 축소 컬럼), `extra/db_dump/data.json`(term1의 원본 JSON, 108개 필드 전체). 이 데이터를 검토해 확정한 것들:
    - `TKCRS_PCNT` 필드가 실제 정원(0~440 다양하게 분포)임을 확인 → 추천 점수의 `to_count`를 V1의 하드코딩 50 대신 실제 정원으로 교체
    - "개설여부"는 `courses` 테이블에 그 학기·코드가 존재하면 무조건 개설로 간주(사용자 확정) → 별도 카탈로그 테이블 불필요
    - Course 엔티티에 고가치 필드 9개 추가(capacity, capacityLimited, cancelledReason/Date, nameEng, descriptionKor/Eng, classroomEng, planUrl) — 나머지 ~90개 시스템/평가비율 필드는 실익 대비 비대화 우려로 제외(사용자와 협의)
  - 구현: `domain/course/CourseCatalogSeeder`(local 전용, `app.seed-course-catalog=true`일 때만 동작 — term1은 JSON을 Jackson으로 직접 파싱, term2는 MySQL 덤프의 INSERT 문을 자체 파서로 읽어 `courses`에 적재) + `domain/recommend`(CourseCandidate/LiberalArtsCourse 빈 스키마, RecommendService가 V1 get_recommendations/get_cat 포팅, RecommendController)
  - 트러블슈팅: H2가 mysqldump의 `COLLATE`/`ENGINE` 절이 섞인 CREATE TABLE과 MySQL 특유 이스케이프 문법을 직접 실행하지 못해 여러 차례 실패 → SQL을 H2에 그대로 실행시키는 대신, INSERT문의 VALUES를 자바에서 직접 파싱(백슬래시 이스케이프 처리 포함)해 PreparedStatement로 바인딩하는 방식으로 전환해 해결. 설명 필드가 2000자 varchar를 넘어서는 경우가 있어 TEXT 컬럼으로 변경
  - 검증: 실 데이터 5,176건(term1 2,643 + term2 2,533) 적재 성공 확인. `RecommendControllerTest`(fixture로 개설/미개설, 정원 0, 선수과목 미충족, 이미수강, 트랙 매칭/불일치, 중영역 학점상한, 동일과목군, 상호배타 코드, 미개설 필터까지 모두 검증), curl로 mypage/mytrack/recommends 전체 확인, `./gradlew test` 전체 통과
