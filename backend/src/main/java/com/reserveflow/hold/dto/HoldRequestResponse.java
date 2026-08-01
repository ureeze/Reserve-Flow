package com.reserveflow.hold.dto;

import com.reserveflow.hold.entity.HoldRequestStatus;
import java.util.UUID;

/**
 * Hold 생성 요청 접수 결과.
 *
 * 요청은 비동기로 처리되므로 즉시 PENDING 상태의 holdRequestId를 반환한다.
 */
public record HoldRequestResponse(
        UUID holdRequestId,
        HoldRequestStatus status
) {
}
