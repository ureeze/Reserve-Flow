package com.reserveflow.auth.controller;

import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;
import org.springframework.test.web.servlet.MvcResult;

final class JsonPathReader {

	private JsonPathReader() {
	}

	static String read(MvcResult result, String expression) throws Exception {
		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		return JsonPath.read(content, expression);
	}
}
