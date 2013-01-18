package org.motechproject.ananya.kilkari.sync.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.http.client.service.HttpClientService;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RefdataSyncServiceTest {

    @Mock
    HttpClientService httpClientService;
    @Mock
    private Properties refdataProperties;
    @Captor
    private ArgumentCaptor<Map<String, String>> headerCaptor;

    @Test
    public void shouldSendSyncRequestToAppropriateURLWithAPIKeyHeaders() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String expectedUrl = "url";
        String expectedAPIKeyName = "APIKey";
        String expectedAPIKeyValue = "1234";
        when(refdataProperties.getProperty("new.location.sync.url")).thenReturn(expectedUrl);
        when(refdataProperties.getProperty("api.key.name")).thenReturn(expectedAPIKeyName);
        when(refdataProperties.getProperty("api.key.value")).thenReturn(expectedAPIKeyValue);

        RefdataSyncService refdataSyncService = new RefdataSyncService(httpClientService, refdataProperties);
        refdataSyncService.syncNewLocation(district, block, panchayat);

        ArgumentCaptor<NewLocationSyncRequest> requestArgumentCaptor = ArgumentCaptor.forClass(NewLocationSyncRequest.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService).post(urlCaptor.capture(), requestArgumentCaptor.capture(), headerCaptor.capture());

        assertEquals(expectedUrl, urlCaptor.getValue());

        NewLocationSyncRequest request = requestArgumentCaptor.getValue();
        assertEquals(district, request.getDistrict());
        assertEquals(block, request.getBlock());
        assertEquals(panchayat, request.getPanchayat());

        Map<String, String> actualHeaders = headerCaptor.getValue();
        assertEquals(1, actualHeaders.size());
        assertEquals(expectedAPIKeyValue, actualHeaders.get(expectedAPIKeyName));
    }

    @Test
    public void shouldSendSyncRequestToAppropriateURLWithoutAPIKeyHeaders() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String expectedUrl = "url";
        String expectedAPIKeyValue = "1234";
        when(refdataProperties.getProperty("new.location.sync.url")).thenReturn(expectedUrl);
        when(refdataProperties.getProperty("api.key.value")).thenReturn(expectedAPIKeyValue);

        RefdataSyncService refdataSyncService = new RefdataSyncService(httpClientService, refdataProperties);
        refdataSyncService.syncNewLocation(district, block, panchayat);

        verify(httpClientService).post(anyString(), any(NewLocationSyncRequest.class), headerCaptor.capture());

        Map<String, String> actualHeaders = headerCaptor.getValue();
        assertTrue(actualHeaders.isEmpty());
    }
}
