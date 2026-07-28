package com.reserveflow.bookingprovider.repository;

import com.reserveflow.bookingprovider.entity.BookingProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 예약 제공자 조회를 담당하는 Repository.
 *
 * 내부 조인은 {@code Long id}로, 외부 API가 전달하는 식별자 조회는 {@code public_id}(UUID)로 처리한다.
 * 동적 필터 검색은 {@link BookingProviderRepositoryCustom}(QueryDSL)에 위임한다.
 */
public interface BookingProviderRepository extends JpaRepository<BookingProvider, Long>, BookingProviderRepositoryCustom {

    /**
     * 외부 노출 식별자(public_id)로 예약 제공자를 조회한다.
     */
    Optional<BookingProvider> findByPublicId(UUID publicId);
}
