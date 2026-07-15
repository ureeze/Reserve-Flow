package com.reserveflow.outbox.service;

import com.reserveflow.outbox.entity.OutboxEvent;
import com.reserveflow.outbox.repository.OutboxEventRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 도메인 트랜잭션 안에서 Outbox 이벤트를 함께 저장하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class OutboxEventAppender {

	private final OutboxEventRepository outboxEventRepository;

	/**
	 * 발행 대기 상태의 Outbox 이벤트를 저장한다.
	 *
	 * 반드시 호출한 도메인 서비스의 트랜잭션에 참여해야 하므로, 트랜잭션이 없으면 예외가 발생한다.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public OutboxEvent append(
			String aggregateType,
			UUID aggregateId,
			String eventType,
			String topic,
			String messageKey,
			int schemaVersion,
			String payload
	) {
		OutboxEvent outboxEvent = OutboxEvent.builder()
				.aggregateType(aggregateType)
				.aggregateId(aggregateId)
				.eventType(eventType)
				.topic(topic)
				.messageKey(messageKey)
				.schemaVersion(schemaVersion)
				.payload(payload)
				.build();
		return outboxEventRepository.save(outboxEvent);
	}
}
