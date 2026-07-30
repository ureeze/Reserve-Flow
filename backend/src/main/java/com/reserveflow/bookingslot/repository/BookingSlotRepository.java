package com.reserveflow.bookingslot.repository;

import com.reserveflow.bookingslot.entity.BookingSlot;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    /**
     * 외부 노출 식별자(public_id)로 booking slot을 조회한다.
     */
    Optional<BookingSlot> findByPublicId(UUID publicId);

    /**
     * 예약 제공자 외부 식별자(UUID)와 시간 범위를 기준으로 예약 가능 슬롯 목록을 시간순으로 조회한다.
     */
    List<BookingSlot> findByBookingProvider_PublicIdAndStartsAtBetweenOrderByStartsAtAsc(
            UUID providerPublicId, Instant fromStartsAt, Instant toStartsAt
    );

    /**
     * 예약 제공자 내부 ID(Long)와 시간 범위를 기준으로 예약 가능 슬롯 목록을 시간순으로 조회한다.
     */
    List<BookingSlot> findByBookingProvider_IdAndStartsAtBetweenOrderByStartsAtAsc(
            Long providerId, Instant fromStartsAt, Instant toStartsAt
    );
}
