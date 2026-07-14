package com.reserveflow.common.error;

import org.springframework.http.HttpStatus;

/**
 * API 명세서의 Error Catalog 코드를 애플리케이션 코드로 표현한다.
 */
public enum ErrorCode {

	VALIDATION_REQUIRED("VALIDATION_004", HttpStatus.BAD_REQUEST, "필수 요청 값이 누락되었습니다."),
	AUTH_REQUIRED("AUTH_001", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	AUTH_INVALID("AUTH_002", HttpStatus.UNAUTHORIZED, "인증 정보가 만료되었거나 유효하지 않습니다."),
	AUTH_FORBIDDEN("AUTH_003", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	AUTH_DUPLICATE_SUBJECT("AUTH_004", HttpStatus.CONFLICT, "이미 가입된 인증 식별자입니다."),
	PROVIDER_NOT_FOUND("PROVIDER_001", HttpStatus.NOT_FOUND, "예약 제공자를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR("INTERNAL_001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final String code;
	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(String code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}

	/**
	 * 클라이언트 응답에 포함할 Error Catalog 코드 값을 반환한다.
	 */
	public String code() {
		return code;
	}

	/**
	 * 오류 코드에 대응하는 HTTP 상태 코드를 반환한다.
	 */
	public HttpStatus httpStatus() {
		return httpStatus;
	}

	/**
	 * 오류 코드의 기본 사용자 메시지를 반환한다.
	 */
	public String message() {
		return message;
	}
}
