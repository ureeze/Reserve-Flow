package com.reserveflow.reservationrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 자연어에서 추출한 구조화된 예약 조건을 반환한다.
 */
public record InterpretReservationResponse(
        /** 희망 예약 날짜다. 아직 알 수 없으면 null이다. */
        LocalDate reservationDate,
        /** 희망 예약 시간이다. 아직 알 수 없으면 null이다. */
        @JsonFormat(pattern = "HH:mm")
        LocalTime reservationTime,
        /** 예약 인원이다. 아직 알 수 없으면 null이다. */
        Integer partySize,
        /** 지역 또는 주소 검색어다. 아직 알 수 없으면 null이다. */
        String location,
        /** 예약 제공자 유형이다. */
        BookingProviderType providerType,
        /** 0부터 1 사이의 LLM 해석 신뢰도다. */
        double confidence,
        /** 사용자에게 추가 입력을 요청해야 하는 필드 목록이다. */
        List<String> missingFields
) {
}
