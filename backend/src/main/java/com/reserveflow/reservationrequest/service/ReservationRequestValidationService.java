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

    public ValidateResponse validate(ValidateRequest request) {
        BookingProvider provider = bookingProviderRepository.findById(request.bookingProviderId())
                .orElseThrow(() -> new ApiException(ErrorCode.PROVIDER_NOT_FOUND));

        ZoneId providerZone = ZoneId.of(provider.getTimezone());
        Instant startsAt = ZonedDateTime.of(request.reservationDate(), request.reservationTime(), providerZone)
                .toInstant();

        List<Violation> violations = new ArrayList<>();
        if (startsAt.isBefore(Instant.now())) {
            violations.add(toViolation(ValidationViolationCode.PAST_DATETIME, "reservationDate"));
        }
        if (request.partySize() > provider.getMaxPartySize()) {
            violations.add(toViolation(ValidationViolationCode.PARTY_SIZE_EXCEEDED, "partySize"));
        }
        if (isOutsideBusinessHours(provider, request)) {
            violations.add(toViolation(ValidationViolationCode.OUTSIDE_BUSINESS_HOURS, "reservationTime"));
        }

        if (!violations.isEmpty()) {
            return new ValidateResponse(false, null, violations);
        }

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
        short dayOfWeek = (short) (request.reservationDate().getDayOfWeek().getValue() % 7);
        List<BookingProviderBusinessHours> businessHours = businessHoursRepository
                .findByBookingProviderIdAndDayOfWeek(provider.getId(), dayOfWeek);

        return businessHours.stream().noneMatch(hours ->
                !request.reservationTime().isBefore(hours.getOpensAt())
                        && request.reservationTime().isBefore(hours.getClosesAt())
        );
    }

    private Violation toViolation(ValidationViolationCode code, String field) {
        return new Violation(code.code(), field, code.message());
    }
}
