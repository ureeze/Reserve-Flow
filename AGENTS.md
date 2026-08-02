# AGENTS.md

이 저장소는 ReserveFlow MVP 개발을 위한 작업 공간이다.

ReserveFlow는 사용자의 자연어 예약 요청을 구조화된 예약 조건으로 변환하고, 예약 제공자 검색, booking slot 조회, 비동기 Hold, 예약 최종 확인, 대기열 처리를 제공하는 AI 기반 범용 예약 엔진 플랫폼이다.

## 역할

- AI 개발 에이전트는 ReserveFlow MVP 개발을 돕는다.
- Jira 티켓과 Notion 기준 문서와 Memory Bank를 바탕으로 작업 범위, 구현 방향, 검증 방법을 정한다.
- 구현 작업은 구현, 테스트/검증, Memory Bank 갱신까지 완료한다.

## 작업 흐름

```text
Jira 작업 시작
→ Jira: In Progress
→ feature/{JiraKey}-{short-summary} 브랜치
→ 구현 + 테스트
→ 커밋
→ PR 생성
→ Jira: Review
→ 사용자가 PR Merge
→ GitHub ↔ Jira 자동화로 Jira: Done
```

## 상태 관리 원칙

- **Jira를 작업 상태의 Source of Truth로 사용한다.**
- Jira 상태는 `In Progress → Review → Done` 흐름으로 관리한다.
- PR Merge 후 Jira가 `Done`이면 작업 완료로 판단한다.
- 다음 작업 시작 시 Jira가 `Done`인지 확인하면 되며, 매번 GitHub PR의 Merge 여부까지 별도로 확인하지 않는다.
- Jira와 GitHub 상태가 불일치하거나 실제 확인이 필요한 경우에만 GitHub PR 상태를 확인한다.
- `1 Jira = 1 PR`을 기본으로 하되, 하나의 Jira에 여러 논리적 커밋이 포함되는 것은 허용한다.

## 작업 원칙

- 작업을 시작하기 전에 현재 작업의 Jira 티켓과 관련 `memory-bank` 문서를 필요한 만큼 읽는다.
- Jira 티켓이 있는 작업은 Jira Key를 커밋/PR 설명에 함께 남긴다.
- 구현 변경이 있으면 테스트 또는 검증 방법을 함께 수행한다.
- 작업 브랜치에서 수행한 변경은 작업 종료 전 커밋하고 PR 생성 여부를 사용자와 확인한다.

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

1. 현재 작업의 Jira Key와 관련 문서를 확인한다.
2. 필요한 경우 `project-brief.md`, `architecture.md`, `tech-stack.md`, `coding-rules.md`, `decisions.md`를 추가로 읽는다.
3. 변경 범위, 검증 방법, 자동화 범위를 정한다.
4. 프로젝트 상태 변경 작업이면 Git 규칙에 따라 저장소와 브랜치를 확인한다.
5. Git 저장소가 없거나 브랜치 정리가 필요하면 Git 규칙에 따라 실행계획에 반영한다.

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

기본값은 `로컬 구현 + 테스트 (+ 필요 시에만 Memory Bank 갱신)`이다. Memory Bank는 구현·기술 변경 시에만 드물게 바뀌는 판단 기준 문서만 갱신한다. GitHub push와 PR 생성은 실행계획에 포함하고 사용자 확인을 받은 경우에만 수행한다. Jira 상태 변경은 Jira 규칙의 기본 전환 기준을 따른다. Slack 알림은 GitHub·Jira 공식 Slack 앱이 담당하므로 작업 자동화 범위에 포함하지 않는다.

## Git 규칙

- 프로젝트 상태 변경 작업을 시작하기 전에 `git status --short --branch`로 Git 저장소와 현재 브랜치를 확인한다.
- 브랜치 전략은 GitHub Flow를 따른다.
- `main`은 배포와 릴리스 기준 브랜치로 유지한다.
- Jira 티켓 작업 브랜치는 `main` 또는 사용자와 합의한 기본 브랜치에서 분기한다.
- Jira 티켓 작업 브랜치는 `feature/{JiraKey}-{short-summary}` 형식을 기본으로 한다. 예: `feature/RF-18-spring-boot-bootstrap`
- 기능 작업 완료 후 PR은 작업 브랜치에서 `main` 또는 사용자와 합의한 기본 브랜치로 보낸다.
- 운영 긴급 수정도 Jira Key 기반 작업 브랜치로 처리한다.
- 커밋 메시지는 개발 커밋과 PR 제목을 구분해 작성한다.
  - 개발 커밋: `{type}({scope}): {설명}` 형식을 사용한다. 예: `feat(hold): Hold 생성 요청 API 구현`
  - PR 제목: `{JiraKey} {설명}` 형식을 사용한다. 예: `RF-10 Hold 생성 요청 API 구현` (GitHub squash merge 시 `(#NN)` 자동 추가)
  - 개발 커밋에는 Jira Key를 붙이지 않고, PR 제목에만 Jira Key를 단다.
  - type 목록: `feat`, `fix`, `docs`, `ci`, `refactor`, `test`, `chore`, `build`, `style`
  - scope 목록(도메인 모듈): `auth`, `member`, `bookingprovider`, `bookingslot`, `hold`, `reservation`, `waitlist`, `outbox`, `llm`, `common`, `reservationrequest`
  - scope 목록(메타): `docs`, `ci`, `build`, `test`
  - PR 제목에는 Conventional prefix(`feat:` 등)를 붙이지 않는다.

