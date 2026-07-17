# RF-6 LLM Interpretation Spike

## 목적

자연어 예약 요청을 LLM으로 구조화된 예약 조건으로 변환하기 위한 서비스 경계와 응답 계약을 확정한다. 이 문서는 구현 코드가 아니라 RF-6 구현 PR의 기준이다.

## 선택한 구조

```text
Client
  -> Spring Boot: POST /api/v1/reservation-requests/interpret
  -> Spring Boot: 입력 검증, 공개 접근, Redis rate limit, 공통 오류 응답
  -> Python FastAPI: 내부 HTTP 호출
  -> LangChain + langchain-openai: structured output 생성
  -> Pydantic: 응답 스키마 검증
  -> Spring Boot: API 명세 응답 검증 후 Client 반환
```

- LLM 호출은 Spring Boot DB 트랜잭션 밖에서만 수행한다.
- Spring Boot는 LLM API key를 갖지 않으며, Python 서비스가 `OPENAI_API_KEY`를 환경변수로 사용한다.
- Python 서비스는 외부 공개 API가 아니라 로컬 개발 Docker 네트워크 또는 내부 네트워크에서만 Spring Boot가 호출한다.

## 외부 API 계약

Spring Boot는 Notion API 명세서의 외부 계약을 유지한다.

```http
POST /api/v1/reservation-requests/interpret
Content-Type: application/json

{
  "reservationMessage": "이번 주 금요일 저녁 7시에 강남에서 4명 예약하고 싶어요."
}
```

응답 필드:

- `reservationDate`: `YYYY-MM-DD`, 없으면 `null`
- `reservationTime`: `HH:mm`, 없으면 `null`
- `partySize`: 인원, 없으면 `null`
- `location`: 지역 또는 주소 검색어, 없으면 `null`
- `providerType`: `RESTAURANT`, `CINEMA`, `HOSPITAL`, `SALON`, `CLASS`, `ROOM` 중 하나
- `confidence`: 0부터 1 사이의 해석 신뢰도
- `missingFields`: 확인이 필요한 필드 이름 목록

`providerType`을 추론하지 못하면 불완전 응답으로 반환하지 않고 `400` 공통 오류 응답으로 처리한다. 그 외 누락 필드는 `missingFields`에 담아 `200 OK`로 반환할 수 있다.

## 내부 LLM 서비스 계약

Python 서비스는 Spring Boot가 제공하는 기준 날짜와 시간대를 입력받는다. 상대 날짜는 모델 자체의 현재 시각이 아니라 이 기준값으로 해석한다.

```http
POST /v1/interpreters/reservation
Content-Type: application/json

{
  "reservationMessage": "이번 주 금요일 저녁 7시에 강남에서 4명 예약하고 싶어요.",
  "referenceDate": "2026-07-17",
  "timezone": "Asia/Seoul"
}
```

- `referenceDate`와 `timezone`은 Spring Boot 서버 시계에서 생성한다.
- Python 응답은 외부 API 응답과 같은 예약 조건 필드를 사용하되, HTTP 오류와 모델 오류는 Spring Boot가 외부 Error Catalog 코드로 변환한다.
- LLM 원문 요청과 응답은 개발 편의용이라도 로그에 남기지 않는다.

## 오류와 제한

- 입력 누락 또는 결과 형식 오류: `400 PARSE_004`
- `providerType` 추론 실패: `400 PARSE_004`
- Redis rate limit 초과: `429 RATE_LIMIT_001`
- Python 서비스 또는 모델 제공자 호출 실패: `502 LLM_001`
- 자연어 해석 요청은 사용자당 분당 10회를 기본 한도로 한다. 공개 API에서는 인증 사용자는 회원 식별자, 비인증 사용자는 IP 기반 보조 키를 사용한다.

## 후속 구현 범위

1. `llm-service/`에 FastAPI, Pydantic 모델, LangChain structured output을 추가한다.
2. `backend/`에 `reservationrequest` controller, service, DTO, Python HTTP client를 추가한다.
3. Spring Security에서 해석 API를 `permitAll`로 허용한다.
4. Redis rate limit과 Error Catalog 코드를 구현한다.
5. 단위 테스트, Spring Boot 통합 테스트, FastAPI 계약 테스트, Docker Compose 기반 로컬 연동 검증을 수행한다.
