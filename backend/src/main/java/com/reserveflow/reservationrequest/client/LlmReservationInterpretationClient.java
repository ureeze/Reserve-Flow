package com.reserveflow.reservationrequest.client;

import com.reserveflow.common.error.ApiException;
import com.reserveflow.common.error.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Python FastAPI LLM 서비스에 자연어 예약 해석을 요청한다.
 */
@Component
public class LlmReservationInterpretationClient {

    private final RestClient llmServiceRestClient;

    public LlmReservationInterpretationClient(RestClient llmServiceRestClient) {
        this.llmServiceRestClient = llmServiceRestClient;
    }

    /**
     * 서버 기준 날짜와 시간대를 포함해 Python LLM 서비스에 해석을 요청한다.
     */
    public LlmReservationInterpretationResult interpret(
            String reservationMessage,
            LocalDate referenceDate,
            String timezone
    ) {
        try {
            LlmReservationInterpretationResult result = llmServiceRestClient.post()
                    .uri("/v1/interpreters/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LlmReservationInterpretationRequest(reservationMessage, referenceDate, timezone))
                    .retrieve()
                    .onStatus(status -> status.isError(), (request, response) -> {
                        throw new ApiException(ErrorCode.LLM_UNAVAILABLE);
                    })
                    .body(LlmReservationInterpretationResult.class);

            if (result == null) {
                throw new ApiException(ErrorCode.LLM_UNAVAILABLE);
            }
            return result;
        } catch (ApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ApiException(ErrorCode.LLM_UNAVAILABLE);
        }
    }

    /** Python 서비스 요청 형식이다. */
    private record LlmReservationInterpretationRequest(
            String reservationMessage,
            LocalDate referenceDate,
            String timezone
    ) {
    }

    /** Python 서비스가 반환하는 해석 결과 형식이다. */
    public record LlmReservationInterpretationResult(
            String reservationDate,
            String reservationTime,
            Integer partySize,
            String location,
            String providerType,
            Double confidence,
            List<String> missingFields
    ) {
    }
}
