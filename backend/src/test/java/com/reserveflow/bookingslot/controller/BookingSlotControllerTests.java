package com.reserveflow.bookingslot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.bookingslot.entity.BookingSlot;
import com.reserveflow.bookingslot.repository.BookingSlotRepository;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class BookingSlotControllerTests {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingProviderRepository bookingProviderRepository;

    @Autowired
    private BookingSlotRepository bookingSlotRepository;

    private BookingProvider provider;
    private LocalDate targetDate;

    @BeforeEach
    void setUp() {
        provider = bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow 슬롯 테스트점")
                .providerType(BookingProviderType.RESTAURANT)
                .locationText("서울 마포구")
                .timezone(ZONE.getId())
                .maxPartySize(10)
                .build());

        targetDate = LocalDate.now(ZONE).plusDays(5);
        var startsAt = targetDate.atTime(12, 0).atZone(ZONE).toInstant();
        var endsAt = targetDate.atTime(13, 0).atZone(ZONE).toInstant();

        bookingSlotRepository.save(BookingSlot.builder()
                .bookingProvider(provider)
                .startsAt(startsAt)
                .endsAt(endsAt)
                .totalCapacity(20)
                .availableCapacity(15)
                .build());
    }

    /**
     * 유효한 예약 제공자와 날짜로 슬롯 조회 시 해당 일자의 예약 가능 슬롯 목록과 올바른 수용량 정보를 반환하는지 검증한다.
     */
    @Test
    void getSlotsReturnsSlotsForDate() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers/{providerPublicId}/slots", provider.getPublicId())
                        .param("date", targetDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalCapacity").value(20))
                .andExpect(jsonPath("$[0].availableCapacity").value(15))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    /**
     * 존재하지 않는 예약 제공자 식별자로 슬롯 조회 시 404 PROVIDER_001 오류를 반환하는지 검증한다.
     */
    @Test
    void getSlotsReturnsNotFoundForUnknownProvider() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers/{providerPublicId}/slots", UUID.randomUUID())
                        .param("date", targetDate.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROVIDER_001"));
    }
}
