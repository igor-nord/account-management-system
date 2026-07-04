package com.homework.transaction.infrastructure.external;

import com.homework.transaction.port.ExternalLoggingException;
import com.homework.transaction.port.ExternalLoggingPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HttpStatLoggingAdapter implements ExternalLoggingPort {

    private final RestClient restClient;
    private final HttpStatProperties properties;

    public HttpStatLoggingAdapter(RestClient httpStatRestClient, HttpStatProperties properties) {
        this.restClient = httpStatRestClient;
        this.properties = properties;
    }

    @Override
    public void logBeforeDebit() {
        String url = properties.baseUrl() + "/" + properties.status();
        try {
            restClient.get().uri(url).retrieve().toBodilessEntity();
        } catch (RestClientException e) {
            throw new ExternalLoggingException("External logging call to " + url + " failed", e);
        }
    }
}