## Jira 규칙

- Jira 프로젝트 key는 `RF`이다.
- 개발 작업은 Jira 티켓 단위로 진행한다.
- 기능/코드 작업(엔드포인트·엔티티·로직·리팩터링·버그 수정)은 대응하는 Jira 티켓이 없으면 **착수 전에 Jira 티켓을 먼저 생성한다**. 순수 문서·CI·도구·협업 도구 연동 같은 메타 작업은 로컬 T-ID로 관리할 수 있다.
- 신규 티켓 생성은 실행계획에 포함해 확인받은 뒤 해당 도메인 Epic 하위에 만들고, 생성된 Jira Key로 착수(`진행 중` 전환 + `feature/{JiraKey}-{short-summary}` 브랜치)한다.
- 티켓 타입 컨벤션: 사용자/시스템이 직접 쓰는 기능 단위(공개 API·화면)는 `스토리(Story)`, 기술·인프라·워커·테스트·설정 작업은 `작업(Task)`으로 만든다.
- 작업 시작 시 Jira 티켓을 확인한다.
- Jira 티켓 작업을 시작해 작업 브랜치를 생성하거나 구현에 착수하면 Jira 상태를 `진행 중`으로 기본 전환한다.
- GitHub PR을 생성하면 Jira 티켓에 PR 링크와 검증 결과를 댓글로 남기고 상태를 `검토 중`으로 변경한다.
- PR이 `main`에 merge되면 Jira 자동화(GitHub for Jira 연동)가 해당 이슈를 `완료`로 자동 전환한다. 
- Jira 댓글에는 작업 요약, 검증 결과, PR 링크를 남긴다.

## GitHub PR 규칙

- PR 대상 브랜치는 GitHub Flow 기준을 따른다.
- 기능 작업 PR은 작업 브랜치에서 `main` 또는 사용자와 합의한 기본 브랜치로 보낸다.
- PR 제목과 본문에는 Jira Key, 변경 요약, 검증 결과를 포함한다.
- PR 본문에는 후속 작업 또는 남은 위험이 있으면 함께 남긴다.
- GitHub push와 PR 생성은 사용자가 요청하거나 실행계획에 포함되어 확인된 경우에만 수행한다.
- PR merge는 사용자가 GitHub UI에서 직접 수행하는 것을 기본 원칙으로 한다.
- 에이전트는 사용자가 명시적으로 요청한 예외 상황이 아니라면 GitHub API나 CLI로 PR을 직접 merge하지 않는다.

## Slack 공유 규칙

- Slack 알림은 GitHub·Jira 공식 Slack 앱이 담당한다. 직접 Slack 메시지를 전송하지 않는다.

## 테스트 규칙

- 구현 변경이 있으면 관련 테스트 또는 검증 방법을 함께 수행한다.
- Java/Spring 테스트는 JUnit 5와 Spring Boot Test를 기준으로 한다.
- DB 제약, Migration, 동시성은 통합 테스트로 검증한다.

## 배포 규칙

- 현재 프로젝트는 초기 로컬 개발 기준이다.
- 배포, 운영 환경 변경, 환경변수 변경, DB migration 적용은 실행계획에 포함하고 사용자 확인을 받은 경우에만 수행한다.
- 적용된 Flyway Migration은 수정하지 않는다. 변경은 새 Migration으로 추가한다.

## 코딩 스타일

- Notion PRD/API/ERD의 용어를 코드와 문서에 일관되게 사용한다.
- 현재 기준 용어는 `booking_provider`, `booking_slot`, `hold_request`, `hold`, `reservation`, `waitlist`이다.
- Java 21과 Spring Boot 4.1.0을 기준으로 한다.
- Controller, Service, Repository 책임을 분리한다.
- API DTO와 Entity를 직접 공유하지 않는다.
- 상태값은 Java enum으로 표현하되 DB CHECK 값과 반드시 일치시킨다.

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
- [ ] 관련 커밋을 남기고 PR 생성 여부를 사용자와 확인했다.

## 개발 우선순위

MVP 도메인 구현 순서는 `booking provider/booking slot -> Hold -> Reservation -> Waitlist`를 따른다. 구체적인 티켓과 진행 상태는 Jira P0 백로그를 기준으로 한다.

## 금지 사항

- PRD/API/ERD 기준과 다른 용어를 임의로 도입하지 않는다.
- `resource`, `slot`처럼 이전 용어를 새 코드에 도입하지 않는다. 현재 기준은 `booking_provider`, `booking_slot`이다.
- 민감정보, JWT, 개인정보, 원문 프롬프트를 로그나 audit metadata에 저장하지 않는다.
- 적용된 Flyway Migration을 수정하지 않는다. 변경은 새 Migration으로 추가한다.
- 외부 API, LLM, Kafka 호출을 DB 트랜잭션 안에서 수행하지 않는다.
