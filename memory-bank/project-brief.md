# Project Brief

## 프로젝트명

ReserveFlow MVP

제품과 문서상의 이름은 `ReserveFlow`를 사용한다. 로컬 저장소 또는 작업 폴더명은 Git 초기화 시 확정한다.

## 목적

ReserveFlow는 사용자가 복잡한 예약 입력 폼을 직접 채우지 않아도 자연어로 예약을 시작할 수 있게 하고, 인기 시간대의 동시 예약 요청에서도 초과 예약 없이 안정적으로 예약을 처리하는 범용 예약 엔진 플랫폼이다.

## 대상 사용자

- 일반 예약 사용자
- 사용자는 자연어로 예약 조건을 입력하고, 가능한 예약 제공자와 booking slot을 선택해 예약을 완료한다.

## MVP 대상 도메인

특정 업종 하나에 고정하지 않고 다음 예약 제공자 유형을 공통 예약 엔진으로 처리한다.

- `RESTAURANT`: 식당 예약
- `CINEMA`: 영화관 또는 상영관 예약
- `HOSPITAL`: 병원·진료 예약
- `SALON`: 미용실·샵 예약
- `CLASS`: 클래스·강의 예약
- `ROOM`: 스터디룸·회의실·공간 예약

초기 데모와 QA 시나리오는 이해하기 쉬운 `RESTAURANT` 중심으로 구성할 수 있다.

## 핵심 기능

- 회원가입
- 로그인
- JWT 토큰 발급과 갱신
- 자연어 예약 요청 입력
- 예약 조건 해석과 검증
- 예약 제공자 검색
- 예약 가능 booking slot 조회
- 대체 booking slot 제공
- 비동기 Hold 생성 요청
- HoldRequest 상태 조회
- Hold 만료 처리
- 예약 최종 확인
- 예약 조회와 취소
- 대기열 등록, 승격, Promotion Hold
- 상태 변경 이력과 감사 로그

## MVP 제외 범위

- 결제 또는 보증금
- OAuth 로그인
- 관리자 대시보드
- 모바일 앱
- 다국어 지원
- SMS, 이메일, 카카오톡, 푸시 알림
- 지도 연동
- 추천 시스템
- 멀티 테넌트 관리자 기능
- 운영자용 감사 로그 조회 화면

## 성공 기준

- 초과 예약 0건
- 중복 Hold, 중복 Reservation, 중복 승격 0건
- HoldRequest 접수 후 1초 이내 PENDING 반환
- HoldRequest 정상 부하에서 10초 이내 종결 상태 도달
- 예약 최종 확인 정상 부하에서 2초 이하
- booking slot 조회 정상 부하에서 1초 이하

## Memory Bank 구조

Memory Bank는 프로젝트의 현재 상태와 기준 지식을 보관한다. `coding-rules.md`는 AI 행동 규칙이 아니라 이 프로젝트의 코드 작성 기준을 담는 지식 문서로 본다.

```text
memory-bank/
├─ project-brief.md
├─ architecture.md
├─ tech-stack.md
├─ coding-rules.md
├─ current-state.md
├─ decisions.md
├─ tasks.md
├─ tasks-archive.md
└─ troubleshooting.md
```

## Memory Bank 문서 역할

- `project-brief.md`: 제품 목적, 범위, 성공 기준, Memory Bank 구조
- `architecture.md`: 아키텍처, 도메인 흐름, 동시성 원칙
- `tech-stack.md`: 기술 스택
- `coding-rules.md`: 프로젝트 코드 작성 기준
- `current-state.md`: 현재 마일스톤, 열린 이슈, 다음 작업
- `decisions.md`: Notion ADR 링크 인덱스
- `tasks.md`: 가까운 작업 목록과 완료 조건
- `tasks-archive.md`: 오래된 완료 작업 보관
- `troubleshooting.md`: 문제 해결 기록
