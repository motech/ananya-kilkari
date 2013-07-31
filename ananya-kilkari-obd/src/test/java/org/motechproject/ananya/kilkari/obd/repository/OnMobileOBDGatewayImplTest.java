package org.motechproject.ananya.kilkari.obd.repository;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.obd.domain.MainSubSlot;
import org.motechproject.ananya.kilkari.obd.service.OBDProperties;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileOBDGatewayImplTest {

    @Mock
    private HttpClient httpClient;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OBDProperties obdProperties;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private OnMobileOBDGateway onMobileOBDGateway;

    @Before
    public void setUp() {
        initMocks(this);
        when(obdProperties.getMessageDeliveryBaseUrl()).thenReturn("mybaseurl");
        when(obdProperties.getMessageDeliveryFileName()).thenReturn("myfile.txt");
        when(obdProperties.getMessageDeliveryFile()).thenReturn("myfile");
        when(obdProperties.getFailureReportUrl()).thenReturn("failureUrl");

        onMobileOBDGateway = new OnMobileOBDGatewayImpl(httpClient, obdProperties, restTemplate);
    }

    @Test
    public void shouldPostTheMessagesFileToOnMobileInMainFirstSubSlot() throws IOException {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(statusLine.getReasonPhrase()).thenReturn("created");
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);
        when(obdProperties.getSlotStartTimeFor(MainSubSlot.ONE)).thenReturn("130000");
        when(obdProperties.getSlotEndTimeFor(MainSubSlot.ONE)).thenReturn("160000");
        String expectedContent = "expectedContent";

        onMobileOBDGateway.sendMessages(expectedContent, MainSubSlot.ONE);

        verify(obdProperties).getSlotStartTimeFor(MainSubSlot.ONE);
        verify(obdProperties).getSlotEndTimeFor(MainSubSlot.ONE);

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(httpClient).execute(captor.capture());
        HttpPost httpPost = (HttpPost) captor.getValue();
        MultipartEntity multipartEntity = (MultipartEntity) httpPost.getEntity();

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        String date = dateTimeFormatter.print(DateTime.now());

        assertEquals("mybaseurl", httpPost.getURI().toString());

        String actualContent = readRequest(multipartEntity);
        assertTrue(actualContent.contains(expectedContent));
        assertTrue(actualContent.contains("form-data; name=\"myfile\"; filename=\"myfile.txt\""));
        assertTrue(actualContent.contains(String.format("form-data; name=\"%s\"%s%s", "startDate", date, "130000")));
        assertTrue(actualContent.contains(String.format("form-data; name=\"%s\"%s%s", "endDate", date, "160000")));
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

        onMobileOBDGateway.sendMessages("content", MainSubSlot.ONE);
    }

    private String readRequest(MultipartEntity multipartEntity) throws IOException {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        multipartEntity.writeTo(out);
        out.close();

        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        String line;
        String actualContent = "";
        while ((line = bfr.readLine()) != null) {
            actualContent += line;
        }
        return actualContent;
    }

    @Test
    public void shouldSendInvalidFailureRecordsToObd() {
        InvalidFailedCallReports invalidFailedCallReports = new InvalidFailedCallReports();
        ArrayList<InvalidFailedCallReport> recordObjectFaileds = new ArrayList<>();
        recordObjectFaileds.add(new InvalidFailedCallReport("msisdn1", "subscriptionId1", "description1"));
        recordObjectFaileds.add(new InvalidFailedCallReport("msisdn2", "subscriptionId2", "description2"));
        invalidFailedCallReports.setRecordObjectFaileds(recordObjectFaileds);

        onMobileOBDGateway.sendInvalidFailureRecord(invalidFailedCallReports);

        ArgumentCaptor<String> requestDataCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> entityTypeCaptor = ArgumentCaptor.forClass(Class.class);
        ArgumentCaptor<HashMap> urlVariablesCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(restTemplate).postForEntity(urlCaptor.capture(), requestDataCaptor.capture(), entityTypeCaptor.capture(), urlVariablesCaptor.capture());

        String actualUrl = urlCaptor.getValue();
        String expectedUrl = String.format("%s?%s=%s", "failureUrl", "msisdn", "{msisdn}");
        HashMap urlVariables = urlVariablesCaptor.getValue();

        assertEquals("[{\"subscriptionId\":\"subscriptionId1\",\"description\":\"description1\",\"mdn\":\"msisdn1\"},{\"subscriptionId\":\"subscriptionId2\",\"description\":\"description2\",\"mdn\":\"msisdn2\"}]", urlVariables.get("msisdn"));
        assertEquals(expectedUrl, actualUrl);
        assertEquals("", requestDataCaptor.getValue());
    }
}