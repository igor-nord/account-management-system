package com.homework.transaction.infrastructure.external;

import com.homework.transaction.port.ExternalLoggingException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HttpStatLoggingAdapterTest {

    private final HttpStatProperties properties =
            new HttpStatProperties("https://httpstat.us", 200, Duration.ofSeconds(2), Duration.ofSeconds(3));

    @Test
    void succeedsOnTwoHundred() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://httpstat.us/200")).andExpect(method(GET)).andRespond(withSuccess());

        HttpStatLoggingAdapter adapter = new HttpStatLoggingAdapter(builder.build(), properties);
        adapter.logBeforeDebit();

        server.verify();
    }

    @Test
    void throwsOnServerError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://httpstat.us/200")).andRespond(withServerError());

        HttpStatLoggingAdapter adapter = new HttpStatLoggingAdapter(builder.build(), properties);
        assertThrows(ExternalLoggingException.class, adapter::logBeforeDebit);
    }
}
