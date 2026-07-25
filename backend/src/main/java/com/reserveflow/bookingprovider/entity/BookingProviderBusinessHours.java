package com.reserveflow.bookingprovider.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 제공자의 요일별 운영 구간을 저장하는 Entity.
 *
 * 한 요일에 여러 구간을 등록할 수 있다.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "booking_provider_business_hours")
public class BookingProviderBusinessHours {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "booking_provider_id", nullable = false)
    private UUID bookingProviderId;

    /**
     * 0=일요일 기준 요일. {@link java.time.DayOfWeek}의 1=월요일과 다르므로 변환이 필요하다.
     */
    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

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
