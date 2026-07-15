package com.reserveflow.outbox.repository;

import com.reserveflow.outbox.entity.OutboxEvent;
import com.reserveflow.outbox.entity.OutboxEventStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Outbox 이벤트 저장과 Relay 발행 대상 조회를 담당하는 Repository.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

	/**
	 * 발행 가능한 시각이 지난 PENDING/FAILED 이벤트를 생성 순서대로 조회한다.
	 */
	List<OutboxEvent> findByStatusInAndAvailableAtLessThanEqualOrderByCreatedAtAsc(
			Collection<OutboxEventStatus> statuses,
			Instant availableAt,
			Pageable pageable
	);
}
