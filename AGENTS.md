# AGENTS.md

이 저장소는 ReserveFlow MVP 개발을 위한 작업 공간이다.

ReserveFlow는 사용자의 자연어 예약 요청을 구조화된 예약 조건으로 변환하고, 예약 제공자 검색, booking slot 조회, 비동기 Hold, 예약 최종 확인, 대기열 처리를 제공하는 AI 기반 범용 예약 엔진 플랫폼이다.

## 역할

- Codex는 ReserveFlow MVP 개발을 돕는 AI 개발 에이전트다.
- Notion 기준 문서와 Memory Bank를 바탕으로 작업 범위, 구현 방향, 검증 방법을 정한다.
- 구현 작업은 가능한 경우 구현, 테스트/검증, Memory Bank 갱신까지 완료한다.

## 작업 원칙

- 작업을 시작하기 전에 `memory-bank/current-state.md`를 먼저 읽는다.
- 이어서 현재 작업과 관련된 `memory-bank` 문서를 필요한 만큼 읽는다.
- Jira 티켓이 있는 작업은 Jira Key를 `tasks.md`와 커밋/PR 설명에 함께 남긴다.
- 구현 변경이 있으면 테스트 또는 검증 방법을 함께 수행한다.
- 작업 브랜치에서 수행한 변경은 작업 종료 전 `current-state.md`와 `tasks.md`를 갱신한다.
- PR merge 이후 상태 반영만을 위한 Memory Bank 단독 커밋은 `main`에 직접 만들지 않는다. 필요한 상태 정리는 다음 작업 브랜치 또는 별도 문서 브랜치의 PR에 포함한다.

## 실행 전 확인 원칙

코드 수정, 파일 생성/삭제, 구조 변경, Git 작업처럼 프로젝트 상태를 변경하는 작업은 실행 전에 사용자에게 실행계획을 먼저 보여주고 확인을 받는다.

실행계획에는 다음 내용을 포함한다.

- 작업 목표
- 수정 또는 생성할 파일
- 예상 변경 내용
- 검증 방법
- Memory Bank 갱신 여부
- 관련 Jira Key

단순 조회, 검색, 문서 확인, 코드 분석, 테스트 실행처럼 프로젝트 상태를 변경하지 않는 작업은 별도 확인 없이 수행할 수 있다.

사용자가 명시적으로 "바로 적용해줘", "진행해줘", "수정해줘"라고 요청한 경우에도, 코드 수정·파일 생성/삭제·구조 변경처럼 영향 범위가 있는 작업은 먼저 간단한 실행계획을 보여주고 진행한다.

## 프로젝트 시작 규칙

1. `memory-bank/current-state.md`를 먼저 읽는다.
2. 현재 작업의 Jira Key와 관련 문서를 확인한다.
3. 필요한 경우 `project-brief.md`, `architecture.md`, `tech-stack.md`, `coding-rules.md`, `decisions.md`를 추가로 읽는다.
4. 변경 범위, 검증 방법, 자동화 범위를 정한다.
5. 프로젝트 상태 변경 작업이면 Git 규칙에 따라 저장소와 브랜치를 확인한다.
6. Git 저장소가 없거나 브랜치 정리가 필요하면 Git 규칙에 따라 실행계획에 반영한다.

## 문서 우선순위

- 제품 요구사항과 설계 기준은 Notion 문서를 우선한다.
- ADR 원본은 Notion ADR 문서를 기준으로 한다. `memory-bank/decisions.md`는 작업 편의를 위한 로컬 인덱스이자 요약 캐시다.
- Notion 문서와 Memory Bank가 충돌하면 Notion 문서를 기준으로 판단하고, 작업 종료 시 Memory Bank를 갱신한다.
- Memory Bank 구조와 각 문서의 역할은 `memory-bank/project-brief.md`를 따른다.

## 작업 자동화 범위

개발 작업은 Jira 티켓 단위로 진행한다. 작업 시작 전 실행계획에는 이번 작업의 자동화 범위를 반드시 명시한다.

자동화 범위는 다음 중 하나로 구분한다.

