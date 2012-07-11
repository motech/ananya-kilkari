package org.motechproject.ananya.kilkari.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileOBDGatewayTest {

    @Mock
    private HttpClient httpClient;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldPostTheMessagesFileToOnMobile() throws IOException {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(201);
        when(statusLine.getReasonPhrase()).thenReturn("created");

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);

        OnMobileOBDGateway onMobileOBDGateway = new OnMobileOBDGateway(httpClient);

        String expectedContent = "expectedContent";

        onMobileOBDGateway.send(expectedContent);

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(httpClient).execute(captor.capture());
        HttpPost httpPost = (HttpPost) captor.getValue();
        MultipartEntity multipartEntity = (MultipartEntity) httpPost.getEntity();

        String actualContent = readRequest(multipartEntity);

        assertTrue(actualContent.contains(expectedContent));
        assertTrue(actualContent.contains("form-data; name=\"file\"; filename=\"file.csv\""));
    }

    private String readRequest(MultipartEntity multipartEntity) throws IOException {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        multipartEntity.writeTo(out);
        out.close();

        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        String line;
        String actualContent = "";
        while((line = bfr.readLine()) != null) {
            actualContent += line;
        }
        return actualContent;
    }
}
