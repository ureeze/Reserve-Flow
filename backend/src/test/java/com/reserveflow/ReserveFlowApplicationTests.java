package com.reserveflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ReserveFlowApplicationTests {

	/**
	 * 테스트 profile로 Spring application context가 정상적으로 로드되는지 검증한다.
	 */
	@Test
	void contextLoads() {
	}

}