- 로컬 구현만 수행
- 로컬 구현 + 테스트 + Memory Bank 갱신
- 로컬 구현 + 테스트 + Memory Bank 갱신 + GitHub PR 생성
- 로컬 구현 + 테스트 + Memory Bank 갱신 + GitHub PR 생성 + Jira 상태 변경
- 로컬 구현 + 테스트 + Memory Bank 갱신 + GitHub PR 생성 + Jira 상태 변경 + Slack 공유

기본값은 `로컬 구현 + 테스트 + Memory Bank 갱신`이다. GitHub push와 PR 생성은 실행계획에 포함하고 사용자 확인을 받은 경우에만 수행한다. Jira 상태 변경은 Jira 규칙의 기본 전환 기준을 따른다. Slack 공유 필수 상황은 Slack 공유 규칙에 따라 기본 수행한다.

## Git 규칙

- 프로젝트 상태 변경 작업을 시작하기 전에 `git status --short --branch`로 Git 저장소와 현재 브랜치를 확인한다.
- 현재 디렉터리가 Git 저장소가 아니면 바로 구현하지 않는다. 실행계획에 `git init`, 기본 브랜치 생성, 최초 커밋 여부, 원격 저장소 연결 여부를 포함하고 사용자 확인을 받은 뒤 진행한다.
- Git 저장소가 있지만 작업 브랜치가 Jira Key 기준 브랜치가 아니면, 실행계획에 브랜치 생성 또는 전환을 포함하고 사용자 확인을 받은 뒤 진행한다.
- 브랜치 전략은 GitHub Flow를 따른다.
- `main`은 배포와 릴리스 기준 브랜치로 유지한다.
- Jira 티켓 작업 브랜치는 `main` 또는 사용자와 합의한 기본 브랜치에서 분기한다.
- Jira 티켓 작업 브랜치는 `feature/{JiraKey}-{short-summary}` 형식을 기본으로 한다. 예: `feature/RF-18-spring-boot-bootstrap`
- 기능 작업 완료 후 PR은 작업 브랜치에서 `main` 또는 사용자와 합의한 기본 브랜치로 보낸다.
- 운영 긴급 수정도 Jira Key 기반 작업 브랜치로 처리한다.
- 원격 저장소가 없거나 연결되지 않은 경우에도 로컬 구현은 진행할 수 있지만, push와 PR 생성은 원격 연결 계획을 사용자에게 확인받은 경우에만 수행한다.
- 커밋 메시지는 Jira Key로 시작한다. 예: `RF-18 Bootstrap Spring Boot project`

## Jira 규칙

- Jira 프로젝트 key는 `RF`이다.
- 개발 작업은 Jira 티켓 단위로 진행한다.
- 작업 시작 시 Jira 티켓을 확인한다.
- Jira 티켓 작업을 시작해 작업 브랜치를 생성하거나 구현에 착수하면 Jira 상태를 `진행 중`으로 기본 전환한다.
- 사용자가 명시적으로 Jira 상태 변경 생략을 요청한 경우에는 작업 시작 시 `진행 중`으로 전환하지 않는다.
- 진행 중 의미 있는 중간 결과가 생기면 Jira 댓글로 요약할 수 있다.
- 구현과 push가 완료되었지만 GitHub PR이 아직 없으면 Jira 상태는 `진행 중`을 유지한다.
- GitHub PR을 생성하면 Jira 티켓에 PR 링크와 검증 결과를 댓글로 남기고 상태를 `검토 중`으로 변경한다.
- PR merge 또는 사용자 승인 후 Jira 상태를 `완료`로 변경한다.
- Jira 댓글에는 작업 요약, 검증 결과, PR 링크를 남긴다.

## GitHub PR 규칙

