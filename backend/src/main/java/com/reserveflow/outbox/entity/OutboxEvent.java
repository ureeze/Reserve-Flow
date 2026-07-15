package com.reserveflow.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 도메인 변경과 함께 저장되는 Transactional Outbox 이벤트 Entity.
 *
 * 실제 Kafka 발행은 Relay가 담당하며, 이 Entity는 발행할 이벤트와 재시도 상태를 DB에 보존한다.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

	/**
	 * Outbox 이벤트 고유 ID.
	 *
	 * Kafka Consumer가 중복 이벤트를 걸러낼 때 eventId로 사용할 수 있다.
	 */
	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();

	/**
	 * 이벤트가 발생한 Aggregate 유형.
	 *
	 * 예: HOLD_REQUEST, RESERVATION, WAITLIST.
	 */
	@Column(name = "aggregate_type", nullable = false, length = 100)
	private String aggregateType;

	/**
	 * 이벤트가 발생한 Aggregate ID.
	 */
	@Column(name = "aggregate_id", nullable = false)
	private UUID aggregateId;

	/**
	 * 발행할 도메인 이벤트 유형.
	 *
	 * 예: HOLD_REQUESTED, RESERVATION_CONFIRMED.
	 */
	@Column(name = "event_type", nullable = false, length = 100)
	private String eventType;

	/**
	 * 이벤트를 발행할 Kafka topic.
	 */
	@Column(nullable = false, length = 200)
	private String topic;

	/**
	 * Kafka partition 선택과 순서 보장을 위해 사용하는 메시지 key.
	 */
	@Column(name = "message_key", nullable = false, length = 200)
	private String messageKey;

	/**
	 * 이벤트 payload 스키마 버전.
	 */
	@Builder.Default
	@Column(name = "schema_version", nullable = false)
	private int schemaVersion = 1;

	/**
	 * Kafka로 발행할 JSON payload.
	 *
	 * 개인정보, 인증정보, 원문 프롬프트, stack trace는 저장하지 않는다.
	 */
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(nullable = false, columnDefinition = "jsonb")
	private String payload;

	/**
	 * 이벤트 발행 처리 상태.
	 */
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OutboxEventStatus status = OutboxEventStatus.PENDING;

	/**
	 * Kafka 발행 실패 후 재시도한 횟수.
	 */
	@Builder.Default
	@Column(name = "retry_count", nullable = false)
	private int retryCount = 0;

	/**
	 * Relay가 이 이벤트를 다시 발행 대상으로 볼 수 있는 시각.
	 */
	@Builder.Default
	@Column(name = "available_at", nullable = false)
	private Instant availableAt = Instant.now();

	/**
	 * Kafka 발행 완료 시각.
	 */
	@Column(name = "published_at")
	private Instant publishedAt;

	/**
	 * 마지막 발행 실패 원인.
	 *
	 * 운영 진단용 비민감 정보만 저장한다.
	 */
	@Column(name = "last_error")
	private String lastError;

	/**
	 * Outbox 이벤트 생성 시각.
	 */
	@Builder.Default
	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	/**
	 * Outbox 이벤트 마지막 수정 시각.
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
