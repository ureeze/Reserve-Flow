package com.reserveflow.reservationrequest.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 공개 해석 API 요청을 rate limit에 사용할 호출자 키로 변환한다.
 */
@Component
public class CallerKeyResolver {

    /**
     * 인증된 요청은 JWT subject를, 비인증 요청은 직접 연결한 클라이언트 IP를 사용한다.
     */
    public String resolve(Jwt jwt, HttpServletRequest request) {
        // 로그인 사용자는 JWT subject를 기준으로 호출 횟수를 구분한다.
        if (jwt != null && StringUtils.hasText(jwt.getSubject())) {
            return "member:" + jwt.getSubject();
        }

        // 비로그인 요청은 요청 IP를 기준으로 호출자를 구분한다.
        return "ip:" + request.getRemoteAddr();
    }
}
