package com.reserveflow.hold.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * 사용자의 예약 희망 조건을 받아 비동기로 Hold를 생성하기 위한 요청 Entity.
 *
 * API 서버는 이 요청을 PENDING 상태로 저장하고, 같은 트랜잭션에 Outbox 이벤트를 함께 기록한다.
 * 이후 Worker가 실제 Hold 생성과 상태 전이를 담당한다.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "hold_requests")
public class HoldRequest {

    /**
     * DB 내부 조인과 영속성 식별에 사용하는 기본키.
     *
     * 외부 API에는 노출하지 않는다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 클라이언트가 Hold 생성 요청을 추적하고 조회할 때 사용하는 외부 식별자.
     */
    @Builder.Default
    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId = UUID.randomUUID();

    /**
     * Hold 생성 요청을 접수한 회원의 내부 ID.
     */
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /**
     * 예약을 희망하는 booking slot의 내부 ID.
     */
    @Column(name = "booking_slot_id", nullable = false)
    private Long bookingSlotId;

    /**
     * 예약 희망 인원수.
     */
    @Column(name = "party_size", nullable = false)
    private int partySize;

    /**
     * Hold 생성 요청 상태.
     *
     * API 접수 직후에는 PENDING이다.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HoldRequestStatus status = HoldRequestStatus.PENDING;

    /**
     * Hold 생성 요청 접수 시각.
     */
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    /**
     * Hold 생성 요청 마지막 수정 시각.
     */
    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /**
     * Entity 수정 시 updated_at 값을 현재 시각으로 갱신한다.
     */
    @PreUpdate
    void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
