package com.reserveflow.reservationrequest.service;

import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.reservationrequest.config.LlmProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * 자연어 해석 API 호출 횟수를 Redis에서 원자적으로 제한한다.
 */
@RequiredArgsConstructor
@Component
public class RequestRateLimiter {

    private static final String KEY_PREFIX = "rate-limit:reservation-extract:";
    private static final DefaultRedisScript<Long> INCREMENT_WITH_EXPIRY = new DefaultRedisScript<>(
            "local current = redis.call('INCR', KEYS[1])\n"
                    + "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end\n"
                    + "return current",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final LlmProperties properties;

    /**
     * 호출자 키의 현재 윈도우 요청 수를 증가시키고 제한 초과 시 예외를 던진다.
     */
    public void check(String callerKey) {
        // 설정에 정의된 윈도우 시간과 최대 요청 수를 가져온다.
        LlmProperties.RateLimit rateLimit = properties.rateLimit();
        long windowSeconds = Math.max(1, rateLimit.window().toSeconds());

        // Redis에서 요청 수를 원자적으로 증가시키고 첫 요청이면 만료 시간을 설정한다.
        Long current = stringRedisTemplate.execute(
                INCREMENT_WITH_EXPIRY,
                List.of(KEY_PREFIX + callerKey),
                Long.toString(windowSeconds)
        );

        // Redis 응답이 없거나 허용량을 넘으면 더 이상 LLM 호출로 진행하지 않는다.
        if (current == null || current > rateLimit.maxRequests()) {
            throw new ApiException(ErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }
}
