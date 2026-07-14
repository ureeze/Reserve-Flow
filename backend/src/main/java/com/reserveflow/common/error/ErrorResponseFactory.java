package com.reserveflow.common.error;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 요청 추적 ID와 시각을 포함한 공통 오류 응답을 생성한다.
 */
@Component
public class ErrorResponseFactory {

	private static final String REQUEST_ID_HEADER = "X-Request-Id";

	/**
	 * ErrorCode의 기본 메시지와 빈 상세 사유로 오류 응답을 생성한다.
	 */
	public ErrorResponse create(ErrorCode errorCode, HttpServletRequest request) {
		return create(errorCode, errorCode.message(), List.of(), request);
	}

	/**
	 * 지정한 메시지와 상세 사유를 사용해 오류 응답을 생성한다.
	 */
	public ErrorResponse create(
			ErrorCode errorCode,
			String message,
			List<ErrorResponse.ErrorDetail> details,
			HttpServletRequest request
	) {
		return new ErrorResponse(new ErrorResponse.ErrorBody(
				errorCode.code(),
				message,
				List.copyOf(details),
				resolveRequestId(request),
				Instant.now()
		));
	}

	/**
	 * 요청 헤더의 X-Request-Id를 우선 사용하고, 없으면 서버에서 새 추적 ID를 만든다.
	 */
	private String resolveRequestId(HttpServletRequest request) {
		String requestId = request.getHeader(REQUEST_ID_HEADER);
		if (StringUtils.hasText(requestId)) {
			return requestId;
		}
		return UUID.randomUUID().toString();
	}
}
