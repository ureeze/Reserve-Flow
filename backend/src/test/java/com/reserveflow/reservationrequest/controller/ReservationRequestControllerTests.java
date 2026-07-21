package com.reserveflow.reservationrequest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.reservationrequest.client.LlmExtractionClient;
import com.reserveflow.reservationrequest.client.LlmExtractionClient.LlmResult;
import com.reserveflow.reservationrequest.service.RequestRateLimiter;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class ReservationRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LlmExtractionClient llmClient;

    @MockitoBean
    private RequestRateLimiter rateLimiter;

    /**
     * 인증 없이 자연어 요청을 보내도 구조화된 예약 조건을 반환하는지 검증한다.
     */
    @Test
    void extractReturnsStructuredReservationConditionWithoutAuthentication() throws Exception {
        given(llmClient.extract(anyString(), any(LocalDate.class), anyString()))
                .willReturn(new LlmResult(
                        "2026-07-18",
                        "19:00",
                        4,
                        "강남",
                        "RESTAURANT",
                        List.of()
                ));

        mockMvc.perform(post("/api/v1/reservation-requests/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservationMessage": "이번 주 금요일 저녁 7시에 강남에서 4명 예약하고 싶어요."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationDate").value("2026-07-18"))
                .andExpect(jsonPath("$.reservationTime").value("19:00"))
                .andExpect(jsonPath("$.partySize").value(4))
                .andExpect(jsonPath("$.location").value("강남"))
                .andExpect(jsonPath("$.providerType").value("RESTAURANT"))
                .andExpect(jsonPath("$.missingFields").isArray());
    }

    /**
     * 업종을 추론하지 못한 LLM 결과는 400 PARSE_004로 변환되는지 검증한다.
     */
    @Test
    void extractRejectsMissingProviderType() throws Exception {
        given(llmClient.extract(anyString(), any(LocalDate.class), anyString()))
                .willReturn(new LlmResult(
                        "2026-07-18",
                        "19:00",
                        4,
                        "강남",
                        null,
                        List.of("providerType")
                ));

        mockMvc.perform(post("/api/v1/reservation-requests/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservationMessage": "강남에서 4명 예약하고 싶어요."
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("PARSE_004"))
                .andExpect(jsonPath("$.error.details[0].field").value("providerType"));
    }

    /**
     * Redis 요청 제한을 초과하면 429 RATE_LIMIT_001을 반환하는지 검증한다.
     */
    @Test
    void extractRejectsRateLimitExceeded() throws Exception {
        doThrow(new ApiException(ErrorCode.RATE_LIMIT_EXCEEDED))
                .when(rateLimiter)
                .check(anyString());

        mockMvc.perform(post("/api/v1/reservation-requests/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservationMessage": "내일 저녁 7시에 식당 예약하고 싶어요."
                                }
                                """))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error.code").value("RATE_LIMIT_001"));
    }

    /**
     * Python LLM 서비스 호출 실패는 502 LLM_001로 반환하는지 검증한다.
     */
    @Test
    void extractMapsLlmFailureToBadGateway() throws Exception {
        given(llmClient.extract(anyString(), any(LocalDate.class), anyString()))
                .willThrow(new ApiException(ErrorCode.LLM_UNAVAILABLE));

        mockMvc.perform(post("/api/v1/reservation-requests/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservationMessage": "내일 저녁 7시에 식당 예약하고 싶어요."
                                }
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error.code").value("LLM_001"));
    }

    /**
     * 필수 자연어 입력을 비우면 기존 공통 Validation 오류를 반환하는지 검증한다.
     */
    @Test
    void extractRejectsBlankReservationMessage() throws Exception {
        mockMvc.perform(post("/api/v1/reservation-requests/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservationMessage": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_004"))
                .andExpect(jsonPath("$.error.details[0].field").value("reservationMessage"));
    }
}
