from datetime import date, time

from pydantic import BaseModel, ConfigDict, Field, field_serializer


class ReservationExtractionRequest(BaseModel):
    """Spring Boot가 전달하는 자연어 예약 해석 요청이다."""

    model_config = ConfigDict(populate_by_name=True)

    reservation_message: str = Field(min_length=1, alias="reservationMessage")
    reference_date: date = Field(alias="referenceDate")
    timezone: str


class ReservationExtractionResponse(BaseModel):
    """LLM이 추출한 예약 조건의 내부 HTTP 응답 계약이다."""

    model_config = ConfigDict(populate_by_name=True)

    reservation_date: date | None = Field(default=None, alias="reservationDate")
    reservation_time: time | None = Field(default=None, alias="reservationTime")
    party_size: int | None = Field(default=None, ge=1, alias="partySize")
    location: str | None = None
    provider_type: str | None = Field(default=None, alias="providerType")
    missing_fields: list[str] = Field(default_factory=list, alias="missingFields")

    @field_serializer("reservation_time")
    def serialize_reservation_time(self, value: time | None) -> str | None:
        """외부 API 명세에 맞게 시간대 정보 없이 HH:mm 형식으로 반환한다."""
        return value.strftime("%H:%M") if value else None
