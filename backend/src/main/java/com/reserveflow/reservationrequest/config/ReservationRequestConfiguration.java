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
@EnableConfigurationProperties(LlmServiceProperties.class)
public class ReservationRequestConfiguration {

    /**
     * Python FastAPI 서비스 전용 HTTP 클라이언트를 생성한다.
     */
    @Bean
	RestClient llmServiceRestClient(LlmServiceProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Duration timeout = properties.timeout();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

		return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
