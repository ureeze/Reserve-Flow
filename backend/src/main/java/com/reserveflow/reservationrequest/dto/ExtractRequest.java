package com.reserveflow.reservationrequest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 사용자가 입력한 자연어 예약 요청을 전달한다.
 */
public record ExtractRequest(
        /** 예약 조건을 포함한 자연어 문장이다. */
        @NotBlank(message = "reservationMessage는 비어 있을 수 없습니다.")
        String reservationMessage
) {
}
