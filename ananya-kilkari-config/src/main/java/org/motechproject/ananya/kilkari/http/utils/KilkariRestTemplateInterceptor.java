package org.motechproject.ananya.kilkari.http.utils;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class KilkariRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse httpResponse = execution.execute(request, body);
        try {
            httpResponse.getStatusCode().value();
        } catch (IllegalArgumentException e) {
            return new BasicClientHttpErrorResponse(getErrorMessage(httpResponse, e.getMessage()), httpResponse);
        }
        return httpResponse;
    }

    private String getErrorMessage(ClientHttpResponse response, String errorMessage) {
        errorMessage = errorMessage.replaceAll("No matching constant for", "Custom HTTP status code received : ");
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) {
                return errorMessage + System.lineSeparator() + FileCopyUtils.copyToString(new InputStreamReader(responseBody));
            }
        } catch (IOException ex) {
        }
        return errorMessage;
    }
}
