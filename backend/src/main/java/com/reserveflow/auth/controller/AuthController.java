package com.reserveflow.auth.controller;

import com.reserveflow.auth.dto.CurrentMemberResponse;
import com.reserveflow.auth.dto.LoginRequest;
import com.reserveflow.auth.dto.RefreshTokenRequest;
import com.reserveflow.auth.dto.SignupRequest;
import com.reserveflow.auth.dto.SignupResponse;
import com.reserveflow.auth.dto.TokenResponse;
import com.reserveflow.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 API 요청을 받는 Controller.
 *
 * 회원가입, 로그인, 토큰 재발급, 현재 회원 조회 요청을 받고
 * 실제 인증 처리는 AuthService에 위임한다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

	private final AuthService authService;

	/**
	 * 회원가입 요청을 처리한다.
	 *
	 * 요청 본문을 검증한 뒤 회원 생성을 AuthService에 위임한다.
	 */
	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	/**
	 * 로그인 요청을 처리한다.
	 *
	 * 인증 식별자와 비밀번호 검증 및 토큰 발급을 AuthService에 위임한다.
	 */
	@PostMapping("/login")
	public TokenResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	/**
	 * refresh token 기반 토큰 재발급 요청을 처리한다.
	 *
	 * refresh token 검증과 새 토큰 쌍 발급을 AuthService에 위임한다.
	 */
	@PostMapping("/token/refresh")
	public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return authService.refresh(request);
	}

	/**
	 * 현재 인증된 회원 정보를 조회한다.
	 *
	 * Bearer access token에서 추출한 subject를 기준으로 회원 정보를 반환한다.
	 */
	@GetMapping("/me")
	public CurrentMemberResponse me(@AuthenticationPrincipal Jwt jwt) {
		return authService.currentMember(jwt.getSubject());
	}
}
