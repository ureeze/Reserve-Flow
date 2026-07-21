package com.reserveflow.reservationrequest.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 예약 요청 해석 모듈이 Python LLM 서비스에 연결하기 위한 설정이다.
 */
@Configuration
@EnableConfigurationProperties(LlmProperties.class)
public class ReservationRequestConfiguration {

    /**
     * Python FastAPI 서비스 전용 HTTP 클라이언트를 생성한다.
     */
    @Bean
    RestClient llmServiceRestClient(LlmProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        // LLM 서비스 연결과 응답 대기에 같은 timeout 설정을 적용한다.
        Duration timeout = properties.timeout();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        // LLM 서비스 baseUrl을 기본 주소로 사용하는 전용 RestClient를 만든다.
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
