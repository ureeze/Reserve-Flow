package com.reserveflow.bookingprovider.entity;

import com.reserveflow.reservationrequest.dto.BookingProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 식당, 영화관, 병원 등 예약 가능한 대상의 공통 운영 정보를 저장하는 Entity.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "booking_providers")
public class BookingProvider {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 20)
    private BookingProviderType providerType;

    @Column(name = "location_text", nullable = false)
    private String locationText;

    /**
     * 예약 제공자의 현지 timezone. IANA timezone 문자열을 사용한다.
     */
    @Builder.Default
    @Column(nullable = false)
    private String timezone = "Asia/Seoul";

    /**
     * 예약 제공자별 최대 예약 인원. 요청 인원은 이 값을 초과할 수 없다.
     */
    @Column(name = "max_party_size", nullable = false)
    private int maxPartySize;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingProviderStatus status = BookingProviderStatus.ACTIVE;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
