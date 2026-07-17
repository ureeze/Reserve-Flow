package com.reserveflow.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.reserveflow.common.error.RestAccessDeniedHandler;
import com.reserveflow.common.error.RestAuthenticationEntryPoint;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 인증과 인가를 위한 Spring Security 설정.
 *
 * 인증 발급 API는 공개하고, 그 외 요청은 Bearer JWT 인증을 요구한다.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
class SecurityConfig {

	/**
	 * HTTP 보안 정책을 구성한다.
	 *
	 * 서버 세션을 사용하지 않는 stateless API로 동작시키고
	 * JWT Resource Server 방식으로 Bearer token을 검증한다.
	 */
	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			RestAuthenticationEntryPoint authenticationEntryPoint,
			RestAccessDeniedHandler accessDeniedHandler
	) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
						"/api/v1/auth/signup",
						"/api/v1/auth/login",
						"/api/v1/auth/token/refresh",
						"/api/v1/reservation-requests/interpret"
						).permitAll()
						.anyRequest().authenticated()
				)
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler)
				)
				.httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(formLogin -> formLogin.disable())
				.oauth2ResourceServer(oauth2 -> oauth2
						.authenticationEntryPoint(authenticationEntryPoint)
						.jwt(Customizer.withDefaults())
				)
				.build();
	}

	/**
	 * 사용자 비밀번호를 안전하게 해시하고 검증하기 위한 BCrypt encoder를 제공한다.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * JWT 서명과 검증에 사용할 대칭키를 생성한다.
	 */
	@Bean
	SecretKey jwtSecretKey(JwtProperties jwtProperties) {
		return new SecretKeySpec(jwtProperties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

	/**
	 * JWT를 발급할 때 사용할 encoder를 제공한다.
	 */
	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
	}

	/**
	 * 요청으로 들어온 Bearer JWT를 검증할 decoder를 제공한다.
	 */
	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSecretKey)
				.macAlgorithm(MacAlgorithm.HS256)
				.build();
	}
}
