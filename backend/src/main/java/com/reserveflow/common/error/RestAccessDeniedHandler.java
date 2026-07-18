package com.reserveflow.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * 권한이 부족한 요청을 공통 오류 응답으로 변환한다.
 */
@RequiredArgsConstructor
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

	private final ErrorResponseFactory errorResponseFactory;
	private final ObjectMapper objectMapper;

	/**
	 * Spring Security 인가 실패 시 403 공통 오류 응답을 직접 작성한다.
	 */
	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException
	) throws IOException {
		ErrorCode errorCode = ErrorCode.AUTH_FORBIDDEN;
		response.setStatus(errorCode.httpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		objectMapper.writeValue(response.getWriter(), errorResponseFactory.create(errorCode, request));
	}
}
