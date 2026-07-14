package com.reserveflow.common.error;

import java.time.Instant;
import java.util.List;

/**
 * API 명세서의 공통 오류 응답 형식.
 */
public record ErrorResponse(ErrorBody error) {

	/**
	 * 오류 코드, 메시지, 상세 사유, 추적 정보를 담는 실제 오류 본문.
	 */
	public record ErrorBody(
			String code,
			String message,
			List<ErrorDetail> details,
			String requestId,
			Instant timestamp
	) {
	}

	/**
	 * Validation 오류처럼 특정 필드에 대한 상세 실패 사유를 담는다.
	 */
	public record ErrorDetail(String field, String reason) {
	}
}
