package com.reserveflow.bookingprovider.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.entity.BookingProviderStatus;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
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
class BookingProviderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingProviderRepository bookingProviderRepository;

    private BookingProvider gangnamRestaurant;

    /**
     * 지역/유형/최대 인원이 다른 ACTIVE 제공자 2건과 INACTIVE 제공자 1건을 준비한다.
     */
    @BeforeEach
    void setUp() {
        bookingProviderRepository.deleteAll();
        gangnamRestaurant = bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow 강남점")
                .providerType(BookingProviderType.RESTAURANT)
                .locationText("서울 강남구 테헤란로")
                .maxPartySize(8)
                .build());
        bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow 판교 병원")
                .providerType(BookingProviderType.HOSPITAL)
                .locationText("경기 성남시 분당구")
                .maxPartySize(2)
                .build());
        bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow 폐점 지점")
                .providerType(BookingProviderType.RESTAURANT)
                .locationText("서울 강남구")
                .maxPartySize(4)
                .status(BookingProviderStatus.INACTIVE)
                .build());
    }

    /**
     * 필터 없이 조회하면 ACTIVE 제공자만 페이지네이션으로 반환하는지 검증한다.
     */
    @Test
    void searchReturnsActiveProvidersWithoutFilter() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    /**
     * providerType 필터가 조건에 맞는 제공자만 반환하고 public_id를 노출하는지 검증한다.
     */
    @Test
    void searchFiltersByProviderTypeAndExposesPublicId() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers").param("providerType", "RESTAURANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].bookingProviderId").value(gangnamRestaurant.getPublicId().toString()))
                .andExpect(jsonPath("$.items[0].providerType").value("RESTAURANT"))
                .andExpect(jsonPath("$.items[0].status").value("ACTIVE"));
    }

    /**
     * location 부분 검색이 동작하는지 검증한다.
     */
    @Test
    void searchFiltersByLocation() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers").param("location", "분당"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].providerType").value("HOSPITAL"));
    }

    /**
     * partySize 필터가 최대 인원 이상인 제공자만 남기는지 검증한다.
     */
    @Test
    void searchFiltersByPartySize() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers").param("partySize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("ReserveFlow 강남점"));
    }

    /**
     * size가 1이면 다음 페이지가 있을 때 hasNext=true를 반환하는지 검증한다.
     */
    @Test
    void searchReturnsHasNextWhenMorePages() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    /**
     * size가 상한(50)을 넘으면 50으로 clamp 되는지 검증한다.
     */
    @Test
    void searchClampsSizeToMax() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers").param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));
    }

    /**
     * partySize가 1 미만이면 400 VALIDATION_002를 반환하는지 검증한다.
     */
    @Test
    void searchRejectsInvalidPartySize() throws Exception {
        mockMvc.perform(get("/api/v1/booking-providers").param("partySize", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_002"));
    }
}
