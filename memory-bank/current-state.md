# Current State

## 현재 마일스톤

ReserveFlow MVP 공통 인프라, 자연어 예약 요청 해석 API, 예약 조건 검증 API가 완료되었고, 협업 도구 연동(Jira↔GitHub, Slack↔Jira, Slack↔GitHub)과 엔티티 식별자 전략(내부 `bigint id` + 외부 `uuid public_id`)까지 정비되었다.

문서 작업은 MVP 개발 시작 기준으로 정리 완료되었고, Jira 프로젝트에는 Notion Jira 작업 문서 기준 Epic과 하위 티켓이 `RF-1` ~ `RF-56`까지 생성되어 있다. `backend/`에 Spring Boot 백엔드 프로젝트 기본 구조, PostgreSQL/Flyway 연결 기반, Redis 연결 기본 설정, JWT 인증 기본 구현, 공통 오류 응답과 Error Catalog, Transactional Outbox 기본 구조가 준비되었다. Python FastAPI + LangChain 기반 자연어 예약 요청 해석 API와 Redis rate limit은 PR #13 merge 후 Jira `RF-6` 완료 상태로 반영되었다. 이후 예약 조건 검증 API(RF-7)와 엔티티 식별자 전략 통일(RF-56)까지 완료했고, Slack↔Jira·Slack↔GitHub·Jira↔GitHub 알림 연동과 "PR 머지 시 Jira 자동 완료 전환"을 구성했다.

## 현재 진행 중 작업

- [T-009] RF-8 예약 제공자 검색 API 구현 완료 및 PR #27 검토 중
- [T-010] RF-9 booking slot 조회 API 구현 및 GitHub PR #28 생성 (PR: https://github.com/ureeze/Reserve-Flow/pull/28, Jira 상태: `검토 중`)
- [T-037] Jira 티켓 타입 정정·미등록 기능 작업 선등록 규칙화·완료 작업 소급 등록: RF-41 스토리 정정, AGENTS.md Jira 규칙 추가, CI/연동 완료 작업 RF-57~59 소급 등록. PR 생성 예정

## 최근 완료 작업

