package com.reserveflow.hold.entity;

/**
 * Hold 생성 요청(HoldRequest)의 수명 주기 상태.
 *
 * API 접수 직후에는 PENDING으로 시작하고,
 * 후속 Worker가 처리하면서 ACTIVE / FAILED / EXPIRED로 전이된다.
 */
public enum HoldRequestStatus {
    PENDING,
    ACTIVE,
    FAILED,
    EXPIRED
}
