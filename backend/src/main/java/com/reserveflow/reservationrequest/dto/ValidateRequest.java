package com.reserveflow.reservationrequest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * 구조화된 예약 조건이 예약 가능한 기본 조건을 만족하는지 검증하기 위한 요청.
 */
public record ValidateRequest(
        @NotNull(message = "bookingProviderId는 비어 있을 수 없습니다.")
        UUID bookingProviderId,

        @NotNull(message = "reservationDate는 비어 있을 수 없습니다.")
        LocalDate reservationDate,

        @NotNull(message = "reservationTime은 비어 있을 수 없습니다.")
        LocalTime reservationTime,

        @NotNull(message = "partySize는 비어 있을 수 없습니다.")
        @Min(value = 1, message = "partySize는 1 이상이어야 합니다.")
        Integer partySize
) {
}
