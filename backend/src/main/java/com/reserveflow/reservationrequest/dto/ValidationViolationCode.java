package com.reserveflow.reservationrequest.dto;

/**
 * 예약 조건 검증 API 명세서의 violations.code 값이다.
 *
 * VALIDATION_004(필수 값 누락)는 Bean Validation 실패로 별도 400 응답을 반환하므로 여기에 포함하지 않는다.
 */
public enum ValidationViolationCode {

    PAST_DATETIME("VALIDATION_001", "과거 날짜 또는 시간입니다."),
    PARTY_SIZE_EXCEEDED("VALIDATION_002", "허용 범위를 벗어난 인원수입니다."),
    OUTSIDE_BUSINESS_HOURS("VALIDATION_003", "영업시간 외 요청입니다.");

    private final String code;
    private final String message;

    ValidationViolationCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
