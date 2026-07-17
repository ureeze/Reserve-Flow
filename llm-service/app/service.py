import os

from langchain_core.messages import HumanMessage, SystemMessage
from langchain_openai import ChatOpenAI

from app.schemas import ReservationInterpretationRequest, ReservationInterpretationResponse


class LlmInvocationError(RuntimeError):
    """OpenAI 또는 LangChain 호출을 외부 API 오류로 변환하기 위한 예외다."""


class ReservationInterpreter:
    """LangChain structured output으로 자연어 예약 요청을 해석한다."""

    def __init__(self, structured_model=None) -> None:
        self._structured_model = structured_model or self._create_structured_model()

    def _create_structured_model(self):
        if not os.getenv("OPENAI_API_KEY"):
            raise LlmInvocationError("OPENAI_API_KEY is not configured")

        model_name = os.getenv("OPENAI_MODEL", "gpt-4.1-mini")
        chat_model = ChatOpenAI(model=model_name, temperature=0)
        return chat_model.with_structured_output(
            ReservationInterpretationResponse,
            method="json_schema",
        )

    async def interpret(
        self,
        request: ReservationInterpretationRequest,
    ) -> ReservationInterpretationResponse:
        """기준 날짜와 시간대를 사용해 LLM의 구조화된 예약 조건을 반환한다."""
        prompt = (
            "사용자 자연어를 ReserveFlow 예약 조건으로 변환하세요. "
            f"상대 날짜는 기준 날짜 {request.reference_date.isoformat()}와 "
            f"시간대 {request.timezone}를 사용해 계산하세요. "
            "providerType은 RESTAURANT, CINEMA, HOSPITAL, SALON, CLASS, ROOM 중 하나만 사용하세요. "
            "업종을 추론할 수 없으면 providerType을 null로 설정하세요. "
            "알 수 없는 필드는 null로 두고 missingFields에 camelCase 필드명을 추가하세요. "
            "추정한 정보는 confidence에 0부터 1 사이 값으로 표현하세요.\n\n"
            f"사용자 요청: {request.reservation_message}"
        )

        try:
            result = await self._structured_model.ainvoke(
                [
                    SystemMessage(content="You extract reservation conditions and return only the requested schema."),
                    HumanMessage(content=prompt),
                ]
            )
        except Exception as exception:  # LangChain provider errors have multiple concrete types.
            raise LlmInvocationError("LLM interpretation failed") from exception

        if not isinstance(result, ReservationInterpretationResponse):
            raise LlmInvocationError("LLM returned an unexpected structured response")
        return result
