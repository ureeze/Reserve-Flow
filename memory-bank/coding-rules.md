# Coding Rules

## 기본 원칙

- Notion PRD/API/ERD의 용어를 코드와 문서에 일관되게 사용한다.
- 현재 기준 용어는 `booking_provider`, `booking_slot`, `hold_request`, `hold`, `reservation`, `waitlist`이다.
- 이전 용어인 `restaurant`, `resource`, `slot`을 범용 도메인 이름으로 새로 도입하지 않는다.
- MVP에서는 과도한 추상화보다 명확한 도메인 경계와 테스트 가능한 구조를 우선한다.

## Java / Spring

- Java 21과 Spring Boot 3.x를 기준으로 한다.
- Controller, Service, Repository 책임을 분리한다.
- API DTO와 Entity를 직접 공유하지 않는다.
- 상태값은 Java enum으로 표현하되 DB CHECK 값과 반드시 일치시킨다.
- 외부 입력은 Bean Validation과 도메인 검증을 함께 사용한다.
- 사용자 소유권은 서버에서 검증한다.

## Database

- Flyway Migration은 `src/main/resources/db/migration`에 둔다.
- 파일명은 `V{version}__{description}.sql` 형식을 사용한다.
- 적용된 Migration은 수정하지 않고, 변경은 새 Migration으로 추가한다.
- 운영 환경에서 Hibernate `ddl-auto`는 `validate`를 사용한다.
- 모든 FK 컬럼은 단일 또는 복합 인덱스의 선두 컬럼에 포함되도록 점검한다.
- `available_capacity`는 DB CHECK와 조건부 UPDATE로 음수 방지한다.

## Transaction

- 트랜잭션은 DB 상태 검증과 변경에 필요한 최소 범위로 유지한다.
- LLM, Kafka, 외부 HTTP 호출은 트랜잭션 밖에서 수행한다.
- 예약 최종 확인은 Hold를 잠그고 `ACTIVE`, `expires_at > now()`를 검증한다.
- 예약 생성과 Hold `CONFIRMED` 전이는 하나의 트랜잭션으로 처리한다.
- Hold 만료와 예약 최종 확인 경합에서 한 전이만 성공해야 한다.

## API

- Base path는 `/api/v1`이다.
- 로그인, 회원가입, 토큰 발급/갱신 같은 인증 발급 API를 MVP 범위에 포함한다.
- 예약 관련 MVP API는 Bearer Token 기반 JWT 인증 기준을 따른다.
- 변경 API 중 예약 최종 확인과 예약 취소에는 `Idempotency-Key`를 필수로 둔다.
- 동일 key와 동일 request hash는 기존 응답을 반환한다.
- 동일 key와 다른 request hash는 `409 IDEMPOTENCY_002`를 반환한다.
- 오류 응답은 API 명세서의 공통 형식을 따른다.

## Logging / Security

- 개인정보, JWT, 비밀번호, 인증 헤더, 원문 프롬프트를 로그에 남기지 않는다.
- 오류 응답에 stack trace, SQL, 내부 topic, 인프라 정보를 노출하지 않는다.
- audit log metadata에는 비민감 정보만 저장한다.

## Test

- 핵심 도메인 규칙은 단위 테스트로 검증한다.
- DB 제약, Migration, 동시성은 통합 테스트로 검증한다.
- 반드시 테스트해야 하는 경합:
  - 동시에 Hold 생성 요청이 들어와도 capacity가 음수가 되지 않는다.
  - Hold 만료와 예약 최종 확인이 동시에 실행되어도 한 전이만 성공한다.
  - 같은 Idempotency-Key 재요청은 같은 결과를 반환한다.

## 작업 운영 규칙

Git, Jira, GitHub PR, Slack 공유 기준은 `AGENTS.md`를 따른다.
이 문서는 코드 작성, 테스트, 보안, 트랜잭션, API 구현 기준을 중심으로 유지한다.
