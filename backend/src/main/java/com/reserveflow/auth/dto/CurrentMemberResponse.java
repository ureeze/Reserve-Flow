package com.reserveflow.auth.dto;

import java.util.UUID;

/**
 * 현재 인증된 회원 조회 응답.
 *
 * Bearer token으로 식별한 회원의 기본 정보를 클라이언트에 전달한다.
 */
public record CurrentMemberResponse(
		/**
		 * 클라이언트와 JWT에 노출하는 회원 public ID.
		 */
		UUID memberId,

		/**
		 * 로그인과 JWT subject로 사용하는 사용자 인증 식별자.
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
