package com.homework.transaction.integration;

import com.homework.transaction.exception.ExternalLoggingException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalLoggingClient {

    private final RestClient restClient;
    private final ExternalLoggingProperties properties;

    public ExternalLoggingClient(RestClient externalLoggingRestClient, ExternalLoggingProperties properties) {
        this.restClient = externalLoggingRestClient;
        this.properties = properties;
    }

    public void logBeforeDebit(String transactionId) {
        String url = properties.baseUrl() + "/status/" + properties.status();
        try {
            restClient.put()
                    .uri(url)
                    .header("transaction-id", transactionId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new ExternalLoggingException("External logging call to " + url + " failed", e);
        }
    }
}
