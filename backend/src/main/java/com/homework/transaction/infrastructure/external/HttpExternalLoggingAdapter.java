package com.homework.transaction.infrastructure.external;

import com.homework.transaction.port.ExternalLoggingException;
import com.homework.transaction.port.ExternalLoggingPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HttpExternalLoggingAdapter implements ExternalLoggingPort {

    private final RestClient restClient;
    private final ExternalLoggingProperties properties;

    public HttpExternalLoggingAdapter(RestClient externalLoggingRestClient, ExternalLoggingProperties properties) {
        this.restClient = externalLoggingRestClient;
        this.properties = properties;
    }

    @Override
    public void logBeforeDebit(String transactionId) {
        String url = properties.baseUrl() + "/status/" + properties.status();
        try {
            restClient.put()
                    .uri(url)
                    .header("Idempotency-Key", transactionId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new ExternalLoggingException("External logging call to " + url + " failed", e);
        }
    }
}
