package com.reserveflow.bookingslot.dto;

import com.reserveflow.bookingslot.entity.BookingSlotStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BookingSlotResponse(
        UUID publicId,
        Instant startsAt,
        Instant endsAt,
        int totalCapacity,
        int availableCapacity,
        BookingSlotStatus status
) {
}
