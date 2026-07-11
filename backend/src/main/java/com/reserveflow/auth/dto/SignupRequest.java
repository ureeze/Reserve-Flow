package com.reserveflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 본문.
 *
 * 인증 식별자, 비밀번호, 표시 이름을 받아 사용자 생성에 사용한다.
 */
public record SignupRequest(
		/**
		 * 회원을 구분하는 인증 식별자. JWT subject로도 사용한다.
		 */
		@NotBlank
		@Size(max = 255)
		String authSubject,

		/**
		 * 회원가입 시 사용자가 입력한 원문 비밀번호.
		 */
		@NotBlank
		@Size(min = 8, max = 100)
		String password,

		/**
		 * 화면에 표시할 사용자 이름.
		 */
		@Size(max = 255)
		String displayName
) {
}
