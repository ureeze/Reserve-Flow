package com.reserveflow.outbox.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.reserveflow.outbox.entity.OutboxEvent;
import com.reserveflow.outbox.entity.OutboxEventStatus;
import com.reserveflow.outbox.repository.OutboxEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class OutboxEventAppenderTests {

	@Autowired
	private OutboxEventAppender outboxEventAppender;

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	/**
	 * 도메인 트랜잭션 안에서 Outbox 이벤트를 저장하면 발행 대기 상태로 보존되는지 검증한다.
	 */
	@Test
	@Transactional
	void appendStoresPendingEventInCurrentTransaction() {
		UUID aggregateId = UUID.randomUUID();

		OutboxEvent saved = outboxEventAppender.append(
				"HOLD_REQUEST",
				aggregateId,
				"HOLD_REQUESTED",
				"reserveflow.hold-events",
				aggregateId.toString(),
				1,
				"""
						{
						  "holdRequestId": "%s",
						  "partySize": 2
						}
				""".formatted(aggregateId)
		);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
		assertThat(saved.getRetryCount()).isZero();
		assertThat(saved.getAvailableAt()).isNotNull();
		assertThat(saved.getPublishedAt()).isNull();
		assertThat(saved.getLastError()).isNull();
		assertThat(outboxEventRepository.findById(saved.getId())).isPresent();
	}

	/**
	 * Relay가 발행 가능한 PENDING 이벤트를 생성 순서 기준으로 조회할 수 있는지 검증한다.
	 */
	@Test
	@Transactional
	void repositoryFindsPublishablePendingEvents() {
		UUID aggregateId = UUID.randomUUID();
		OutboxEvent saved = outboxEventAppender.append(
				"HOLD_REQUEST",
				aggregateId,
				"HOLD_REQUESTED",
				"reserveflow.hold-events",
				aggregateId.toString(),
				1,
				"""
						{
						  "holdRequestId": "%s"
						}
				""".formatted(aggregateId)
		);

		assertThat(outboxEventRepository.findByStatusInAndAvailableAtLessThanEqualOrderByCreatedAtAsc(
				List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED),
				Instant.now().plusSeconds(1),
				PageRequest.of(0, 10)
		)).extracting(OutboxEvent::getId).contains(saved.getId());
	}

	/**
	 * Outbox 저장이 도메인 트랜잭션 밖에서 호출되면 실패하는지 검증한다.
	 */
	@Test
	void appendRequiresExistingTransaction() {
		UUID aggregateId = UUID.randomUUID();

		assertThatThrownBy(() -> outboxEventAppender.append(
				"RESERVATION",
				aggregateId,
				"RESERVATION_CONFIRMED",
				"reserveflow.reservation-events",
				aggregateId.toString(),
				1,
				"""
						{
						  "reservationId": "%s",
						  "status": "CONFIRMED"
						}
						""".formatted(aggregateId)
		)).isInstanceOf(IllegalTransactionStateException.class);
	}
}
