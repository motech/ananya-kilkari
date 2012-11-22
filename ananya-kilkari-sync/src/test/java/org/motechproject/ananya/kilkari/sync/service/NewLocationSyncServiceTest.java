package org.motechproject.ananya.kilkari.sync.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewLocationSyncServiceTest {

    @Mock
    HttpClientService httpClientService;
    @Mock
    private Properties kilkariProperties;
    private NewLocationSyncService newLocationSyncService;

    @Before
    public void setup() {
        initMocks(this);
        newLocationSyncService = new NewLocationSyncService(httpClientService, kilkariProperties);
    }

    @Test
    public void shouldSendSyncRequestToURL() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String expectedUrl = "url";
        when(kilkariProperties.get("new.location.sync.url")).thenReturn(expectedUrl);

        newLocationSyncService.sync(district, block, panchayat);

        ArgumentCaptor<NewLocationSyncRequest> requestArgumentCaptor = ArgumentCaptor.forClass(NewLocationSyncRequest.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService).post(urlCaptor.capture(), requestArgumentCaptor.capture());

        assertEquals(expectedUrl, urlCaptor.getValue());

        NewLocationSyncRequest request = requestArgumentCaptor.getValue();
        assertEquals(district, request.getDistrict());
        assertEquals(block, request.getBlock());
        assertEquals(panchayat, request.getPanchayat());
    }
}
