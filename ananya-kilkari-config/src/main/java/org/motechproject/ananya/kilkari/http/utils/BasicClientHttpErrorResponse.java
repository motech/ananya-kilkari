package org.motechproject.ananya.kilkari.http.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;

public class BasicClientHttpErrorResponse implements ClientHttpResponse {
    private String statusText;
    private ClientHttpResponse baseResponse;

    public BasicClientHttpErrorResponse(String statusText, ClientHttpResponse baseResponse) {
        this.statusText = statusText;
        this.baseResponse = baseResponse;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getStatusText() throws IOException {
        return statusText + baseResponse.getStatusText();
    }

    @Override
    public void close() {
    }

    @Override
    public InputStream getBody() throws IOException {
        return baseResponse.getBody();
    }

    @Override
    public HttpHeaders getHeaders() {
        return baseResponse.getHeaders();
    }
}
