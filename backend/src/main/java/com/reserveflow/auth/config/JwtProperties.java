package com.reserveflow.auth.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 발급과 검증에 필요한 설정값.
 *
 * application.yml의 reserveflow.jwt 하위 값을 타입 안전하게 바인딩한다.
 */
@ConfigurationProperties(prefix = "reserveflow.jwt")
public record JwtProperties(
		/**
		 * JWT 발급자를 나타내는 값.
		 *
		 * access token과 refresh token의 issuer claim에 사용한다.
		 */
		String issuer,

		/**
		 * JWT 서명과 검증에 사용하는 비밀값.
		 *
		 * 운영 환경에서는 반드시 환경변수로 안전하게 주입해야 한다.
		 */
		String secret,

		/**
		 * access token의 유효 시간.
		 *
		 * 일반 API 인증에 사용하는 짧은 수명의 토큰 만료 시간을 정한다.
		 */
		Duration accessTokenTtl,

		/**
		 * refresh token의 유효 시간.
		 *
		 * access token 재발급에 사용하는 긴 수명의 토큰 만료 시간을 정한다.
		 */
		Duration refreshTokenTtl
) {
}
