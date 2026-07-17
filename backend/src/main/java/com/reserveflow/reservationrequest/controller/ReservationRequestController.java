package com.reserveflow.reservationrequest.controller;

import com.reserveflow.reservationrequest.dto.InterpretReservationRequest;
import com.reserveflow.reservationrequest.dto.InterpretReservationResponse;
import com.reserveflow.reservationrequest.service.InterpretationCallerKeyResolver;
import com.reserveflow.reservationrequest.service.ReservationRequestInterpretationService;
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
 * 사용자의 자연어 예약 요청을 구조화된 예약 조건으로 변환하는 공개 API다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reservation-requests")
public class ReservationRequestController {

    private final ReservationRequestInterpretationService interpretationService;
    private final InterpretationCallerKeyResolver callerKeyResolver;

    /**
     * 자연어 요청을 Python LLM 서비스로 해석하고 예약 조건 후보를 반환한다.
     */
    @PostMapping("/interpret")
    public InterpretReservationResponse interpret(
            @Valid @RequestBody InterpretReservationRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest
    ) {
        String callerKey = callerKeyResolver.resolve(jwt, httpRequest);
        return interpretationService.interpret(request, callerKey);
    }
}
