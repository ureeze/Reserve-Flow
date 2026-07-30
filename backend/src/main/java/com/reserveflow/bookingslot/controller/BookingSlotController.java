package com.reserveflow.bookingslot.controller;

import com.reserveflow.bookingslot.dto.BookingSlotResponse;
import com.reserveflow.bookingslot.service.BookingSlotService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/booking-providers")
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    /**
     * 특정 예약 제공자의 날짜별 예약 가능 booking slot 목록을 조회한다.
     *
     * @param providerPublicId 예약 제공자의 외부 식별자 (UUID)
     * @param date             조회할 날짜 (YYYY-MM-DD), 생략 시 공급자 타임존 기준 오늘 날짜
     * @return 해당 일자의 예약 가능 booking slot 목록
     */
    @GetMapping("/{providerPublicId}/slots")
    public List<BookingSlotResponse> getSlots(
            @PathVariable UUID providerPublicId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // 1. 예약 제공자 존재 여부 검증 및 타임존 확인
        // 2. 요청된 날짜(또는 오늘)의 시작 시각과 종료 시각(Instant) 계산
        // 3. 해당 시간 범위 내의 booking slot 목록을 시간순으로 조회하여 반환
        return bookingSlotService.getSlots(providerPublicId, date);
    }
}
