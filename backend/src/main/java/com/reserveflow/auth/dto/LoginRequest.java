package com.reserveflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인 요청 본문.
 *
 * 인증 식별자와 비밀번호를 받아 토큰 발급에 사용한다.
 */
public record LoginRequest(
		/**
		 * 로그인에 사용하는 사용자 인증 식별자.
		 */
		@NotBlank
		@Size(max = 255)
		String authSubject,

		/**
		 * 사용자가 입력한 원문 비밀번호.
		 */
		@NotBlank
		@Size(min = 8, max = 100)
		String password
) {
}
