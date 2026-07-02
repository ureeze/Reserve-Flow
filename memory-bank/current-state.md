# Current State

## 현재 마일스톤

ReserveFlow MVP 개발 착수 준비 완료.

문서 작업은 MVP 개발 시작 기준으로 정리 완료되었고, Jira 프로젝트와 P0 핵심 티켓이 생성되어 있다. `backend/`에 Spring Boot 백엔드 프로젝트 기본 구조가 생성되었고, 다음 단계는 PostgreSQL 연결과 Flyway 설정이다.

## 현재 진행 중 작업

- 없음

## 최근 완료 작업

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

- 원격 GitHub 저장소는 아직 연결되지 않았다.
- 프론트엔드 방식은 아직 확정되지 않았다. 후보는 HTML/CSS/JS 또는 React이다.
- 대기열 처리 P1 티켓은 아직 Jira에 실제 생성하지 않았다.

## 다음 작업

1. [T-002] PostgreSQL 연결과 Flyway 설정 (Jira: RF-19)
2. [T-003] Redis 연결과 기본 설정 (Jira: RF-20)
3. [T-004] 회원가입, 로그인, 토큰 발급/갱신과 Bearer Token 기반 JWT 인증 구현 (Jira: RF-21)
4. [T-005] 공통 오류 응답과 Error Catalog 적용 (Jira: RF-22)
5. [T-006] Transactional Outbox 기본 구조 구현 (Jira: RF-23)

## 관련 문서

- PRD: https://app.notion.com/p/3838a222ce3c80df8c21ef5333b0fa07
- ERD/DB 설계서: https://app.notion.com/p/3878a222ce3c80b7a4c2dc8d56921596
- API 명세서: https://app.notion.com/p/3898a222ce3c809694b1de28d89bf258
- Jira 작업 문서: https://app.notion.com/p/3898a222ce3c80dbb03df3949aa7a122
- Jira 프로젝트: https://ureeze.atlassian.net/jira/software/projects/RF/boards
