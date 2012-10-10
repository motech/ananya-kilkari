package org.motechproject.ananya.kilkari.http.utils;

import org.apache.http.HttpRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KilkariRestTemplateInterceptorTest {
    @Mock
    private HttpRequest httpRequest;
    @Mock
    private ClientHttpRequestExecution execution;
    @Mock
    private ClientHttpResponse clientHttpResponse;

    @Test
    public void shouldHandleCustomErrorCodes() throws IOException {
        when(execution.execute(null, null)).thenReturn(new InvalidClientHttpResponse());

        ClientHttpResponse response = new KilkariRestTemplateInterceptor().intercept(null, null, execution);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    public void shouldNotHandleNormalRequests() throws IOException {
        when(execution.execute(null, null)).thenReturn(clientHttpResponse);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);

        ClientHttpResponse response = new KilkariRestTemplateInterceptor().intercept(null, null, execution);

        assertEquals(200, response.getStatusCode().value());
    }
}

class InvalidClientHttpResponse implements ClientHttpResponse {
    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(602);
    }

    @Override
    public String getStatusText() throws IOException {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public InputStream getBody() throws IOException {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }
}