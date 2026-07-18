from fastapi import FastAPI, HTTPException, status

from app.schemas import ReservationExtractionRequest, ReservationExtractionResponse
from app.service import LlmInvocationError, ReservationExtractor

app = FastAPI(title="ReserveFlow LLM Service", version="0.1.0")


def get_extractor() -> ReservationExtractor:
    """첫 요청 시에만 모델 클라이언트를 만들고 API 키 누락을 외부 오류로 처리한다."""
    extractor = getattr(app.state, "reservation_extractor", None)
    if extractor is None:
        extractor = ReservationExtractor()
        app.state.reservation_extractor = extractor
    return extractor


@app.post(
    "/v1/extractors/reservation",
    response_model=ReservationExtractionResponse,
    response_model_by_alias=True,
)
async def extract_reservation(
    request: ReservationExtractionRequest,
) -> ReservationExtractionResponse:
    """Spring Boot 내부 요청을 받아 LangChain으로 예약 조건을 해석한다."""
    try:
        return await get_extractor().extract(request)
    except LlmInvocationError as exception:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="LLM extraction service is unavailable",
        ) from exception
