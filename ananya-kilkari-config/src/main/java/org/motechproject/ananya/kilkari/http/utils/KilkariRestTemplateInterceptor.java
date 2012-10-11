package org.motechproject.ananya.kilkari.http.utils;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KilkariRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse httpResponse = execution.execute(request, body);
        try {
            httpResponse.getStatusCode().value();
        } catch (IllegalArgumentException e) {
            return new BasicClientHttpErrorResponse(e.getMessage(), httpResponse);
        }
        return httpResponse;
    }
}
