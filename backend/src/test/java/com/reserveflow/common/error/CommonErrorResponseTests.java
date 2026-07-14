package com.reserveflow.common.error;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class CommonErrorResponseTests {

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Bean Validation 실패가 400 공통 오류 응답으로 변환되는지 검증한다.
	 */
	@Test
	void validationErrorReturnsCommonErrorResponse() throws Exception {
		mockMvc.perform(post("/api/v1/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "",
								  "password": "short",
								  "displayName": "Validation User"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.code").value("VALIDATION_004"))
				.andExpect(jsonPath("$.error.message").value("필수 요청 값이 누락되었습니다."))
				.andExpect(jsonPath("$.error.details").isArray())
				.andExpect(jsonPath("$.error.requestId").value(not(blankOrNullString())))
				.andExpect(jsonPath("$.error.timestamp").value(not(blankOrNullString())));
	}

	/**
	 * Error Catalog 기반 404 예외가 공통 오류 응답으로 변환되는지 검증한다.
	 */
	@Test
	void notFoundErrorReturnsCommonErrorResponse() throws Exception {
		String accessToken = accessToken("not-found-user@example.test");

		mockMvc.perform(get("/api/v1/test/not-found")
						.header("Authorization", "Bearer " + accessToken)
						.header("X-Request-Id", "not-found-request-id"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error.code").value("PROVIDER_001"))
				.andExpect(jsonPath("$.error.message").value("예약 제공자를 찾을 수 없습니다."))
				.andExpect(jsonPath("$.error.requestId").value("not-found-request-id"))
				.andExpect(jsonPath("$.error.timestamp").value(not(blankOrNullString())));
	}

	private String accessToken(String authSubject) throws Exception {
		mockMvc.perform(post("/api/v1/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "%s",
								  "password": "password123!",
								  "displayName": "Test User"
								}
								""".formatted(authSubject)))
				.andExpect(status().isCreated());

		MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "authSubject": "%s",
								  "password": "password123!"
								}
								""".formatted(authSubject)))
				.andExpect(status().isOk())
				.andReturn();

		String content = login.getResponse().getContentAsString(StandardCharsets.UTF_8);
		return JsonPath.read(content, "$.accessToken");
	}

	@TestConfiguration
	static class TestErrorControllerConfiguration {

		@Bean
		TestErrorController testErrorController() {
			return new TestErrorController();
		}
	}

	@RestController
	@RequestMapping("/api/v1/test")
	static class TestErrorController {

		@GetMapping("/not-found")
		void notFound() {
			throw new ApiException(ErrorCode.PROVIDER_NOT_FOUND);
		}
	}
}
