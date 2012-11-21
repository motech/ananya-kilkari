package org.motechproject.ananya.kilkari.sync.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.reports.kilkari.contract.LocationStatus;
import org.motechproject.ananya.reports.kilkari.contract.request.LocationSyncRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberLocation;
import org.motechproject.http.client.service.HttpClientService;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationSyncServiceTest {

    @Mock
    HttpClientService httpClientService;
    @Mock
    private Properties clientServiceProperties;
    private LocationSyncService locationSyncService;

    @Before
    public void setup() {
        initMocks(this);
        locationSyncService = new LocationSyncService(httpClientService, clientServiceProperties);
    }

    @Test
    public void shouldSendSyncRequestToURL() {
        SubscriberLocation location = new SubscriberLocation("district", "block", "panchayat");
        String expectedUrl = "url";
        when(clientServiceProperties.get("location.sync.url.reference.data")).thenReturn(expectedUrl);

        locationSyncService.sync(location);

        ArgumentCaptor<LocationSyncRequest> requestArgumentCaptor = ArgumentCaptor.forClass(LocationSyncRequest.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService).post(urlCaptor.capture(), requestArgumentCaptor.capture());

        assertEquals(expectedUrl, urlCaptor.getValue());

        LocationSyncRequest request = requestArgumentCaptor.getValue();
        assertEquals(location.getDistrict(), request.getActualLocation().getDistrict());
        assertEquals(location.getBlock(), request.getActualLocation().getBlock());
        assertEquals(location.getPanchayat(), request.getActualLocation().getPanchayat());
        assertEquals(LocationStatus.NOT_VERIFIED.name(), request.getLocationStatus());
        assertNull(request.getNewLocation());
        assertNull(request.getLastModifiedTime());
    }

    @Test
    public void shouldNotSyncIfLocationIsNull() {
        locationSyncService.sync(null);

        verify(httpClientService, never()).post(anyString(),any(LocationSyncRequest.class));
    }
}
