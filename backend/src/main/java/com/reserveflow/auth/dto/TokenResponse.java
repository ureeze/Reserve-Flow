package com.reserveflow.auth.dto;

import java.util.UUID;

/**
 * 인증 성공 시 반환하는 토큰 응답.
 *
 * Bearer token 타입, access token, refresh token, 만료 시간, 회원 ID를 전달한다.
 */
public record TokenResponse(
		/**
		 * Authorization 헤더에서 사용할 토큰 타입. 현재는 Bearer이다.
		 */
		String tokenType,

		/**
		 * 보호 API 호출에 사용할 JWT access token.
		 */
		String accessToken,

		/**
		 * access token 만료 후 새 토큰을 발급받기 위한 JWT refresh token.
		 */
		String refreshToken,

		/**
		 * access token 만료까지 남은 시간. 단위는 초이다.
		 */
		long expiresIn,

		/**
		 * 토큰을 발급한 회원의 public ID.
		 */
		UUID memberId
) {
}
