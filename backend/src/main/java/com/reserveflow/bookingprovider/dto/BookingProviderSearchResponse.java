package com.reserveflow.bookingprovider.dto;

import com.reserveflow.bookingprovider.entity.BookingProviderStatus;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import java.util.List;
import java.util.UUID;

/**
 * 예약 제공자 검색 결과를 페이지네이션 형태로 반환한다.
 */
public record BookingProviderSearchResponse(
        List<Item> items,
        int page,
        int size,
        boolean hasNext
) {

    /**
     * 검색 목록의 예약 제공자 한 건. 외부 식별자는 내부 id가 아니라 public_id(UUID)를 노출한다.
     */
    public record Item(
            UUID bookingProviderId,
            String name,
            BookingProviderType providerType,
            String locationText,
            String timezone,
            int maxPartySize,
            BookingProviderStatus status
    ) {
    }
}
