from fastapi import FastAPI, HTTPException, status

from app.schemas import ReservationInterpretationRequest, ReservationInterpretationResponse
from app.service import LlmInvocationError, ReservationInterpreter

app = FastAPI(title="ReserveFlow LLM Service", version="0.1.0")


def get_interpreter() -> ReservationInterpreter:
    """첫 요청 시에만 모델 클라이언트를 만들고 API 키 누락을 외부 오류로 처리한다."""
    interpreter = getattr(app.state, "reservation_interpreter", None)
    if interpreter is None:
        interpreter = ReservationInterpreter()
        app.state.reservation_interpreter = interpreter
    return interpreter


@app.post(
    "/v1/interpreters/reservation",
    response_model=ReservationInterpretationResponse,
    response_model_by_alias=True,
)
async def interpret_reservation(
    request: ReservationInterpretationRequest,
) -> ReservationInterpretationResponse:
    """Spring Boot 내부 요청을 받아 LangChain으로 예약 조건을 해석한다."""
    try:
        return await get_interpreter().interpret(request)
    except LlmInvocationError as exception:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="LLM interpretation service is unavailable",
        ) from exception
