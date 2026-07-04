package com.homework.transaction.infrastructure.external;

import com.homework.transaction.port.ExternalLoggingException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HttpExternalLoggingAdapterTest {

    private final ExternalLoggingProperties properties =
            new ExternalLoggingProperties("https://httpbin.org", 200, Duration.ofSeconds(2), Duration.ofSeconds(3));

    @Test
    void putsWithIdempotencyKeyAndSucceedsOnTwoHundred() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://httpbin.org/status/200"))
                .andExpect(method(PUT))
                .andExpect(header("Idempotency-Key", "TXN0000000001"))
                .andRespond(withSuccess());

        HttpExternalLoggingAdapter adapter = new HttpExternalLoggingAdapter(builder.build(), properties);
        adapter.logBeforeDebit("TXN0000000001");

        server.verify();
    }

    @Test
    void throwsOnServerError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://httpbin.org/status/200")).andRespond(withServerError());

        HttpExternalLoggingAdapter adapter = new HttpExternalLoggingAdapter(builder.build(), properties);
        assertThrows(ExternalLoggingException.class, () -> adapter.logBeforeDebit("TXN0000000002"));
    }
}
