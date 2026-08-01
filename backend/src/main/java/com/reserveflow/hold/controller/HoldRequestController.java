package com.reserveflow.hold.controller;

import com.reserveflow.hold.dto.HoldRequestCreateRequest;
import com.reserveflow.hold.dto.HoldRequestResponse;
import com.reserveflow.hold.service.HoldRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hold 생성 요청 API를 받는 Controller.
 *
 * 요청한 회원은 JWT에서 식별하고, 실제 접수 처리는 HoldRequestService에 위임한다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hold-requests")
public class HoldRequestController {

    private final HoldRequestService holdRequestService;

    /**
     * 비동기 Hold 생성을 접수한다.
     *
     * Hold 생성은 비동기로 처리되므로 즉시 202 Accepted와 PENDING 상태의 holdRequestId를 반환한다.
     */
    @PostMapping
    public ResponseEntity<HoldRequestResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody HoldRequestCreateRequest request
    ) {
        HoldRequestResponse response = holdRequestService.create(jwt.getSubject(), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
