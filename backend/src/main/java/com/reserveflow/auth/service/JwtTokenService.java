package com.reserveflow.auth.service;

import com.reserveflow.auth.config.JwtProperties;
import com.reserveflow.auth.dto.TokenResponse;
import com.reserveflow.member.entity.Member;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * JWT access token과 refresh token을 발급하는 Service.
 *
 * 설정된 issuer, secret, TTL을 사용해 HS256 서명 기반 토큰을 생성한다.
 */
@RequiredArgsConstructor
@Service
public class JwtTokenService {

	private final JwtEncoder jwtEncoder;
	private final JwtProperties jwtProperties;

	/**
	 * 회원에게 access token과 refresh token 한 쌍을 발급한다.
	 *
	 * access token은 API 인증에 사용하고, refresh token은 재발급 요청에 사용한다.
	 */
	public TokenResponse issue(Member member) {
		Instant issuedAt = Instant.now();
		Instant accessTokenExpiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());
		Instant refreshTokenExpiresAt = issuedAt.plus(jwtProperties.refreshTokenTtl());

		String accessToken = encode(member, issuedAt, accessTokenExpiresAt, "access");
		String refreshToken = encode(member, issuedAt, refreshTokenExpiresAt, "refresh");

		return new TokenResponse(
				"Bearer",
				accessToken,
				refreshToken,
				jwtProperties.accessTokenTtl().toSeconds(),
				member.getPublicId()
		);
	}

	/**
	 * JWT claims와 HS256 header를 구성해 실제 token 문자열로 인코딩한다.
	 */
	private String encode(Member member, Instant issuedAt, Instant expiresAt, String tokenType) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(jwtProperties.issuer())
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.subject(member.getAuthSubject())
				.claim("member_id", member.getPublicId().toString())
				.claim("token_type", tokenType)
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}
}
