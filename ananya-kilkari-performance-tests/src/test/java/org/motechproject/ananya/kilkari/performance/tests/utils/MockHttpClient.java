package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Component
public class MockHttpClient implements HttpClient {
    @Override
    public HttpParams getParams() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException {
        HttpResponse mockHttpResponse = mock(HttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockHttpEntity = mock(HttpEntity.class);

        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream("ok".getBytes()));

        return mockHttpResponse;
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new RuntimeException("Not implemented");
    }
}
