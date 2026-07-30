package com.reserveflow.bookingslot.service;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.bookingslot.dto.BookingSlotResponse;
import com.reserveflow.bookingslot.entity.BookingSlot;
import com.reserveflow.bookingslot.repository.BookingSlotRepository;
import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingSlotService {

    private final BookingProviderRepository bookingProviderRepository;
    private final BookingSlotRepository bookingSlotRepository;

    /**
     * 예약 제공자 외부 식별자와 조회 날짜를 기준으로 예약 가능 슬롯 목록을 조회한다.
     *
     * @param providerPublicId 예약 제공자 외부 식별자 (UUID)
     * @param date             조회할 날짜 (null인 경우 공급자 타임존 기준 오늘 날짜)
     * @return 시간순으로 정렬된 BookingSlotResponse 목록
     */
    public List<BookingSlotResponse> getSlots(UUID providerPublicId, LocalDate date) {
        // 1. 외부 식별자(publicId)로 예약 제공자 존재 여부 확인 (없으면 404 PROVIDER_001 예외 발생)
        BookingProvider provider = bookingProviderRepository.findByPublicId(providerPublicId)
                .orElseThrow(() -> new ApiException(ErrorCode.PROVIDER_NOT_FOUND));

        // 2. 조회할 날짜 결정 (전달된 날짜가 없으면 공급자의 현지 타임존 기준 오늘 날짜 사용)
        LocalDate targetDate = (date != null) ? date : LocalDate.now(ZoneId.of(provider.getTimezone()));
        var zoneId = ZoneId.of(provider.getTimezone());
        
        // 3. 공급자 타임존 기준 해당 일자의 시작 시각(00:00)과 다음 날 시작 시각(00:00)을 Instant로 변환
        var startInstant = targetDate.atStartOfDay(zoneId).toInstant();
        var endInstant = targetDate.plusDays(1).atStartOfDay(zoneId).toInstant();

        // 4. 내부 ID와 시간 범위를 조건으로 DB에서 booking slot 목록을 시간순 조회
        List<BookingSlot> slots = bookingSlotRepository.findByBookingProvider_IdAndStartsAtBetweenOrderByStartsAtAsc(
                provider.getId(), startInstant, endInstant
        );

        // 5. 조회된 엔티티 목록을 응답 DTO로 매핑하여 반환
        return slots.stream().map(this::toDto).toList();
    }

    private BookingSlotResponse toDto(BookingSlot slot) {
        return BookingSlotResponse.builder()
                .publicId(slot.getPublicId())
                .startsAt(slot.getStartsAt())
                .endsAt(slot.getEndsAt())
                .totalCapacity(slot.getTotalCapacity())
                .availableCapacity(slot.getAvailableCapacity())
                .status(slot.getStatus())
                .build();
    }
}
