package com.reserveflow.reservationrequest.controller;

import com.reserveflow.reservationrequest.dto.ExtractReservationRequest;
import com.reserveflow.reservationrequest.dto.ExtractReservationResponse;
import com.reserveflow.reservationrequest.service.ExtractionCallerKeyResolver;
import com.reserveflow.reservationrequest.service.ReservationRequestExtractionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자의 자연어 예약 요청을 구조화된 예약 조건으로 변환하는 공개 API이다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reservation-requests")
public class ReservationRequestController {

    private final ReservationRequestExtractionService extractionService;
    private final ExtractionCallerKeyResolver callerKeyResolver;

    /**
     * 자연어 예약 요청을 받아 Python LLM 서비스로 해석하고 예약 조건 후보를 반환한다.
     */
    @PostMapping("/extract")
    public ExtractReservationResponse extract(
            @Valid @RequestBody ExtractReservationRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest
    ) {
        // 인증 사용자는 JWT subject, 비인증 사용자는 요청 IP를 기준으로 호출자 키를 만든다.
        String callerKey = callerKeyResolver.resolve(jwt, httpRequest);

        // 호출 제한 확인, LLM 호출, 응답 검증은 서비스 계층에 위임한다.
        return extractionService.extract(request, callerKey);
    }
}
