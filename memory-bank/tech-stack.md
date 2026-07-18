# Tech Stack

## Backend

- 위치: `backend/`
- Java 21
- Spring Boot 4.1.0
- Spring Web
- Spring Validation
- Spring Security
  - RF-18에서는 포함하지 않는다.
  - 의존성 추가, JWT 인증, 보안 설정 구현은 RF-21에서 수행한다.
- Spring Data JPA
  - 의존성 추가와 PostgreSQL 연결은 RF-19에서 수행한다.
- Flyway
  - 의존성 추가와 migration 설정은 RF-19에서 수행한다.

## Database

- PostgreSQL 18.4 기준
- `members`는 내부 PK `Long id`와 외부/JWT 식별자 `UUID publicId`를 분리한다.
- 예약 도메인의 외부 식별자는 UUID 기반으로 설계하며, UUIDv7 생성 방식은 실제 구현 시 결정한다.
- 운영 환경 Hibernate `ddl-auto`는 `validate`

## Cache / Queue / Async

- Redis
- Kafka
- Transactional Outbox
- Worker 기반 비동기 처리

## LLM Extraction

- 위치(예정): `llm-service/`
- Python 3.12+
- FastAPI: Spring Boot가 호출하는 내부 HTTP API 제공
- LangChain + `langchain-openai`: 모델 호출과 structured output 처리
- Pydantic: LLM 응답 스키마 검증
- Spring Boot는 공개 해석 API, 입력 검증, Redis rate limit, 공통 오류 응답을 담당한다.
- `OPENAI_API_KEY` 등 모델 제공자 비밀값은 환경변수로만 주입하며 저장소, 로그, Outbox payload에 남기지 않는다.

## API

- REST API
- Base path: `/api/v1`
- OpenAPI 문서화는 코드 우선 방식을 기준으로 한다.
- `springdoc-openapi` 도입은 실제 API 구현 또는 공통 오류 응답 정리 시점에 결정한다.
- 공통 오류 응답:

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "사용자용 메시지",
    "details": [],
    "requestId": "uuid",
    "timestamp": "2026-06-30T00:00:00Z"
  }
}
```

## Auth

- Bearer Token 기반 JWT
- 회원가입, 로그인, 토큰 발급/갱신 API
- 예약 관련 MVP API의 JWT 인증 필터와 사용자 식별
- 자연어 예약 요청 해석 API는 MVP 기준 공개 API(`permitAll`)로 둔다.
- JWT subject는 `members.auth_subject`와 연결
- 클라이언트가 전달한 `userId`는 신뢰하지 않음

## Frontend

- 아직 확정 전
- 후보: HTML/CSS/JS 또는 React
- MVP 백엔드 우선 개발 후 화면 구현 방식 결정

## Test

- JUnit 5
- Spring Boot Test
- Testcontainers PostgreSQL 권장
- Redis/Kafka는 통합 테스트 단계에서 Testcontainers 또는 로컬 docker compose 검토

## DevOps

- 초기 로컬 개발 기준
- PostgreSQL 자동 백업/PITR은 운영 전 검토
- MVP 개발·베타 기준 RPO 24시간, RTO 4시간
