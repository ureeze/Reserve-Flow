package com.reserveflow.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * 인증되지 않은 요청을 공통 오류 응답으로 변환한다.
 */
@RequiredArgsConstructor
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ErrorResponseFactory errorResponseFactory;
	private final ObjectMapper objectMapper;

	/**
	 * Spring Security 인증 실패 시 401 공통 오류 응답을 직접 작성한다.
	 */
	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	) throws IOException {
		ErrorCode errorCode = resolveErrorCode(authException);
		response.setStatus(errorCode.httpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		objectMapper.writeValue(response.getWriter(), errorResponseFactory.create(errorCode, request));
	}

	/**
	 * Bearer token 자체가 잘못된 경우와 인증 정보가 없는 경우를 구분한다.
	 */
	private ErrorCode resolveErrorCode(AuthenticationException authException) {
		if (authException instanceof InvalidBearerTokenException) {
			return ErrorCode.AUTH_INVALID;
		}
		return ErrorCode.AUTH_REQUIRED;
	}
}