- RF-8 진행: QueryDSL 도입(build.gradle 의존성·`JPAQueryFactory` 설정·custom repository), `GET /api/v1/booking-providers` 검색 API, size clamp(최대 50), `partySize<1` → `400 VALIDATION_002`, 신규 테스트 7건 통과
- Jira 정비: RF-41(대기열 취소 API)을 작업→스토리로 정정, 미등록 기능 작업 Jira 선등록 규칙과 타입 컨벤션을 AGENTS.md에 명문화, CI/배포(RF-57)·Jira 연동 자동화(RF-58)·Slack 알림 연동(RF-59)을 EPIC-08 하위 완료 티켓으로 소급 등록 (T-037)
- 문서 정합성 점검: memory-bank 3개 파일 낡은 내용 갱신, ERD 4.2~4.13 테이블 정의서 전체를 새 식별자 구조로 동기화 (T-036)
- RF-56 완료: PR #24 merge 후 Jira 자동화(GitHub for Jira)가 `완료`로 자동 전환 — 자동화 첫 실전 동작 확인 (T-035)
- RF-56 진행: ADR-005 개정(내부 bigint PK + 외부 uuid public_id) 및 ERD(2.2·물리 ERD·6.1·4.2·4.3) 동기화, ADR-008 Slack 현행화
- RF-56 진행: `booking_providers`·`booking_provider_business_hours` 마이그레이션(V3·V4 재작성)·엔티티·Repository를 `bigint id + uuid public_id`로 전환, `findByPublicId` 외부 조회 적용, `.\backend\gradlew.bat test` 통과
- RF-56 착수: Jira 상태를 `진행 중`으로 전환하고 `feature/RF-56-entity-id-strategy` 브랜치 생성
- Slack 알림 연동 정비: Slack↔Jira, Slack↔GitHub 공식 앱 연결·검증 완료. GitHub Actions의 Slack 전송은 제거하고 알림을 공식 앱으로 일원화 (T-034)
- Jira 자동화 도입: GitHub for Jira 연동 + Jira Automation "PR 병합 시 이슈 완료" 규칙 활성화. 이후 머지 후 `완료` 전환은 자동화가 담당 (T-033)
- RF-55 완료: PR #20 merge 후 Jira 상태를 `완료`로 전환 (T-032)
- RF-7 완료: PR #18 merge 후 Jira 상태를 `완료`로 전환 (T-008)
- Slack 공유 정책 정리 완료: PR #19 merge (T-031)
- RF-55 GitHub PR 생성: https://github.com/ureeze/Reserve-Flow/pull/20
- RF-55 진행: `ReservationRequestValidationService`의 `validate`/`isOutsideBusinessHours`/`toViolation` 메서드 주석 보강 (로직 변경 없음)
- RF-7 진행: ERD 기준 `booking_providers`, `booking_provider_business_hours` Flyway migration(V3, V4)과 JPA Entity/Repository 추가
- RF-7 진행: `POST /api/v1/reservation-requests/validate` 구현, provider 존재/영업시간/최대 인원/과거 날짜 검증과 `VALIDATION_001~003` violation, `404 PROVIDER_001` 처리 추가
- RF-7 검증: `.\backend\gradlew.bat test` 통과 (신규 테스트 6건 포함)
- RF-7 착수: Jira 상태를 `진행 중`으로 전환하고 `feature/RF-7-reservation-request-validation` 브랜치 생성
- GitHub Actions Node.js 20 deprecation 경고 수정 완료: PR #17 merge 후 `actions/checkout@v4`→`v6`, `actions/setup-java@v4`→`v5`로 Action 버전 갱신 (T-030)
- T-031 GitHub PR 생성: https://github.com/ureeze/Reserve-Flow/pull/19
- Slack 공유 정책 정리: Codex/클로드 코드의 직접 Slack 전송을 전면 폐지하고 GitHub Actions(`pr-check.yml`, `deploy.yml`)가 전담하도록 `AGENTS.md` Slack 공유 규칙과 작업 자동화 범위를 재정리 (T-031)
- RF-6 후속 리팩터링 완료: PR #16 squash merge 후 자연어 추출 API 관련 클래스명을 짧은 이름으로 정리
- GitHub Actions Slack 알림 step 실패 수정 완료: PR #15 squash merge 후 `STATUS_TEXT` 환경변수 전달과 알림 step `continue-on-error` 설정 반영
- GitHub Actions 자동화 완료: PR #14 merge 후 PR Check 성공과 Deploy workflow 자동 실행 확인
- GitHub Actions 자동화 확인: Deploy workflow는 Slack 알림 step의 `STATUS_TEXT` 환경변수 전달 누락으로 실패했으며, 알림 step 수정 작업을 진행한다
- GitHub Actions 자동화 진행: PR 대상 `main` 테스트 workflow와 `main` push 배포 placeholder workflow 추가
- GitHub Actions 자동화 진행: `SLACK_WEBHOOK_URL` Repository Secret 기반 테스트/배포 성공·실패 Slack 알림 구조 추가
- 운영 규칙 진행: GitHub Actions 테스트/배포 결과 알림은 Codex 직접 Slack 전송보다 Actions 자동 알림을 우선하도록 정리
- RF-6 구현: Spring Boot 공개 자연어 해석 API, Python FastAPI + LangChain structured output 서비스, Pydantic 응답 계약 추가
- RF-6 구현: Redis Lua script 기반 사용자/IP 분당 10회 요청 제한과 `PARSE_004`, `RATE_LIMIT_001`, `LLM_001` 공통 오류 추가
- RF-6 검증: `.\backend\gradlew.bat test` 통과, Python 모의 structured-output 테스트 통과, Docker Compose 기반 실제 OpenAI 호출과 Spring Boot 공개 API 연동 검증 완료
- RF-6 완료: PR #13 squash merge 후 Jira 상태를 `완료`로 전환
- Jira 백로그 보강: Notion Jira 작업 문서 기준으로 누락 Epic과 P1/P2 하위 티켓을 `RF-24` ~ `RF-54`로 생성 완료
- Jira 백로그 보강: EPIC-05 대기열 처리, EPIC-07 프론트엔드 화면, EPIC-08 QA와 운영 준비와 하위 작업 등록 완료
- RF-23 착수: Jira 상태를 `진행 중`으로 전환하고 `feature/RF-23-transactional-outbox` 브랜치 생성
- RF-23 진행: `outbox_events` Flyway migration 추가
- RF-23 진행: OutboxEvent Entity, 상태 enum, Repository, 트랜잭션 참여 전용 Appender 서비스 추가
- RF-23 진행: OutboxEvent 생성에 Builder 패턴을 적용해 문자열 인자 순서 실수 위험 완화
- RF-23 진행: Outbox 이벤트 저장, 기존 트랜잭션 필수 참여, 발행 대상 이벤트 조회 테스트 추가
- RF-23 검증: `.\backend\gradlew.bat test` 통과
- RF-23 완료: PR #11 squash merge 후 Jira 상태를 `완료`로 전환하고 Slack 공유 완료
- Memory Bank 작업 상태 흐름을 `In Progress -> Review -> Done` 기준으로 정리
- RF-22 완료: 공통 오류 응답 DTO, Error Catalog, GlobalExceptionHandler, Spring Security 인증/인가 오류 응답 처리 적용
- RF-22 완료: Validation/Auth/Not Found/Conflict 오류 응답 테스트와 로컬 API 검증 완료
- RF-22 완료: PR #10 squash merge 후 Jira 상태를 `완료`로 전환하고 Slack 공유 완료
- RF-21 완료: Spring Security와 OAuth2 Resource Server 기반 JWT 의존성 추가
- RF-21 완료: `members` 테이블 Flyway migration, Member Entity, Repository 추가
- RF-21 완료: 회원 내부 PK는 `Long id`, API/JWT 노출 식별자는 `UUID publicId`로 분리
- RF-21 완료: 회원가입, 로그인, refresh token 재발급, 현재 회원 조회 API 추가
- RF-21 완료: 회원가입은 회원 생성만 수행하고, access/refresh token은 로그인 성공 시 발급하도록 분리
- RF-21 완료: BCrypt 비밀번호 해시, HS256 JWT access/refresh token 발급과 Bearer 인증 설정 추가
- RF-21 완료: 인증 코드를 `controller`, `service`, `dto`, `config`, `entity`, `repository` 계층 패키지로 정리
- RF-21 완료: Lombok을 도입해 생성자 주입과 JPA 기본 생성자 반복 코드를 정리
- RF-21 완료: 인증 성공/실패, 중복 가입, 토큰 재발급, 보호 API 접근 테스트 추가, `.\backend\gradlew.bat test` 통과
- RF-21 완료: 로컬 PostgreSQL/Redis 기반 `.\backend\gradlew.bat bootRun` 실행과 실제 회원가입, 로그인, 현재 회원 조회, refresh token 재발급 API 호출 검증 완료
- RF-21 완료: 로컬 PostgreSQL Docker 이미지를 `postgres:18.4`로 유지하고 PostgreSQL 18 기준 볼륨 경로(`/var/lib/postgresql`)와 전용 volume으로 정리
- RF-21 완료: Spring Boot `4.1.0` BOM 기준으로 PostgreSQL JDBC `42.7.11`, Lettuce `7.5.2.RELEASE`, Flyway `12.4.0`, Hibernate `7.4.1.Final` 버전 정렬
- RF-21 완료: Boot 4 기준 `spring-boot-starter-flyway`, `spring-boot-starter-webmvc-test` 추가와 Hibernate dialect 자동 감지 설정 정리
- RF-21 완료: Spring Boot `4.1.0`, PostgreSQL `18.4`, Redis `8.2-alpine` 조합으로 `.\backend\gradlew.bat test`와 `bootRun`/실제 API 호출 재검증 완료
- RF-22 착수: Jira 상태를 `진행 중`으로 전환하고 `feature/RF-22-error-catalog` 브랜치 생성
- RF-21 완료: PR #9 squash merge 후 Jira 상태를 `완료`로 전환
- Jira 티켓 작업 시작 시 `진행 중` 상태를 기본 전환하도록 운영 규칙 변경
- RF-20 완료: Spring Data Redis 의존성 추가
- RF-20 완료: 환경변수로 덮어쓸 수 있는 Redis 연결 설정 추가
- RF-20 완료: 로컬 Redis 실행용 `docker-compose.yml` 서비스와 healthcheck 추가
- RF-20 완료: 테스트 profile Redis 설정 추가, `.\backend\gradlew.bat test` 통과
- RF-20 Jira 상태를 `진행 중`으로 전환
- RF-19 완료: PostgreSQL JDBC Driver, Spring Data JPA, Flyway 의존성 추가
- RF-19 완료: 환경변수로 덮어쓸 수 있는 datasource/Flyway/JPA 설정 추가
- RF-19 완료: 로컬 PostgreSQL 실행용 `docker-compose.yml` 추가
- RF-19 완료: Flyway migration 디렉터리 준비, 테스트 profile context load 설정 추가, `.\backend\gradlew.bat test` 통과
- 주요 상태 변화 시 Slack 공유를 기본 수행하도록 운영 규칙 변경, 기본 채널 `#reserve-flow-dev`, `#reserve-flow-deploy`, `#reserve-flow-alerts` 생성 및 용도 매핑
- PR 생성, PR merge/완료, 배포, 블로커, 주요 변경, 긴급 수정 시 실행계획에 Slack 공유 여부를 반드시 포함하도록 규칙화
- PR merge는 사용자가 GitHub UI에서 직접 수행하고, merge 이후 Memory Bank 단독 커밋/push는 하지 않는 운영 원칙을 문서화
- Slack 공유 시점을 PR 생성, PR merge/완료, 배포, 블로커, 주요 변경, 긴급 수정 기준으로 규칙화
- RF-18 GitHub PR 생성: https://github.com/ureeze/Reserve-Flow/pull/1
- Jira RF-18에 PR 링크와 검증 결과를 댓글로 남기고 상태를 `검토 중`으로 전환
- Jira 상태 전환 기준에 PR 생성 전 push 완료 상태는 `진행 중` 유지 원칙을 추가
- RF-18 feature 브랜치를 GitHub 원격 저장소에 push
- RF-18 완료: Git 저장소 초기화, GitHub Flow feature 브랜치 생성, `backend/` Spring Boot 3.5.16 Gradle 프로젝트 기본 구조 생성, `.\backend\gradlew.bat test` 통과
- OpenAPI 문서화 기준을 파일 선작성 방식에서 코드 우선 방식으로 정리
- `AGENTS.md`와 Memory Bank의 브랜치 전략 표현을 GitHub Flow 기준으로 재정리
- 인증 발급 API를 MVP 범위에 포함하도록 로컬 문서 기준 정리
- `tasks.md`의 Jira Key 기준을 개발 작업과 로컬 문서 정리 작업 기준으로 분리
- `example.md` 삭제
- `AGENTS.md`의 Git/GitHub PR 규칙을 GitHub Flow 기준으로 분리 정리
- `AGENTS.md`의 반복 규칙을 줄이고 섹션별 책임을 명확히 정리
- Git/Jira/GitHub PR/Slack 운영 규칙을 `AGENTS.md`로 통합하고 `memory-bank/coding-rules.md`는 코드 작성 기준 중심으로 정리
- `AGENTS.md`에 역할, 프로젝트 시작 규칙, 문서 우선순위, 테스트 규칙, 배포 규칙, 코딩 스타일 섹션 보강
- RF-18 착수 전 문서 표현과 완료 조건 정리
- Git 저장소가 없는 상태에서 프로젝트 작업을 시작할 경우, 실행계획에 `git init`과 브랜치/원격 처리 방안을 포함하도록 문서화
- GitHub Flow와 Jira Key 기반 feature 브랜치 규칙을 문서화
- Notion ADR을 ADR 원본 문서로 정리
- `memory-bank/decisions.md`를 Notion ADR 링크 인덱스와 요약 캐시로 축소
- `AGENTS.md`에 ADR source of truth 원칙 추가
- PRD 정리 완료
- ERD/DB 설계서 정리 완료
- API 명세서 정리 완료
- Figma 화면 설계서 정리 완료
- QA 테스트 케이스 문서 위치 확정
- Jira 작업 문서 작성 완료
- Jira 프로젝트 `RF` 생성 확인
- Jira MVP 백로그 생성 완료: `RF-1` ~ `RF-54`
- 로컬 `AGENTS.md`와 `memory-bank`를 ReserveFlow 프로젝트 기준으로 초기화

## 열린 이슈

- 프론트엔드 방식은 아직 확정되지 않았다. 후보는 HTML/CSS/JS 또는 React이다.

## 다음 작업

1. [T-011] Hold 생성 요청 API 구현 (Jira: RF-10)

## 관련 문서

- PRD: https://app.notion.com/p/3838a222ce3c80df8c21ef5333b0fa07
- ERD/DB 설계서: https://app.notion.com/p/3878a222ce3c80b7a4c2dc8d56921596
- API 명세서: https://app.notion.com/p/3898a222ce3c809694b1de28d89bf258
- Jira 작업 문서: https://app.notion.com/p/3898a222ce3c80dbb03df3949aa7a122
- Jira 프로젝트: https://ureeze.atlassian.net/jira/software/projects/RF/boards
