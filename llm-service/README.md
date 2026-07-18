# ReserveFlow LLM Service

Python FastAPI와 LangChain으로 자연어 예약 요청을 구조화된 예약 조건으로 변환하는 내부 서비스다.

## Required Environment Variables

```powershell
$env:OPENAI_API_KEY = "..."
$env:OPENAI_MODEL = "gpt-4.1-mini"
```

- `OPENAI_API_KEY`는 필수이며 저장소, 로그, Docker 이미지에 포함하지 않는다.
- `OPENAI_MODEL`은 선택값이며 기본값은 `gpt-4.1-mini`다.

## Local Run

```powershell
docker compose up -d llm-service
```

내부 API는 `POST http://localhost:8000/v1/extractors/reservation`이다. Spring Boot는 `LLM_SERVICE_BASE_URL` 환경변수로 이 서비스 주소를 변경할 수 있다.

## Test

로컬 Python 설치 없이 Docker로 테스트할 수 있다.

```powershell
docker compose run --rm --no-deps -v "${PWD}\llm-service:/workspace" -w /workspace llm-service sh -c "pip install '.[test]' && pytest -q"
```
