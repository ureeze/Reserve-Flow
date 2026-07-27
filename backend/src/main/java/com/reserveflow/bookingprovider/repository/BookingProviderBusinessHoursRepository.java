package com.reserveflow.bookingprovider.repository;

import com.reserveflow.bookingprovider.entity.BookingProviderBusinessHours;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 예약 제공자의 요일별 운영시간 조회를 담당하는 Repository.
 */
public interface BookingProviderBusinessHoursRepository extends JpaRepository<BookingProviderBusinessHours, Long> {

    /**
     * 특정 예약 제공자의 특정 요일 운영 구간을 모두 조회한다.
     *
     * 예약 제공자 참조는 내부 PK(bigint)를 사용한다.
     */
    List<BookingProviderBusinessHours> findByBookingProviderIdAndDayOfWeek(Long bookingProviderId, short dayOfWeek);
}
