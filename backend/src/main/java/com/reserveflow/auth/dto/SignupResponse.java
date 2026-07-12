package com.reserveflow.auth.dto;

import java.util.UUID;

/**
 * 회원가입 성공 응답.
 *
 * 회원 생성 결과를 반환하며, 인증 토큰은 로그인 API에서 별도로 발급한다.
 */
public record SignupResponse(
		/**
		 * 클라이언트와 JWT에 노출하는 회원 public ID.
		 */
		UUID memberId,

		/**
		 * 로그인과 JWT subject로 사용하는 회원 인증 식별자.
		 */
		String authSubject,

		/**
		 * 화면에 표시할 회원 이름.
		 */
		String displayName,

		/**
		 * 회원 계정 상태. 현재 기본값은 ACTIVE이다.
		 */
		String status
) {
}
