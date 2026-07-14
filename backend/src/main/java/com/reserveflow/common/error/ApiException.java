package com.reserveflow.common.error;

import java.util.List;

/**
 * 도메인과 애플리케이션 계층에서 Error Catalog 코드로 실패를 표현하는 예외.
 */
public class ApiException extends RuntimeException {

	private final ErrorCode errorCode;
	private final List<ErrorResponse.ErrorDetail> details;

	/**
	 * ErrorCode의 기본 메시지와 빈 상세 사유로 예외를 생성한다.
	 */
	public ApiException(ErrorCode errorCode) {
		this(errorCode, errorCode.message(), List.of());
	}

	/**
	 * ErrorCode와 별도 메시지로 예외를 생성한다.
	 */
	public ApiException(ErrorCode errorCode, String message) {
		this(errorCode, message, List.of());
	}

	/**
	 * ErrorCode, 메시지, 상세 사유를 모두 지정해 예외를 생성한다.
	 */
	public ApiException(ErrorCode errorCode, String message, List<ErrorResponse.ErrorDetail> details) {
		super(message);
		this.errorCode = errorCode;
		this.details = List.copyOf(details);
	}

	/**
	 * 공통 오류 응답에 사용할 Error Catalog 코드를 반환한다.
	 */
	public ErrorCode errorCode() {
		return errorCode;
	}

	/**
	 * 필드별 오류 등 클라이언트에 전달할 상세 사유 목록을 반환한다.
	 */
	public List<ErrorResponse.ErrorDetail> details() {
		return details;
	}
}
