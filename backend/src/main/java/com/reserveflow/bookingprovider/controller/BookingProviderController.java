package com.reserveflow.bookingprovider.controller;

import com.reserveflow.bookingprovider.dto.BookingProviderSearchResponse;
import com.reserveflow.bookingprovider.service.BookingProviderSearchService;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 예약 제공자 검색 공개 API이다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/booking-providers")
public class BookingProviderController {

    private final BookingProviderSearchService searchService;

    /**
     * ACTIVE 예약 제공자를 선택 필터로 검색한다. 필터가 없으면 전체 ACTIVE 목록을 페이지네이션으로 반환한다.
     */
    @GetMapping
    public BookingProviderSearchResponse search(
            @RequestParam(required = false) BookingProviderType providerType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer partySize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return searchService.search(providerType, location, partySize, page, size);
    }
}