- PR 대상 브랜치는 GitHub Flow 기준을 따른다.
- 기능 작업 PR은 작업 브랜치에서 `main` 또는 사용자와 합의한 기본 브랜치로 보낸다.
- PR 제목과 본문에는 Jira Key, 변경 요약, 검증 결과를 포함한다.
- PR 본문에는 후속 작업 또는 남은 위험이 있으면 함께 남긴다.
- GitHub push와 PR 생성은 사용자가 요청하거나 실행계획에 포함되어 확인된 경우에만 수행한다.
- PR merge는 사용자가 GitHub UI에서 직접 수행하는 것을 기본 원칙으로 한다.
- Codex는 PR merge 전 변경 요약, 검증 결과, Jira 상태, merge 전 확인 사항을 정리하고 사용자에게 GitHub UI에서 merge할 수 있도록 안내한다.
- 사용자가 PR merge 완료를 알려주면 Codex는 로컬 `main` 최신화, PR/Jira 상태 확인, 다음 작업 브랜치 준비를 수행한다.
- 사용자가 명시적으로 요청한 예외 상황이 아니라면 Codex가 GitHub API나 CLI로 PR을 직접 merge하지 않는다.
- PR merge 이후 Memory Bank 상태 반영만을 위한 별도 커밋과 원격 push는 수행하지 않는다.
- merge 이후 필요한 문서 정리는 다음 기능 브랜치 또는 별도 문서 브랜치에서 PR로 반영한다.

## Slack 공유 규칙

- Slack 공유는 팀원이 알아야 할 상태 변화가 생겼을 때 수행한다.
- 다음 상황은 Slack 공유를 기본 수행한다.
  - GitHub PR 생성으로 리뷰가 필요한 상태가 된 경우
  - PR merge 또는 작업 완료로 다음 작업자가 이어받을 수 있는 상태가 된 경우
  - 배포 준비, 배포 시작, 배포 완료처럼 운영 영향이 있는 경우
  - 블로커, 장애, 의사결정 필요 사항처럼 팀 확인이나 도움이 필요한 경우
  - 주요 요구사항, 설계, 일정 변경처럼 팀 동기화가 필요한 경우
  - 운영 긴급 수정 시작 또는 완료처럼 영향도가 높은 경우
- 위 상황의 실행계획에는 Slack 공유 예정 채널, 메시지 요약, 전송 시점을 포함한다.
- 사용자가 명시적으로 Slack 공유 생략을 요청한 경우에만 위 상황의 Slack 공유를 생략한다.
- Slack 공유 채널이 정해져 있지 않거나 전송 권한이 없으면 전송 전 사용자에게 채널 또는 권한 확인을 요청한다.
- 기본 Slack 공유 채널은 다음 기준을 따른다.
  - `#reserve-flow-dev`: GitHub PR 생성, PR merge 또는 작업 완료, 개발 블로커, 의사결정 필요, 주요 요구사항/설계/일정 변경
  - `#reserve-flow-deploy`: 배포 준비, 배포 시작, 배포 완료, 롤백
  - `#reserve-flow-alerts`: 장애, 운영 긴급 수정 시작/완료, 운영 영향이 있는 블로커 또는 긴급 의사결정
- 다음 상황은 기본적으로 Slack 공유를 생략한다.
  - 로컬 문서 정리만 수행한 경우
  - 커밋 또는 push만 있고 PR이 아직 없는 경우
  - 단순 오타 수정, 테스트 재실행처럼 팀 액션이 필요 없는 경우
  - Jira 또는 PR 기록만으로 충분하고 별도 알림 가치가 낮은 경우
- PR 생성 시 Slack 메시지는 Jira Key, PR 링크, 변경 요약, 검증 결과, 리뷰 요청 또는 다음 액션을 짧게 포함한다.
- 작업 완료 또는 배포 관련 Slack 메시지는 완료 범위, 영향 범위, 검증 결과, 후속 액션을 짧게 포함한다.

## 테스트 규칙

- 구현 변경이 있으면 관련 테스트 또는 검증 방법을 함께 수행한다.
- Java/Spring 테스트는 JUnit 5와 Spring Boot Test를 기준으로 한다.
- DB 제약, Migration, 동시성은 통합 테스트로 검증한다.
- 반드시 검증해야 하는 핵심 경합은 Hold 생성 동시 요청, Hold 만료와 예약 최종 확인 경합, Idempotency-Key 재요청이다.
- 테스트를 실행할 수 없으면 사유와 대체 검증 방법을 작업 결과에 남긴다.

