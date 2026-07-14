package com.reserveflow.auth.service;

import com.reserveflow.auth.dto.CurrentMemberResponse;
import com.reserveflow.auth.dto.LoginRequest;
import com.reserveflow.auth.dto.RefreshTokenRequest;
import com.reserveflow.auth.dto.SignupRequest;
import com.reserveflow.auth.dto.SignupResponse;
import com.reserveflow.auth.dto.TokenResponse;
import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.member.entity.Member;
import com.reserveflow.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 도메인의 주요 비즈니스 로직.
 *
 * 회원 생성, 비밀번호 검증, refresh token 검증,
 * access/refresh token 발급 흐름을 담당한다.
 */
@RequiredArgsConstructor
@Service
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtDecoder jwtDecoder;
	private final JwtTokenService jwtTokenService;

	/**
	 * 회원가입을 수행한다.
	 *
	 * 중복 가입 여부를 확인하고, 비밀번호를 BCrypt로 해시한 뒤 회원을 저장한다.
	 * 인증 토큰은 로그인 성공 시 별도로 발급한다.
	 */
	@Transactional
	public SignupResponse signup(SignupRequest request) {
		if (memberRepository.existsByAuthSubject(request.authSubject())) {
			throw new ApiException(ErrorCode.AUTH_DUPLICATE_SUBJECT);
		}

		Member member = new Member(
				request.authSubject(),
				passwordEncoder.encode(request.password()),
				request.displayName()
		);
		memberRepository.save(member);

		return new SignupResponse(member.getPublicId(), member.getAuthSubject(), member.getDisplayName(), member.getStatus());
	}

	/**
	 * 로그인을 수행한다.
	 *
	 * 활성 회원 조회 후 입력 비밀번호와 저장된 해시를 비교하고,
	 * 검증에 성공하면 새 토큰 쌍을 발급한다.
	 */
	@Transactional(readOnly = true)
	public TokenResponse login(LoginRequest request) {
		Member member = memberRepository.findByAuthSubjectAndStatus(request.authSubject(), "ACTIVE")
				.orElseThrow(() -> new ApiException(ErrorCode.AUTH_REQUIRED));

		if (!passwordEncoder.matches(request.password(), member.getPasswordHash())) {
			throw new ApiException(ErrorCode.AUTH_REQUIRED);
		}

		return jwtTokenService.issue(member);
	}

	/**
	 * refresh token으로 access/refresh token을 재발급한다.
	 *
	 * JWT 서명과 token_type을 확인하고, 활성 회원에게만 새 토큰 쌍을 발급한다.
	 */
	@Transactional(readOnly = true)
	public TokenResponse refresh(RefreshTokenRequest request) {
		Jwt jwt;
		try {
			jwt = jwtDecoder.decode(request.refreshToken());
		} catch (JwtException exception) {
			throw invalidToken();
		}

		if (!"refresh".equals(jwt.getClaimAsString("token_type"))) {
			throw invalidToken();
		}

		Member member = memberRepository.findByAuthSubjectAndStatus(jwt.getSubject(), "ACTIVE")
				.orElseThrow(this::invalidToken);

		return jwtTokenService.issue(member);
	}

	/**
	 * 현재 인증된 회원 정보를 조회한다.
	 *
	 * JWT subject와 일치하는 활성 회원을 찾아 API 응답 DTO로 변환한다.
	 */
	@Transactional(readOnly = true)
	public CurrentMemberResponse currentMember(String authSubject) {
		Member member = memberRepository.findByAuthSubjectAndStatus(authSubject, "ACTIVE")
				.orElseThrow(this::invalidToken);

		return new CurrentMemberResponse(member.getPublicId(), member.getAuthSubject(), member.getDisplayName(), member.getStatus());
	}

	/**
	 * 만료되었거나 유효하지 않은 인증 정보 응답을 생성한다.
	 */
	private ApiException invalidToken() {
		return new ApiException(ErrorCode.AUTH_INVALID);
	}
}
