package com.reserveflow.reservationrequest.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 공개 해석 API 요청을 rate limit에 사용할 호출자 키로 변환한다.
 */
@Component
public class InterpretationCallerKeyResolver {

    /**
     * 인증된 요청은 JWT subject를, 비인증 요청은 직접 연결한 클라이언트 IP를 사용한다.
     */
    public String resolve(Jwt jwt, HttpServletRequest request) {
        if (jwt != null && StringUtils.hasText(jwt.getSubject())) {
            return "member:" + jwt.getSubject();
        }
        return "ip:" + request.getRemoteAddr();
    }
}