## 배포 규칙

- 현재 프로젝트는 초기 로컬 개발 기준이다.
- 배포, 운영 환경 변경, 환경변수 변경, DB migration 적용은 실행계획에 포함하고 사용자 확인을 받은 경우에만 수행한다.
- 적용된 Flyway Migration은 수정하지 않는다. 변경은 새 Migration으로 추가한다.
- 배포 또는 운영 반영 전에는 관련 테스트/검증 결과를 확인한다.

## 코딩 스타일

- Notion PRD/API/ERD의 용어를 코드와 문서에 일관되게 사용한다.
- 현재 기준 용어는 `booking_provider`, `booking_slot`, `hold_request`, `hold`, `reservation`, `waitlist`이다.
- Java 21과 Spring Boot 3.x를 기준으로 한다.
- Controller, Service, Repository 책임을 분리한다.
- API DTO와 Entity를 직접 공유하지 않는다.
- 상태값은 Java enum으로 표현하되 DB CHECK 값과 반드시 일치시킨다.
- 외부 입력은 Bean Validation과 도메인 검증을 함께 사용한다.

## 기준 문서

- PRD: https://app.notion.com/p/3838a222ce3c80df8c21ef5333b0fa07
- ERD/DB 설계서: https://app.notion.com/p/3878a222ce3c80b7a4c2dc8d56921596
- API 명세서: https://app.notion.com/p/3898a222ce3c809694b1de28d89bf258
- Figma 화면 설계서: https://app.notion.com/p/3898a222ce3c812d9e6de9191a3a731e
- QA 테스트 케이스: https://app.notion.com/p/3828a222ce3c806481efc5d6ce4ac293
- ADR: https://app.notion.com/p/3828a222ce3c80b481ebfb7fcfb3476c
- Jira 작업 문서: https://app.notion.com/p/3898a222ce3c80dbb03df3949aa7a122
- Jira 프로젝트: https://ureeze.atlassian.net/jira/software/projects/RF/boards

## 작업 종료 체크리스트

- [ ] 요청한 작업을 완료했다.
- [ ] 관련 테스트 또는 검증을 수행했다.
- [ ] 프로젝트 상태 변경 작업이면 Git 저장소와 브랜치 상태를 확인했다.
- [ ] 필요한 경우 Notion/Jira와 용어가 일치하는지 확인했다.
- [ ] `memory-bank/current-state.md`를 갱신했다.
- [ ] `memory-bank/tasks.md`를 갱신했다.
- [ ] 새 결정이 생겼다면 Notion ADR에 기록하고 `memory-bank/decisions.md` 인덱스를 갱신했다.
- [ ] 문제 해결 기록이 필요하면 `memory-bank/troubleshooting.md`에 남겼다.

## 개발 우선순위

현재 MVP 개발 시작 순서는 Jira P0 백로그를 기준으로 한다.

1. RF-18 Spring Boot 프로젝트 기본 구조 생성
2. RF-19 PostgreSQL 연결과 마이그레이션 도구 설정
3. RF-20 Redis 연결과 기본 설정
4. RF-21 회원가입, 로그인, 토큰 발급/갱신과 Bearer Token 기반 JWT 인증 구현
5. RF-22 공통 오류 응답과 Error Catalog 적용
6. RF-23 Transactional Outbox 기본 구조 구현

이후 `booking provider/booking slot -> Hold -> Reservation -> Waitlist` 순서로 구현한다.

## 금지 사항

- PRD/API/ERD 기준과 다른 용어를 임의로 도입하지 않는다.
- `resource`, `slot`처럼 이전 용어를 새 코드에 도입하지 않는다. 현재 기준은 `booking_provider`, `booking_slot`이다.
- 민감정보, JWT, 개인정보, 원문 프롬프트를 로그나 audit metadata에 저장하지 않는다.
- 적용된 Flyway Migration을 수정하지 않는다. 변경은 새 Migration으로 추가한다.
- 외부 API, LLM, Kafka 호출을 DB 트랜잭션 안에서 수행하지 않는다.

