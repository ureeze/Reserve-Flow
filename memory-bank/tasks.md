# Tasks

이 문서는 현재 실행할 작업 목록의 source of record이다. Jira 전체 백로그를 그대로 복사하지 않고, 실제 개발 진행에 필요한 가까운 작업만 유지한다.

개발 작업은 Jira Key 기준으로 진행한다. 문서 정리, 운영 규칙 정리, 로컬 관리 작업은 로컬 T-ID로 기록할 수 있다. 작업 완료 시 테스트/검증 결과, Memory Bank 갱신 여부, GitHub PR 생성 여부, Jira 상태 반영 여부, Slack 공유 여부를 필요에 따라 기록한다.

기본 완료 범위는 로컬 구현, 테스트/검증, Memory Bank 갱신이다. GitHub PR 생성은 실행계획에 포함하고 사용자 확인을 받은 경우에만 수행한다. Jira 상태 변경은 `AGENTS.md`의 Jira 규칙에 따라 작업 시작, PR 생성, PR merge 시점에 기본 전환한다. 주요 상태 변화의 Slack 공유는 `AGENTS.md` 기준에 따라 기본 수행한다.

작업 상태는 `In Progress -> Review -> Done` 흐름으로 관리한다. 구현 중인 작업은 `In Progress`, 구현과 검증이 완료되어 PR이 생성된 작업은 `Review`, PR merge와 Jira 완료 전환까지 끝난 작업은 `Done`에 둔다.

## Next

- 없음
- [T-008] 예약 조건 검증 API 구현 (Jira: RF-7)
- [T-009] 예약 제공자 검색 API 구현 (Jira: RF-8)
- [T-010] booking slot 조회 API 구현 (Jira: RF-9)

## In Progress

- 없음

## Review

