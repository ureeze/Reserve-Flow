# Current State

## 현재 마일스톤

ReserveFlow MVP 개발 착수 준비 완료.

문서 작업은 MVP 개발 시작 기준으로 정리 완료되었고, Jira 프로젝트와 P0 핵심 티켓이 생성되어 있다. `backend/`에 Spring Boot 백엔드 프로젝트 기본 구조, PostgreSQL/Flyway 연결 기반, Redis 연결 기본 설정, JWT 인증 기본 구현이 준비되었다. 다음 단계는 공통 오류 응답과 Error Catalog 적용이다.

## 현재 진행 중 작업

- 없음

## 최근 완료 작업

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
- RF-21 완료: 로컬 PostgreSQL Docker 이미지를 `postgres:17-alpine`으로 정리해 현재 볼륨 마운트 경로와 호환되도록 수정
- RF-21 Jira 상태를 `진행 중`으로 전환
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
- Jira P0 핵심 백로그 생성 완료: `RF-1` ~ `RF-23`
- 로컬 `AGENTS.md`와 `memory-bank`를 ReserveFlow 프로젝트 기준으로 초기화

## 열린 이슈

- 프론트엔드 방식은 아직 확정되지 않았다. 후보는 HTML/CSS/JS 또는 React이다.
- 대기열 처리 P1 티켓은 아직 Jira에 실제 생성하지 않았다.

## 다음 작업

1. [T-005] 공통 오류 응답과 Error Catalog 적용 (Jira: RF-22)
2. [T-006] Transactional Outbox 기본 구조 구현 (Jira: RF-23)
3. [T-007] 자연어 예약 요청 해석 API 구현 (Jira: RF-6)

## 관련 문서

- PRD: https://app.notion.com/p/3838a222ce3c80df8c21ef5333b0fa07
- ERD/DB 설계서: https://app.notion.com/p/3878a222ce3c80b7a4c2dc8d56921596
- API 명세서: https://app.notion.com/p/3898a222ce3c809694b1de28d89bf258
- Jira 작업 문서: https://app.notion.com/p/3898a222ce3c80dbb03df3949aa7a122
- Jira 프로젝트: https://ureeze.atlassian.net/jira/software/projects/RF/boards
