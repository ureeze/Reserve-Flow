package com.reserveflow.outbox.entity;

/**
 * Outbox 이벤트의 Kafka 발행 처리 상태.
 */
public enum OutboxEventStatus {

	/**
	 * 아직 발행되지 않았고 Relay가 가져갈 수 있는 상태.
	 */
	PENDING,

	/**
	 * Kafka 발행이 완료된 상태.
	 */
	PUBLISHED,

	/**
	 * 발행 중 오류가 발생해 재시도 대상이 된 상태.
	 */
	FAILED
}
