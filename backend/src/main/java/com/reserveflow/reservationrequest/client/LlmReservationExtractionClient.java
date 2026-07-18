package com.reserveflow.reservationrequest.client;

import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Python FastAPI LLM 서비스에 자연어 예약 해석을 요청한다.
 */
@RequiredArgsConstructor
@Component
public class LlmReservationExtractionClient {

    private final RestClient llmServiceRestClient;

    /**
     * 서버 기준 날짜와 시간대를 포함해 Python LLM 서비스에 해석을 요청한다.
     */
    public LlmReservationExtractionResult extract(
            String reservationMessage,
            LocalDate referenceDate,
            String timezone
    ) {
        try {
            // 자연어 메시지와 기준 날짜/시간대를 Python LLM 서비스에 JSON으로 전달한다.
            LlmReservationExtractionResult result = llmServiceRestClient.post()
                    .uri("/v1/extractors/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LlmReservationExtractionRequest(reservationMessage, referenceDate, timezone))
                    .retrieve()
                    .onStatus(status -> status.isError(), (request, response) -> {
                        // LLM 서비스가 오류 응답을 주면 서비스 사용 불가로 통일한다.
                        throw new ApiException(ErrorCode.LLM_UNAVAILABLE);
                    })
                    .body(LlmReservationExtractionResult.class);

            // 성공 응답이어도 body가 없으면 정상 해석 결과로 볼 수 없다.
            if (result == null) {
                throw new ApiException(ErrorCode.LLM_UNAVAILABLE);
            }
            return result;
        } catch (ApiException exception) {
            // 이미 서비스 예외로 변환된 경우에는 그대로 전파한다.
            throw exception;
        } catch (RestClientException exception) {
            // 연결 실패, 타임아웃, 응답 변환 실패 같은 HTTP 클라이언트 오류를 통일한다.
            throw new ApiException(ErrorCode.LLM_UNAVAILABLE);
        }
    }

    /** Python 서비스 요청 형식이다. */
    private record LlmReservationExtractionRequest(
            String reservationMessage,
            LocalDate referenceDate,
            String timezone
    ) {
    }

    /** Python 서비스가 반환하는 해석 결과 형식이다. */
    public record LlmReservationExtractionResult(
            String reservationDate,
            String reservationTime,
            Integer partySize,
            String location,
            String providerType,
            List<String> missingFields
    ) {
    }
}
