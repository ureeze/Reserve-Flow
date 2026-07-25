package com.reserveflow.reservationrequest.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 예약 조건 검증 결과를 반환한다.
 *
 * 유효하면 정규화된 조건을, 유효하지 않으면 위반 사유 목록을 반환한다.
 */
public record ValidateResponse(
        boolean valid,
        Normalized normalized,
        List<Violation> violations
) {

    /**
     * 검증을 통과한 예약 조건을 provider timezone 기준 UTC 시각으로 정규화한 결과.
     */
    public record Normalized(
            UUID bookingProviderId,
            Instant startsAt,
            Integer partySize,
            String timezone
    ) {
    }

    /**
     * 검증에 실패한 사유 하나를 나타낸다.
     */
    public record Violation(
            String code,
            String field,
            String message
    ) {
    }
}
