from datetime import date, time

import pytest

from app.schemas import ReservationInterpretationRequest, ReservationInterpretationResponse
from app.service import ReservationInterpreter


class FakeStructuredModel:
    """OpenAI 호출 없이 LangChain structured output 경계를 검증하는 테스트 대역이다."""

    async def ainvoke(self, messages):
        return ReservationInterpretationResponse(
            reservationDate=date(2026, 7, 17),
            reservationTime=time(19, 0),
            partySize=4,
            location="강남",
            providerType="RESTAURANT",
            confidence=0.91,
            missingFields=[],
        )


@pytest.mark.asyncio
async def test_interpreter_returns_structured_reservation_condition():
    """구조화된 LLM 결과를 Spring Boot 계약과 같은 필드명으로 반환하는지 검증한다."""
    interpreter = ReservationInterpreter(structured_model=FakeStructuredModel())

    result = await interpreter.interpret(
        ReservationInterpretationRequest(
            reservationMessage="오늘 저녁 7시에 강남에서 4명 예약하고 싶어요.",
            referenceDate=date(2026, 7, 17),
            timezone="Asia/Seoul",
        )
    )

    assert result.model_dump(mode="json", by_alias=True) == {
        "reservationDate": "2026-07-17",
        "reservationTime": "19:00",
        "partySize": 4,
        "location": "강남",
        "providerType": "RESTAURANT",
        "confidence": 0.91,
        "missingFields": [],
    }
