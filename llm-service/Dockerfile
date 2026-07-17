FROM python:3.12-slim

WORKDIR /app

COPY pyproject.toml ./
COPY app ./app

RUN pip install --no-cache-dir .

EXPOSE 8000

CMD ["sh", "-c", "uvicorn app.main:app --host ${LLM_SERVICE_HOST:-0.0.0.0} --port ${LLM_SERVICE_PORT:-8000}"]
