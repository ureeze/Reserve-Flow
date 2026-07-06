# Tasks

이 문서는 현재 실행할 작업 목록의 source of record이다. Jira 전체 백로그를 그대로 복사하지 않고, 실제 개발 진행에 필요한 가까운 작업만 유지한다.

개발 작업은 Jira Key 기준으로 진행한다. 문서 정리, 운영 규칙 정리, 로컬 관리 작업은 로컬 T-ID로 기록할 수 있다. 작업 완료 시 테스트/검증 결과, Memory Bank 갱신 여부, GitHub PR 생성 여부, Jira 상태 반영 여부, Slack 공유 여부를 필요에 따라 기록한다.

기본 완료 범위는 로컬 구현, 테스트/검증, Memory Bank 갱신이다. GitHub PR 생성은 실행계획에 포함하고 사용자 확인을 받은 경우에만 수행한다. Jira 상태 변경은 `AGENTS.md`의 Jira 규칙에 따라 작업 시작, PR 생성, PR merge 시점에 기본 전환한다. 주요 상태 변화의 Slack 공유는 `AGENTS.md` 기준에 따라 기본 수행한다.

## Next

- [T-004] 회원가입, 로그인, 토큰 발급/갱신과 Bearer Token 기반 JWT 인증 구현 (Jira: RF-21)
- [T-005] 공통 오류 응답과 Error Catalog 적용 (Jira: RF-22)
- [T-006] Transactional Outbox 기본 구조 구현 (Jira: RF-23)
- [T-007] 자연어 예약 요청 해석 API 구현 (Jira: RF-6)
- [T-008] 예약 조건 검증 API 구현 (Jira: RF-7)
- [T-009] 예약 제공자 검색 API 구현 (Jira: RF-8)
- [T-010] booking slot 조회 API 구현 (Jira: RF-9)

## In Progress

- 없음

## Blocked

- 없음

## Acceptance Criteria

### T-001 / RF-18

- Gradle 기반 Spring Boot 3.x 프로젝트가 생성되어 있다.
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

## Done

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
