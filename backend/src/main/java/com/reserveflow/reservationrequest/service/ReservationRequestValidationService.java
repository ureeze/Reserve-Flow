package com.reserveflow.reservationrequest.service;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.entity.BookingProviderBusinessHours;
import com.reserveflow.bookingprovider.repository.BookingProviderBusinessHoursRepository;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.reservationrequest.dto.ValidateRequest;
import com.reserveflow.reservationrequest.dto.ValidateResponse;
import com.reserveflow.reservationrequest.dto.ValidateResponse.Normalized;
import com.reserveflow.reservationrequest.dto.ValidateResponse.Violation;
import com.reserveflow.reservationrequest.dto.ValidationViolationCode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 구조화된 예약 조건이 예약 제공자의 운영 정책을 만족하는지 검증한다.
 */
@RequiredArgsConstructor
@Service
public class ReservationRequestValidationService {

    private final BookingProviderRepository bookingProviderRepository;
    private final BookingProviderBusinessHoursRepository businessHoursRepository;

    /**
     * 예약 조건을 예약 제공자 운영 정책 기준으로 검증한다.
     *
     * 제공자가 없으면 404 PROVIDER_001로 실패하고, 그 외 조건 위반은 예외 대신
     * valid=false와 위반 목록으로 반환한다. 모두 통과하면 provider timezone 기준
     * UTC 시각으로 정규화한 결과를 valid=true와 함께 반환한다.
     */
    public ValidateResponse validate(ValidateRequest request) {
        // 검증 기준이 되는 예약 제공자를 조회한다. 없으면 404 PROVIDER_001로 처리한다.
        BookingProvider provider = bookingProviderRepository.findById(request.bookingProviderId())
                .orElseThrow(() -> new ApiException(ErrorCode.PROVIDER_NOT_FOUND));

        // 요청의 현지 날짜/시간을 제공자 timezone 기준 절대 시각(UTC)으로 변환한다.
        ZoneId providerZone = ZoneId.of(provider.getTimezone());
        Instant startsAt = ZonedDateTime.of(request.reservationDate(), request.reservationTime(), providerZone)
                .toInstant();

        // 조건 위반은 즉시 중단하지 않고 모두 수집해 한 번에 반환한다.
        List<Violation> violations = new ArrayList<>();
        // 과거 시각 예약 여부(VALIDATION_001)
        if (startsAt.isBefore(Instant.now())) {
            violations.add(toViolation(ValidationViolationCode.PAST_DATETIME, "reservationDate"));
        }
        // 제공자 최대 인원 초과 여부(VALIDATION_002)
        if (request.partySize() > provider.getMaxPartySize()) {
            violations.add(toViolation(ValidationViolationCode.PARTY_SIZE_EXCEEDED, "partySize"));
        }
        // 영업시간 외 요청 여부(VALIDATION_003)
        if (isOutsideBusinessHours(provider, request)) {
            violations.add(toViolation(ValidationViolationCode.OUTSIDE_BUSINESS_HOURS, "reservationTime"));
        }

        // 위반이 하나라도 있으면 정규화 없이 valid=false와 위반 목록을 반환한다.
        if (!violations.isEmpty()) {
            return new ValidateResponse(false, null, violations);
        }

        // 모든 검증을 통과하면 후속 예약 흐름이 사용할 정규화된 조건을 만들어 반환한다.
        Normalized normalized = new Normalized(
                provider.getId(),
                startsAt,
                request.partySize(),
                provider.getTimezone()
        );
        return new ValidateResponse(true, normalized, List.of());
    }

    /** 요청 시간이 예약 제공자의 해당 요일 운영 구간 중 어디에도 속하지 않으면 영업시간 외로 판단한다. */
    private boolean isOutsideBusinessHours(BookingProvider provider, ValidateRequest request) {
        // DayOfWeek는 월=1~일=7이므로 %7로 DB 저장 기준(일=0~토=6)에 맞춘다.
        short dayOfWeek = (short) (request.reservationDate().getDayOfWeek().getValue() % 7);
        List<BookingProviderBusinessHours> businessHours = businessHoursRepository
                .findByBookingProviderIdAndDayOfWeek(provider.getId(), dayOfWeek);

        // 요청 시간이 [opensAt, closesAt) 구간에 포함되는 운영 구간이 하나도 없으면 영업시간 외다.
        return businessHours.stream().noneMatch(hours ->
                !request.reservationTime().isBefore(hours.getOpensAt())
                        && request.reservationTime().isBefore(hours.getClosesAt())
        );
    }

    /** ValidationViolationCode와 대상 필드를 API 응답용 violation 항목으로 변환한다. */
    private Violation toViolation(ValidationViolationCode code, String field) {
        return new Violation(code.code(), field, code.message());
    }
}
