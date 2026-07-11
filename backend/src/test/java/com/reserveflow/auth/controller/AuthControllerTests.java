package com.reserveflow.auth.controller;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTests {

	@Autowired
	private MockMvc mockMvc;

	/**
	 * 회원가입 요청이 유효하면 회원을 생성하고 토큰 없이 회원 생성 결과만 반환하는지 검증한다.
	 */
	@Test
	void signupCreatesMemberWithoutTokens() throws Exception {
		mockMvc.perform(post("/api/v1/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "signup-user@example.test",
								  "password": "password123!",
								  "displayName": "Signup User"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.memberId").value(not(blankOrNullString())))
				.andExpect(jsonPath("$.authSubject").value("signup-user@example.test"))
				.andExpect(jsonPath("$.displayName").value("Signup User"))
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andExpect(jsonPath("$.accessToken").doesNotExist())
				.andExpect(jsonPath("$.refreshToken").doesNotExist());
	}

	/**
	 * 이미 가입된 인증 식별자로 다시 회원가입하면 중복 가입을 거부하는지 검증한다.
	 */
	@Test
	void signupRejectsDuplicateAuthSubject() throws Exception {
		String body = """
				{
				  "authSubject": "duplicate-user@example.test",
				  "password": "password123!",
				  "displayName": "Duplicate User"
				}
				""";

		mockMvc.perform(post("/api/v1/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isConflict());
	}

	/**
	 * 가입된 회원이 올바른 비밀번호로 로그인하면 Bearer access token을 발급하는지 검증한다.
	 */
	@Test
	void loginIssuesTokensWithValidPassword() throws Exception {
		signup("login-user@example.test", "password123!");

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "login-user@example.test",
								  "password": "password123!"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.accessToken").value(not(blankOrNullString())));
	}

	/**
	 * 가입된 회원이라도 비밀번호가 틀리면 로그인을 거부하고 401을 반환하는지 검증한다.
	 */
	@Test
	void loginRejectsInvalidPassword() throws Exception {
		signup("invalid-password-user@example.test", "password123!");

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "invalid-password-user@example.test",
								  "password": "wrong-password"
								}
								"""))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * 유효한 refresh token을 제출하면 새로운 access/refresh token 쌍을 발급하는지 검증한다.
	 */
	@Test
	void refreshIssuesNewTokenPair() throws Exception {
		signup("refresh-user@example.test", "password123!");
		MvcResult login = login("refresh-user@example.test", "password123!");
		String refreshToken = JsonPathReader.read(login, "$.refreshToken");

		mockMvc.perform(post("/api/v1/auth/token/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "%s"
								}
								""".formatted(refreshToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value(not(blankOrNullString())))
				.andExpect(jsonPath("$.refreshToken").value(not(blankOrNullString())));
	}

	/**
	 * 보호 API는 Bearer access token 없이 호출할 수 없고 401을 반환하는지 검증한다.
	 */
	@Test
	void protectedEndpointRequiresBearerToken() throws Exception {
		mockMvc.perform(get("/api/v1/auth/me"))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * 유효한 Bearer access token으로 현재 회원 조회 API를 호출하면 인증된 회원 정보를 반환하는지 검증한다.
	 */
	@Test
	void currentMemberReturnsAuthenticatedMember() throws Exception {
		signup("me-user@example.test", "password123!");
		MvcResult login = login("me-user@example.test", "password123!");
		String accessToken = JsonPathReader.read(login, "$.accessToken");

		mockMvc.perform(get("/api/v1/auth/me")
						.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.authSubject").value("me-user@example.test"))
				.andExpect(jsonPath("$.status").value("ACTIVE"));
	}

	private MvcResult signup(String authSubject, String password) throws Exception {
		return mockMvc.perform(post("/api/v1/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "%s",
								  "password": "%s",
								  "displayName": "Test User"
								}
								""".formatted(authSubject, password)))
				.andExpect(status().isCreated())
				.andReturn();
	}

	private MvcResult login(String authSubject, String password) throws Exception {
		return mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "%s",
								  "password": "%s"
								}
								""".formatted(authSubject, password)))
				.andExpect(status().isOk())
				.andReturn();
	}
}
