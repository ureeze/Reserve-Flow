package com.reserveflow.reservationrequest.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Python LLM 해석 서비스 연결과 요청 제한 설정을 보관한다.
 */
@ConfigurationProperties(prefix = "reserveflow.llm")
public record LlmServiceProperties(
        String baseUrl,
        Duration timeout,
        RateLimit rateLimit
) {

    /**
     * 자연어 해석 API의 Redis 기반 요청 제한 설정이다.
     */
    public record RateLimit(int maxRequests, Duration window) {
    }
}
