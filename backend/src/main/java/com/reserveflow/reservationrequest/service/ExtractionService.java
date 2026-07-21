package com.reserveflow.reservationrequest.service;

import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.common.error.ErrorResponse;
import com.reserveflow.reservationrequest.client.LlmExtractionClient;
import com.reserveflow.reservationrequest.client.LlmExtractionClient.LlmResult;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import com.reserveflow.reservationrequest.dto.ExtractRequest;
import com.reserveflow.reservationrequest.dto.ExtractResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * 자연어 예약 요청을 Python LLM 서비스에 전달하고 API 계약에 맞게 검증한다.
 */
@RequiredArgsConstructor
@Service
public class ExtractionService {

    private final LlmExtractionClient llmClient;
    private final RequestRateLimiter rateLimiter;

    /**
     * 호출 횟수를 확인한 뒤 서버 현재 날짜를 기준으로 자연어 예약 요청을 해석한다.
     */
    public ExtractResponse extract(ExtractRequest request, String callerKey) {
        // 호출자별 LLM 해석 API 사용량을 먼저 제한한다.
        rateLimiter.check(callerKey);

        // "오늘", "내일" 같은 상대 날짜 해석을 위해 서버 기준 날짜와 시간대를 전달한다.
        ZoneId serverZone = ZoneId.systemDefault();
        LlmResult result = llmClient.extract(
                request.reservationMessage(),
                LocalDate.now(serverZone),
                serverZone.getId()
        );

        // LLM 응답을 API 응답 계약에 맞게 파싱하고 검증한다.
        return new ExtractResponse(
                parseDate(result.reservationDate()),
                parseTime(result.reservationTime()),
                validatePartySize(result.partySize()),
                result.location(),
                parseProviderType(result.providerType()),
                result.missingFields() == null ? List.of() : List.copyOf(result.missingFields())
        );
    }

    /** LLM이 반환한 날짜 문자열을 ISO 8601 날짜 형식으로 검증한다. */
    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw invalidResult("reservationDate", "ISO 8601 날짜 형식이어야 합니다.");
        }
    }

    /** LLM이 반환한 시간 문자열을 HH:mm 형식으로 검증한다. */
    private LocalTime parseTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw invalidResult("reservationTime", "HH:mm 시간 형식이어야 합니다.");
        }
    }

    /** 예약 인원이 존재하면 양수인지 검증한다. */
    private Integer validatePartySize(Integer value) {
        if (value == null) {
            return null;
        }
        if (value < 1) {
            throw invalidResult("partySize", "1명 이상이어야 합니다.");
        }
        return value;
    }

    /** providerType은 MVP에서 지원하는 예약 제공자 유형 중 하나여야 한다. */
    private BookingProviderType parseProviderType(String value) {
        if (!StringUtils.hasText(value)) {
            throw invalidResult("providerType", "예약 제공자 유형을 추론하지 못했습니다.");
        }
        try {
            return BookingProviderType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw invalidResult("providerType", "지원하지 않는 예약 제공자 유형입니다.");
        }
    }

    /** LLM 출력이 API 계약을 따르지 않을 때 반환할 공통 예외를 생성한다. */
    private ApiException invalidResult(String field, String reason) {
        return new ApiException(
                ErrorCode.PARSE_RESULT_INVALID,
                ErrorCode.PARSE_RESULT_INVALID.message(),
                List.of(new ErrorResponse.ErrorDetail(field, reason))
        );
    }
}
