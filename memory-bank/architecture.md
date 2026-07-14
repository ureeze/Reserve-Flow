# Architecture

## 아키텍처 기준

- Backend: Java 21, Spring Boot 4.1.0
- 구조: Modular Monolith 우선
- 비동기 처리: Transactional Outbox + Kafka 이벤트 + Worker
- 최종 정합성 기준: PostgreSQL
- 캐시/제어 보조: Redis
- API: REST `/api/v1`

## 핵심 도메인 흐름

```text
자연어 예약 요청
-> 예약 조건 해석
-> 예약 조건 검증
-> 예약 제공자 검색
-> booking slot 조회
-> HoldRequest 생성
-> Outbox 이벤트 저장
-> Worker가 Hold 생성
-> HoldRequest 상태 polling
-> 예약 최종 확인
-> Reservation 생성
```

## 주요 모듈 후보

- `reservationrequest`: 자연어 예약 요청 해석과 검증
- `bookingprovider`: 예약 제공자 검색과 운영 정책
- `bookingslot`: booking slot 조회와 잔여 수용량 관리
- `hold`: HoldRequest, Hold 생성, Hold 만료
- `reservation`: 예약 최종 확인, 조회, 취소, 멱등성
- `waitlist`: 대기열 등록, 순번 조회, 승격, Promotion Hold
- `common`: 공통 오류 응답, request id, security, time, uuid
- `outbox`: Transactional Outbox 저장과 Relay
- `audit`: 상태 이력과 감사 로그

## 핵심 데이터 모델

- `users`
- `booking_providers`
- `booking_provider_business_hours`
- `booking_provider_closures`
- `booking_slots`
- `hold_requests`
- `holds`
- `reservations`
- `waitlists`
- `status_histories`
- `audit_logs`
- `outbox_events`
- `idempotency_keys`

## 동시성 원칙

- `booking_slots.available_capacity`는 실제 예약 가능한 잔여 수용량이다.
- Hold 생성 시점에 `available_capacity`를 차감한다.
- 예약 최종 확인 시에는 재고를 다시 차감하지 않는다.
- Hold 만료와 예약 취소 시 `available_capacity`를 복구한다.
- 예약 최종 확인과 Hold 만료가 경합하면 한 전이만 성공해야 한다.
- 조건부 UPDATE와 row lock을 사용해 음수 capacity와 중복 전이를 방지한다.

## 내부 비동기 처리

- `POST /hold-requests`는 즉시 `202 Accepted`와 `holdRequestId`를 반환한다.
- API 서버는 `hold_requests`와 `outbox_events`를 같은 트랜잭션에 저장한다.
- Outbox Relay가 이벤트를 발행한다.
- Hold 생성 Worker가 booking slot 재고를 조건부 차감하고 Hold를 생성한다.
- Hold 만료 Worker가 만료된 ACTIVE Hold를 EXPIRED로 전이하고 재고를 복구한다.
- Waitlist Promotion Worker가 재고 복구 이벤트를 기반으로 대기열을 승격한다.

## 설계 주의사항

- 외부 API, LLM, Kafka 호출은 DB 트랜잭션 밖에서 수행한다.
- 적용된 Flyway Migration은 수정하지 않는다.
- DB CHECK와 UNIQUE로 핵심 무결성을 방어한다.
- 상태 전이는 애플리케이션에서 명시적으로 검증한다.
- 개인정보와 비밀값은 audit log, JSONB metadata, 로그에 저장하지 않는다.
