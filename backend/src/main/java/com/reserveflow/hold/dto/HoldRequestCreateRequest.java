package com.reserveflow.hold.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 비동기 Hold 생성을 접수하기 위한 요청.
 *
 * 예약 희망 booking slot과 인원수를 입력받는다.
 * 요청한 회원은 JWT에서 식별하므로 요청 본문에는 담지 않는다.
 */
public record HoldRequestCreateRequest(
        @NotNull(message = "bookingSlotId는 비어 있을 수 없습니다.")
        UUID bookingSlotId,

        @NotNull(message = "partySize는 비어 있을 수 없습니다.")
        @Min(value = 1, message = "partySize는 1 이상이어야 합니다.")
        Integer partySize
) {
}
