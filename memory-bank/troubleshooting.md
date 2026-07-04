# Troubleshooting

문제 해결 기록을 남기는 문서다.

## 2026-06-30 - AGENTS.md와 memory-bank 한글 깨짐

### 상태

해결

### 문제

`AGENTS.md`와 `memory-bank` 문서 대부분이 한글 깨짐 상태였고, 일부 문서는 템플릿 placeholder만 포함하고 있었다.

### 원인

이전 파일이 잘못된 문자 인코딩으로 저장되었거나, CP949/EUC-KR 계열 텍스트가 UTF-8로 읽히며 깨진 것으로 보인다.

### 해결

Notion의 PRD, ERD/DB 설계서, API 명세서, Jira 작업 문서를 기준으로 `AGENTS.md`와 `memory-bank` 문서를 UTF-8 Markdown 내용으로 재작성했다.

### 재발 방지

- Markdown 문서는 UTF-8로 저장한다.
- 새 Codex 작업 시작 전 `AGENTS.md`와 `memory-bank/current-state.md`가 정상 표시되는지 확인한다.
- 깨진 문서를 부분 수정하지 말고, 신뢰 가능한 원본 문서 기준으로 재작성한다.
