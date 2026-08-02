package com.reserveflow.hold.service;

import com.reserveflow.bookingslot.entity.BookingSlot;
import com.reserveflow.bookingslot.repository.BookingSlotRepository;
import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import com.reserveflow.hold.dto.HoldRequestCreateRequest;
import com.reserveflow.hold.dto.HoldRequestResponse;
import com.reserveflow.hold.entity.HoldRequest;
import com.reserveflow.hold.repository.HoldRequestRepository;
import com.reserveflow.member.entity.Member;
import com.reserveflow.member.repository.MemberRepository;
import com.reserveflow.outbox.service.OutboxEventRecorder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비동기 Hold 생성 요청 접수를 담당하는 서비스.
 *
 * 요청한 회원과 예약 희망 slot을 확인한 뒤 HoldRequest를 PENDING으로 저장하고,
 * 같은 트랜잭션에 HOLD_REQUESTED Outbox 이벤트를 함께 기록한다.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HoldRequestService {

    private final MemberRepository memberRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final HoldRequestRepository holdRequestRepository;
    private final OutboxEventRecorder outboxEventRecorder;

    /**
     * Hold 생성 요청을 접수하고 HOLD_REQUESTED 이벤트를 발행한다.
     *
     * @param authSubject 요청한 회원의 JWT subject
     * @param request     예약 희망 booking slot과 인원수
     * @return 즉시 반환할 PENDING 상태의 접수 결과
     */
    @Transactional
    public HoldRequestResponse create(String authSubject, HoldRequestCreateRequest request) {
        // 1. 요청한 회원 확인 (없으면 401 AUTH_INVALID 예외 발생)
        Member member = memberRepository.findByAuthSubjectAndStatus(authSubject, "ACTIVE")
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_INVALID));

        // 2. 예약 희망 booking slot 존재 확인 (없으면 404 SLOT_001 예외 발생)
        BookingSlot slot = bookingSlotRepository.findByPublicId(request.bookingSlotId())
                .orElseThrow(() -> new ApiException(ErrorCode.SLOT_NOT_FOUND));

        // 3. HoldRequest를 PENDING 상태로 저장
        HoldRequest holdRequest = HoldRequest.builder()
                .memberId(member.getId())
                .bookingSlotId(slot.getId())
                .partySize(request.partySize())
                .build();
        holdRequestRepository.save(holdRequest);

        // 4. 같은 트랜잭션에 HOLD_REQUESTED Outbox 이벤트 저장
        outboxEventRecorder.record(
                "HOLD_REQUEST",
                holdRequest.getPublicId(),
                "HOLD_REQUESTED",
                "reserveflow.hold-events",
                holdRequest.getPublicId().toString(),
                1,
                """
                        {
                          "holdRequestId": "%s",
                          "bookingSlotId": "%s",
                          "partySize": %d
                        }
                        """.formatted(holdRequest.getPublicId(), request.bookingSlotId(), request.partySize())
        );

        return new HoldRequestResponse(holdRequest.getPublicId(), holdRequest.getStatus());
    }
}
