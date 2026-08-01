package com.reserveflow.hold.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.reserveflow.bookingprovider.entity.BookingProvider;
import com.reserveflow.bookingprovider.repository.BookingProviderRepository;
import com.reserveflow.bookingslot.entity.BookingSlot;
import com.reserveflow.bookingslot.repository.BookingSlotRepository;
import com.reserveflow.outbox.entity.OutboxEvent;
import com.reserveflow.outbox.repository.OutboxEventRepository;
import com.reserveflow.reservationrequest.dto.BookingProviderType;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class HoldRequestControllerTests {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingProviderRepository bookingProviderRepository;

    @Autowired
    private BookingSlotRepository bookingSlotRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    private String accessToken;
    private BookingSlot slot;

    private static final AtomicInteger USER_SEQ = new AtomicInteger();

    @BeforeEach
    void setUp() throws Exception {
        accessToken = signupAndLogin("hold-user-" + USER_SEQ.incrementAndGet() + "@example.test");

        BookingProvider provider = bookingProviderRepository.save(BookingProvider.builder()
                .name("ReserveFlow Hold 테스트점")
                .providerType(BookingProviderType.RESTAURANT)
                .locationText("서울 마포구")
                .timezone(ZONE.getId())
                .maxPartySize(10)
                .build());

        Instant startsAt = Instant.parse("2026-08-10T03:00:00Z");
        Instant endsAt = Instant.parse("2026-08-10T04:00:00Z");
        slot = bookingSlotRepository.save(BookingSlot.builder()
                .bookingProvider(provider)
                .startsAt(startsAt)
                .endsAt(endsAt)
                .totalCapacity(20)
                .availableCapacity(15)
                .build());
    }

    /**
     * 유효한 요청이면 202 Accepted와 PENDING 상태의 holdRequestId를 반환하고
     * HoldRequest와 HOLD_REQUESTED Outbox 이벤트가 같은 트랜잭션에 저장되는지 검증한다.
     */
    @Test
    void createReturnsPendingHoldRequestAndOutboxEvent() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/hold-requests")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingSlotId": "%s",
                                  "partySize": 2
                                }
                                """.formatted(slot.getPublicId())))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.holdRequestId").value(not(blankOrNullString())))
                .andReturn();

        UUID holdRequestId = UUID.fromString(readJson(result, "$.holdRequestId"));

        List<OutboxEvent> events = outboxEventRepository.findAll();
        assertThat(events)
                .filteredOn(event -> "HOLD_REQUESTED".equals(event.getEventType()))
                .anySatisfy(event -> {
                    assertThat(event.getAggregateType()).isEqualTo("HOLD_REQUEST");
                    assertThat(event.getAggregateId()).isEqualTo(holdRequestId);
                    assertThat(event.getTopic()).isEqualTo("reserveflow.hold-events");
                    assertThat(event.getPayload()).contains(holdRequestId.toString());
                });
    }

    /**
     * 존재하지 않는 booking slot 식별자로 요청하면 404 SLOT_001 오류를 반환하는지 검증한다.
     */
    @Test
    void createReturnsNotFoundForUnknownSlot() throws Exception {
        mockMvc.perform(post("/api/v1/hold-requests")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingSlotId": "%s",
                                  "partySize": 2
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("SLOT_001"));
    }

    /**
     * 인원수가 1 미만이면 400 VALIDATION_004 오류를 반환하는지 검증한다.
     */
    @Test
    void createRejectsInvalidPartySize() throws Exception {
        mockMvc.perform(post("/api/v1/hold-requests")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingSlotId": "%s",
                                  "partySize": 0
                                }
                                """.formatted(slot.getPublicId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_004"));
    }

    /**
     * Bearer access token 없이 요청하면 401 AUTH_001 오류를 반환하는지 검증한다.
     */
    @Test
    void createRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/hold-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingSlotId": "%s",
                                  "partySize": 2
                                }
                                """.formatted(slot.getPublicId())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("AUTH_001"));
    }

    private String signupAndLogin(String authSubject) throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authSubject": "%s",
                                  "password": "password123!",
                                  "displayName": "Hold User"
                                }
                                """.formatted(authSubject)))
                .andExpect(status().isCreated());

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authSubject": "%s",
                                  "password": "password123!"
                                }
                                """.formatted(authSubject)))
                .andExpect(status().isOk())
                .andReturn();

        return readJson(login, "$.accessToken");
    }

    private static String readJson(MvcResult result, String expression) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(UTF_8), expression);
    }
}
