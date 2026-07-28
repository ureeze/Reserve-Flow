package com.reserveflow.bookingprovider.repository;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * QueryDSL 기반 예약 제공자 동적 검색 Repository.
 */
public interface BookingProviderRepositoryCustom {

    /**
     * ACTIVE 예약 제공자를 선택 필터(providerType/location/partySize)로 검색한다.
     *
     * 다음 페이지 존재 여부만 필요하므로 count 없이 {@code size + 1}건을 조회해 {@link Slice}로 반환한다.
     */
    Slice<BookingProvider> search(BookingProviderType providerType, String location, Integer partySize, Pageable pageable);
}
