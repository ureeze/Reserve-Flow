package com.reserveflow.bookingprovider.repository;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 예약 제공자 조회를 담당하는 Repository.
 */
public interface BookingProviderRepository extends JpaRepository<BookingProvider, UUID> {
}
