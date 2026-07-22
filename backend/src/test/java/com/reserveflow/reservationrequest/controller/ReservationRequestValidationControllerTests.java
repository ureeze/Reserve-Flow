package com.reserveflow.reservationrequest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.entity.BookingProviderBusinessHours;
import com.reserveflow.bookingprovider.repository.BookingProviderBusinessHoursRepository;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class ReservationRequestValidationControllerTests {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingProviderRepository bookingProviderRepository;

    @Autowired
    private BookingProviderBusinessHoursRepository businessHoursRepository;

    private BookingProvider openProvider;
    private BookingProvider narrowHoursProvider;

    /**
     * 요일 구분 없이 종일 영업하는 제공자와, 낮 시간에만 영업하는 제공자를 준비한다.
     */
    @BeforeEach
    void setUp() {
        openProvider = bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow 강남점")
                .providerType(BookingProviderType.RESTAURANT)
                .locationText("서울 강남구")
                .timezone(ZONE.getId())
                .maxPartySize(8)
                .build());
        for (short dayOfWeek = 0; dayOfWeek <= 6; dayOfWeek++) {
            businessHoursRepository.save(BookingProviderBusinessHours.builder()
                    .bookingProviderId(openProvider.getId())
                    .dayOfWeek(dayOfWeek)
                    .opensAt(LocalTime.of(0, 0))
                    .closesAt(LocalTime.of(23, 59))
                    .build());
        }

        narrowHoursProvider = bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow 판교점")
                .providerType(BookingProviderType.RESTAURANT)
                .locationText("경기 성남시")
                .timezone(ZONE.getId())
                .maxPartySize(8)
                .build());
        for (short dayOfWeek = 0; dayOfWeek <= 6; dayOfWeek++) {
            businessHoursRepository.save(BookingProviderBusinessHours.builder()
                    .bookingProviderId(narrowHoursProvider.getId())
                    .dayOfWeek(dayOfWeek)
                    .opensAt(LocalTime.of(11, 0))
                    .closesAt(LocalTime.of(15, 0))
                    .build());
        }
    }

    private LocalDate futureDate() {
        return LocalDate.now(ZONE).plusDays(30);
    }

    /**
     * 유효한 예약 조건은 정규화된 결과와 함께 valid=true를 반환하는지 검증한다.
     */
    @Test
    void validReturnsNormalizedCondition() throws Exception {
        mockMvc.perform(post("/api/v1/reservation-requests/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingProviderId": "%s",
                                  "reservationDate": "%s",
                                  "reservationTime": "19:00",
                                  "partySize": 4
                                }
                                """.formatted(openProvider.getId(), futureDate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.normalized.bookingProviderId").value(openProvider.getId().toString()))
                .andExpect(jsonPath("$.normalized.partySize").value(4))
                .andExpect(jsonPath("$.normalized.timezone").value("Asia/Seoul"))
                .andExpect(jsonPath("$.violations").isEmpty());
    }

    /**
     * 존재하지 않는 예약 제공자는 404 PROVIDER_001을 반환하는지 검증한다.
     */
    @Test
    void validateRejectsUnknownProvider() throws Exception {
        mockMvc.perform(post("/api/v1/reservation-requests/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingProviderId": "%s",
                                  "reservationDate": "%s",
                                  "reservationTime": "19:00",
                                  "partySize": 4
                                }
                                """.formatted(UUID.randomUUID(), futureDate())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROVIDER_001"));
    }

    /**
     * 과거 날짜·시간 요청은 valid=false와 VALIDATION_001 위반을 반환하는지 검증한다.
     */
    @Test
    void validateReturnsViolationForPastDateTime() throws Exception {
        LocalDate pastDate = LocalDate.now(ZONE).minusDays(1);

        mockMvc.perform(post("/api/v1/reservation-requests/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingProviderId": "%s",
                                  "reservationDate": "%s",
                                  "reservationTime": "19:00",
                                  "partySize": 4
                                }
                                """.formatted(openProvider.getId(), pastDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.normalized").doesNotExist())
                .andExpect(jsonPath("$.violations[0].code").value("VALIDATION_001"))
                .andExpect(jsonPath("$.violations[0].field").value("reservationDate"));
    }

    /**
     * 예약 제공자의 최대 인원을 초과하면 VALIDATION_002 위반을 반환하는지 검증한다.
     */
    @Test
    void validateReturnsViolationForPartySizeExceeded() throws Exception {
        mockMvc.perform(post("/api/v1/reservation-requests/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingProviderId": "%s",
                                  "reservationDate": "%s",
                                  "reservationTime": "19:00",
                                  "partySize": 20
                                }
                                """.formatted(openProvider.getId(), futureDate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.violations[0].code").value("VALIDATION_002"))
                .andExpect(jsonPath("$.violations[0].field").value("partySize"));
    }

    /**
     * 등록된 영업시간을 벗어난 요청은 VALIDATION_003 위반을 반환하는지 검증한다.
     */
    @Test
    void validateReturnsViolationForOutsideBusinessHours() throws Exception {
        mockMvc.perform(post("/api/v1/reservation-requests/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingProviderId": "%s",
                                  "reservationDate": "%s",
                                  "reservationTime": "19:00",
                                  "partySize": 4
                                }
                                """.formatted(narrowHoursProvider.getId(), futureDate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.violations[0].code").value("VALIDATION_003"))
                .andExpect(jsonPath("$.violations[0].field").value("reservationTime"));
    }

    /**
     * 필수 예약 조건이 누락되면 400 VALIDATION_004를 반환하는지 검증한다.
     */
    @Test
    void validateRejectsMissingRequiredField() throws Exception {
        mockMvc.perform(post("/api/v1/reservation-requests/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reservationDate": "%s",
                                  "reservationTime": "19:00",
                                  "partySize": 4
                                }
                                """.formatted(futureDate())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_004"))
                .andExpect(jsonPath("$.error.details[0].field").value("bookingProviderId"));
    }
}
