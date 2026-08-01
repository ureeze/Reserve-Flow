package com.reserveflow.hold.repository;

import com.reserveflow.hold.entity.HoldRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Hold 생성 요청 Entity 조회와 저장을 담당하는 Repository.
 */
public interface HoldRequestRepository extends JpaRepository<HoldRequest, Long> {

    /**
     * 외부 노출 식별자(public_id)로 Hold 생성 요청을 조회한다.
     */
    Optional<HoldRequest> findByPublicId(UUID publicId);
}
