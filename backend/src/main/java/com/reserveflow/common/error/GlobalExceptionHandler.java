package com.reserveflow.common.error;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.RequiredArgsConstructor;

/**
 * Controller 계층에서 발생한 예외를 API 공통 오류 응답으로 변환한다.
 */
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final ErrorResponseFactory errorResponseFactory;

	/**
	 * 애플리케이션에서 명시적으로 던진 ApiException을 ErrorCode 기준 응답으로 변환한다.
	 */
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
		ErrorCode errorCode = exception.errorCode();
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(errorResponseFactory.create(errorCode, exception.getMessage(), exception.details(), request));
	}

	/**
	 * Bean Validation 실패를 400 VALIDATION_004 응답으로 변환한다.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception,
			HttpServletRequest request
	) {
		List<ErrorResponse.ErrorDetail> details = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toErrorDetail)
				.toList();
		ErrorCode errorCode = ErrorCode.VALIDATION_REQUIRED;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(errorResponseFactory.create(errorCode, errorCode.message(), details, request));
	}

	/**
	 * JSON 본문 파싱 실패를 400 VALIDATION_004 응답으로 변환한다.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
			HttpMessageNotReadableException exception,
			HttpServletRequest request
	) {
		ErrorCode errorCode = ErrorCode.VALIDATION_REQUIRED;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(errorResponseFactory.create(errorCode, "요청 본문을 읽을 수 없습니다.", List.of(), request));
	}

	/**
	 * 예상하지 못한 예외를 민감정보 없는 500 공통 오류 응답으로 변환한다.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception, HttpServletRequest request) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(errorResponseFactory.create(errorCode, request));
	}

	/**
	 * Spring FieldError를 API 명세서의 details 항목으로 변환한다.
	 */
	private ErrorResponse.ErrorDetail toErrorDetail(FieldError fieldError) {
		return new ErrorResponse.ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
	}
}
