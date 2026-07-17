package com.reserveflow.reservationrequest.service;

import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.reservationrequest.config.LlmServiceProperties;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * 자연어 해석 API 호출 횟수를 Redis에서 원자적으로 제한한다.
 */
@Component
public class InterpretationRateLimiter {

    private static final String KEY_PREFIX = "rate-limit:reservation-interpret:";
    private static final DefaultRedisScript<Long> INCREMENT_WITH_EXPIRY = new DefaultRedisScript<>(
            "local current = redis.call('INCR', KEYS[1])\n"
                    + "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end\n"
                    + "return current",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final LlmServiceProperties properties;

    public InterpretationRateLimiter(StringRedisTemplate stringRedisTemplate, LlmServiceProperties properties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.properties = properties;
    }

    /**
     * 호출자 키의 현재 윈도우 요청 수를 증가시키고 제한 초과 시 예외를 반환한다.
     */
    public void check(String callerKey) {
        LlmServiceProperties.RateLimit rateLimit = properties.rateLimit();
        long windowSeconds = Math.max(1, rateLimit.window().toSeconds());
        Long current = stringRedisTemplate.execute(
                INCREMENT_WITH_EXPIRY,
                List.of(KEY_PREFIX + callerKey),
                Long.toString(windowSeconds)
        );

        if (current == null || current > rateLimit.maxRequests()) {
            throw new ApiException(ErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }
}
