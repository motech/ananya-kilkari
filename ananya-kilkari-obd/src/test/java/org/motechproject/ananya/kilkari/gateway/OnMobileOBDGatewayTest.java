package org.motechproject.ananya.kilkari.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileOBDGatewayTest {

    @Mock
    private HttpClient httpClient;

    private Properties obdProperties;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private OnMobileOBDGateway onMobileOBDGateway;

    @Before
    public void setUp() {
        initMocks(this);
        obdProperties = new Properties();
        obdProperties.put("obd.message.delivery.url", "myurl");
        obdProperties.put("obd.message.delivery.filename", "myfile.txt");
        obdProperties.put("obd.message.delivery.file", "myfile");

        onMobileOBDGateway = new OnMobileOBDGateway(httpClient, obdProperties);
    }

    @Test
    public void shouldPostTheMessagesFileToOnMobile() throws IOException {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(statusLine.getReasonPhrase()).thenReturn("created");

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);

        String expectedContent = "expectedContent";

        onMobileOBDGateway.send(expectedContent);

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(httpClient).execute(captor.capture());
        HttpPost httpPost = (HttpPost) captor.getValue();
        MultipartEntity multipartEntity = (MultipartEntity) httpPost.getEntity();

        assertEquals("myurl", httpPost.getURI().toString());

        String actualContent = readRequest(multipartEntity);

        assertTrue(actualContent.contains(expectedContent));
        assertTrue(actualContent.contains("form-data; name=\"myfile\"; filename=\"myfile.txt\""));
    }

    @Test
    public void shouldThrowExceptionForAFailureResponse() throws IOException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Sending messages to OBD failed with code: 500, reason: failed");

        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(500);
        when(statusLine.getReasonPhrase()).thenReturn("failed");

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);

        onMobileOBDGateway.send("content");
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