- [T-007] 자연어 예약 요청 해석 API 구현 (Jira: RF-6, PR: #13, 브랜치: `feature/RF-6-llm-interpretation-spike`, 검증: `.\backend\gradlew.bat test` 통과, Python 모의 structured-output 테스트 통과, Docker Compose 기반 실제 OpenAI 호출과 Spring Boot 공개 API 연동 검증 통과, Jira 상태: `검토 중`, Slack: `#reserve-flow-dev` 구현 완료 리뷰 요청 전송 시도했으나 커넥터의 외부 공유 보안 정책으로 차단)

## Blocked

- 없음

## Acceptance Criteria

### T-001 / RF-18

- Gradle 기반 Spring Boot 4.1.0 프로젝트가 준비되어 있다.
- Java 21 기준으로 빌드 설정이 되어 있다.
- `backend/gradlew`와 `backend/gradlew.bat` 또는 동등한 Gradle 실행 방법이 준비되어 있다.
- 기본 애플리케이션 클래스와 context load 테스트가 있다.
- 기본 패키지 구조가 ReserveFlow 모듈 후보와 어긋나지 않는다.
- OpenAPI 문서화는 코드 우선 방식을 기준으로 하며, `springdoc-openapi` 도입 여부가 결정되어 있다.
- `.\backend\gradlew.bat test` 또는 합의한 검증 명령이 통과한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-002 / RF-19

- Spring Data JPA 의존성이 추가되어 있다.
- PostgreSQL JDBC Driver 의존성이 추가되어 있다.
- Flyway 의존성과 PostgreSQL Flyway 지원 모듈이 추가되어 있다.
- datasource 설정은 환경변수로 덮어쓸 수 있으며 로컬 기본값을 가진다.
- 운영 기준 Hibernate `ddl-auto`는 `validate`로 설정되어 있다.
- Flyway Migration 기본 경로 `src/main/resources/db/migration`이 준비되어 있다.
- 로컬 PostgreSQL 실행 방법이 준비되어 있다.
- `.\backend\gradlew.bat test` 또는 합의한 검증 명령이 통과한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-003 / RF-20

- Spring Data Redis 의존성이 추가되어 있다.
- Redis 연결 설정은 환경변수로 덮어쓸 수 있으며 로컬 기본값을 가진다.
- 로컬 Redis 실행 방법이 `docker-compose.yml`에 준비되어 있다.
- 테스트 profile에서 애플리케이션 context load가 Redis 설정 때문에 실패하지 않는다.
- `.\backend\gradlew.bat test` 또는 합의한 검증 명령이 통과한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-004 / RF-21

- Spring Security 의존성과 JWT 검증 기반이 추가되어 있다.
- `members` 테이블과 Member Entity/Repository가 준비되어 있다.
- 회원 내부 PK는 `Long id`, API/JWT 노출 식별자는 `UUID publicId`로 분리되어 있다.
- 회원가입 API가 비밀번호를 BCrypt로 해시 저장하고 회원 생성 결과를 반환한다.
- 로그인 API가 회원 인증 후 access/refresh token을 발급한다.
- refresh token으로 토큰 재발급이 가능하다.
- Bearer Token 기반으로 보호 API에서 현재 회원을 식별할 수 있다.
- 인증이 없거나 유효하지 않으면 401을 반환한다.
- JWT, 비밀번호, 인증 헤더를 로그에 남기지 않는다.
- 인증 코드는 `controller`, `service`, `dto`, `config`, `entity`, `repository` 계층 패키지로 정리되어 있다.
- 반복 생성자 코드는 Lombok으로 정리되어 있다.
- `.\backend\gradlew.bat test` 또는 합의한 검증 명령이 통과한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-005 / RF-22

- 공통 오류 응답 DTO가 정의되어 있다.
- 오류 응답은 API 명세서 형식인 `error.code`, `error.message`, `error.details`, `error.requestId`, `error.timestamp`를 사용한다.
- 주요 예외가 Error Catalog 코드로 매핑되어 있다.
- Validation 오류는 `400 VALIDATION_004` 공통 형식으로 반환된다.
- 인증 필요 또는 로그인 실패는 `401 AUTH_001` 공통 형식으로 반환된다.
- 토큰 만료 또는 유효하지 않은 토큰은 `401 AUTH_002` 공통 형식으로 반환된다.
- 리소스 없음은 `404` 계열 Error Catalog 코드의 공통 형식으로 반환될 수 있다.
- 중복 가입 같은 충돌은 `409 AUTH_004` 공통 형식으로 반환된다.
- 오류 응답에 stack trace, SQL, 내부 인프라 정보가 노출되지 않는다.
- `.\backend\gradlew.bat test` 또는 합의한 검증 명령이 통과한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-006 / RF-23

- ERD/DB 설계서 기준 `outbox_events` 테이블 migration이 준비되어 있다.
- Outbox 이벤트 Entity와 상태 enum이 정의되어 있다.
- 상태 값은 `PENDING`, `PUBLISHED`, `FAILED`를 사용한다.
- 도메인 트랜잭션 안에서 Outbox 이벤트를 함께 저장할 수 있는 Appender 서비스가 있다.
- 트랜잭션 없이 Outbox 저장을 호출하면 실패한다.
- Relay가 발행 대상 이벤트를 조회할 수 있는 Repository 메서드가 있다.
- Kafka 발행 Worker 구현은 후속 작업 범위로 남긴다.
- 개인정보, 인증정보, 원문 프롬프트, stack trace를 payload에 저장하지 않는 기준이 코드 주석 또는 문서에 반영되어 있다.
- `.\backend\gradlew.bat test` 또는 합의한 검증 명령이 통과한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-007 / RF-6

- `POST /api/v1/reservation-requests/interpret`가 자연어 예약 요청을 구조화된 예약 조건으로 반환한다.
- 해석 API는 인증 없이 호출할 수 있고, 인증된 호출은 JWT subject를 rate limit 키로 사용한다.
- Python FastAPI + LangChain 서비스가 Spring Boot의 내부 HTTP 요청을 처리한다.
- Python 서비스는 Pydantic과 LangChain structured output으로 응답 구조를 검증한다.
- 상대 날짜는 Spring Boot 서버의 현재 날짜와 시간대를 기준으로 해석한다.
- `providerType`을 추론하지 못하거나 지원하지 않는 유형이면 `400 PARSE_004`를 반환한다.
- 요청 제한 초과 시 `429 RATE_LIMIT_001`, LLM 서비스 호출 실패 시 `502 LLM_001`을 반환한다.
- 자연어 원문은 DB, Outbox payload, audit metadata, 로그에 저장하지 않는다.
- Redis 기반 호출 제한이 사용자당 분당 10회로 적용된다.
- Spring Boot 테스트와 Python 서비스 테스트가 통과한다.
- Docker Compose 기반 로컬 연동을 검증한다.
- `memory-bank/current-state.md`와 `memory-bank/tasks.md`가 갱신된다.

### T-026 / RF-6 Spike

- Spring Boot 공개 해석 API와 Python LLM 서비스의 책임 경계가 문서화되어 있다.
- Python 서비스는 FastAPI, LangChain, `langchain-openai`, Pydantic을 사용하기로 결정되어 있다.
- Spring Boot가 Python 서비스로 HTTP 호출하고, Python 서비스가 structured output을 반환하는 계약이 정의되어 있다.
- 자연어 원문을 DB, Outbox payload, audit metadata, 로그에 저장하지 않는 기준이 반영되어 있다.
- 해석 API는 `permitAll`, Redis rate limit은 Spring Boot 경계에서 처리하는 기준이 반영되어 있다.
- 상대 날짜는 서버 현재 날짜 기준으로 해석하고, 업종 추론 실패는 400으로 처리하는 기준이 반영되어 있다.
- Notion ADR 원본과 `memory-bank/decisions.md` 인덱스에 결정을 기록한다.

## Done

- [T-006] Transactional Outbox 기본 구조 구현 (Jira: RF-23, done: 2026-07-16, PR: #11 merge 완료, 검증: `.\backend\gradlew.bat test` 통과, `outbox_events` Flyway migration, OutboxEvent Entity/상태 enum/Repository, 트랜잭션 참여 전용 Appender 서비스, Builder 패턴 적용, 발행 대상 이벤트 조회 테스트 포함, GitHub PR/Slack 공유 완료, Jira 상태 변경: `완료` 전환 완료)
- [T-005] 공통 오류 응답과 Error Catalog 적용 (Jira: RF-22, done: 2026-07-15, 검증: `.\backend\gradlew.bat test` 통과, `.\backend\gradlew.bat bootRun` 로컬 실행과 실제 오류 응답 API 호출 검증 통과, 공통 오류 응답 DTO/Error Catalog/GlobalExceptionHandler/Spring Security 인증·인가 오류 응답 처리 포함, GitHub PR/Slack 공유 완료, Jira 상태 변경: `완료` 전환 완료)
- [T-004] 회원가입, 로그인, 토큰 발급/갱신과 Bearer Token 기반 JWT 인증 구현 (Jira: RF-21, done: 2026-07-08, 검증: `.\backend\gradlew.bat test` 통과, `.\backend\gradlew.bat bootRun` 로컬 실행과 실제 회원가입/로그인/현재 회원 조회/refresh token 재발급 API 호출 통과, PostgreSQL 18 기준 로컬 volume 경로 정리, Spring Boot `4.1.0` BOM 기준 PostgreSQL JDBC `42.7.11`, Lettuce `7.5.2.RELEASE`, Flyway `12.4.0`, Hibernate `7.4.1.Final` 버전 정렬과 PostgreSQL 18.4 경고 제거 확인, 회원가입과 로그인 토큰 발급 분리, Member 명칭 적용, 회원 내부 PK와 public ID 분리, 패키지 계층 정리와 Lombok 생성자 정리 포함, GitHub PR/Slack 공유 완료, Jira 상태 변경: `완료` 전환 완료)
- [T-003] Redis 연결과 기본 설정 (Jira: RF-20, done: 2026-07-06, 검증: `.\backend\gradlew.bat test` 통과, GitHub PR/Slack 공유: 미수행, Jira 상태 변경: `진행 중` 전환 완료)
- [T-025] Jira 티켓 작업 시작 시 `진행 중` 상태를 기본 전환하도록 운영 규칙 변경 (done: 2026-07-05)
- [T-002] PostgreSQL 연결과 Flyway 설정 (Jira: RF-19, done: 2026-07-05, 검증: `.\backend\gradlew.bat test` 통과, GitHub PR/Jira 상태 변경/Slack 공유: 미수행)
- [T-024] 주요 상태 변화 시 Slack 공유를 기본 수행하도록 운영 규칙 변경, 기본 채널 `#reserve-flow-dev`, `#reserve-flow-deploy`, `#reserve-flow-alerts` 생성 및 용도 매핑 (done: 2026-07-05)
- [T-023] 주요 상태 변화 시 Slack 공유 여부를 실행계획에 포함하도록 규칙화 (done: 2026-07-03)
- [T-022] PR merge UI 수행과 post-merge Memory Bank 단독 커밋 금지 원칙 문서화 (done: 2026-07-03)
- [T-021] Slack 공유 시점과 메시지 포함 항목 규칙화 (done: 2026-07-02)
- [T-020] RF-18 GitHub PR 생성과 Jira 검토 중 상태 반영 (done: 2026-07-02)
- [T-019] Jira 상태 전환 기준 보강과 RF-18 원격 push 상태 반영 (done: 2026-07-02)
- [T-001] Spring Boot 프로젝트 기본 구조 생성 (Jira: RF-18, done: 2026-07-02)
- [T-018] 브랜치 전략 문서 표현을 GitHub Flow 기준으로 재정리 (done: 2026-07-02)
- [T-017] 인증 범위와 브랜치 전략 문서 표현 정리, example.md 삭제 (done: 2026-07-02)
- [T-016] AGENTS.md Git 규칙을 GitHub Flow 기준으로 정리 (done: 2026-07-01)
- [T-015] AGENTS.md 중복 규칙 정리와 섹션 책임 분리 (done: 2026-07-01)
- [T-014] Git/Jira/GitHub PR/Slack 운영 규칙을 AGENTS.md로 통합 (done: 2026-07-01)
- [T-013] AGENTS.md 역할과 작업 운영 규칙 섹션 보강 (done: 2026-07-01)
- [T-012] Git 저장소 초기화 기준과 GitHub Flow 문서 규칙 보강 (done: 2026-06-30)
- [T-011] ADR source of truth를 Notion ADR로 통합하고 로컬 decisions 인덱스화 (done: 2026-06-30)
- [T-000] 문서 정리, Jira 프로젝트 확인, P0 Jira 티켓 생성, memory-bank 초기화 (done: 2026-06-30)

## Jira P0 생성 결과

- Epic: RF-1 EPIC-01 자연어 예약 요청
- Epic: RF-2 EPIC-02 예약 제공자와 booking slot 조회
- Epic: RF-3 EPIC-03 Hold 처리
- Epic: RF-4 EPIC-04 예약 처리
- Epic: RF-5 EPIC-06 공통 인프라
- P0 tickets: RF-6 ~ RF-23
