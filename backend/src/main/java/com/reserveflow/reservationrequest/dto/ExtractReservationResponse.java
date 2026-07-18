package com.reserveflow.reservationrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 자연어에서 추출한 구조화된 예약 조건을 반환한다.
 */
public record ExtractReservationResponse(
        LocalDate reservationDate,
        @JsonFormat(pattern = "HH:mm")
        LocalTime reservationTime,
        Integer partySize,
        String location,
        BookingProviderType providerType,
        List<String> missingFields
) {
}
