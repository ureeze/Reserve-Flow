package com.reserveflow.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 재발급 요청 본문.
 *
 * 기존 refresh token을 전달해 새 access/refresh token 발급을 요청한다.
 */
public record RefreshTokenRequest(
		/**
		 * access token 재발급에 사용할 refresh token.
		 */
		@NotBlank
		String refreshToken
) {
}
