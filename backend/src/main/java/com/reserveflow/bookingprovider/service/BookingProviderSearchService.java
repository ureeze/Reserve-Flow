package com.reserveflow.bookingprovider.service;

import com.reserveflow.bookingprovider.dto.BookingProviderSearchResponse;
import com.reserveflow.bookingprovider.dto.BookingProviderSearchResponse.Item;
import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

/**
 * 예약 제공자 검색 조건을 정규화하고 QueryDSL 검색 결과를 API 응답으로 변환한다.
 */
@Service
public class BookingProviderSearchService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final BookingProviderRepository bookingProviderRepository;

    public BookingProviderSearchService(BookingProviderRepository bookingProviderRepository) {
        this.bookingProviderRepository = bookingProviderRepository;
    }

    public BookingProviderSearchResponse search(BookingProviderType providerType, String location, Integer partySize,
            int page, int size) {
        // partySize가 있으면 1 이상이어야 한다. 벗어나면 400 VALIDATION_002.
        if (partySize != null && partySize < 1) {
            throw new ApiException(ErrorCode.VALIDATION_PARTY_SIZE);
        }

        // page는 0 미만이면 0으로, size는 1~50 범위로 clamp 한다.
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), MAX_SIZE);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);

        Slice<BookingProvider> slice = bookingProviderRepository.search(providerType, location, partySize, pageable);

        return new BookingProviderSearchResponse(
                slice.getContent().stream().map(this::toItem).toList(),
                normalizedPage,
                normalizedSize,
                slice.hasNext()
        );
    }

    /** 외부 응답에는 내부 id(Long)가 아니라 public_id(UUID)를 노출한다. */
    private Item toItem(BookingProvider provider) {
        return new Item(
                provider.getPublicId(),
                provider.getName(),
                provider.getProviderType(),
                provider.getLocationText(),
                provider.getTimezone(),
                provider.getMaxPartySize(),
                provider.getStatus()
        );
    }
}
