package org.motechproject.ananya.kilkari.sync.mapper;

import org.junit.Test;
import org.motechproject.ananya.reports.kilkari.contract.LocationStatus;
import org.motechproject.ananya.reports.kilkari.contract.request.LocationSyncRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberLocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocationSyncRequestMapperTest {
    @Test
    public void shouldCreateALocationSyncRequest(){
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String status = LocationStatus.NOT_VERIFIED.name();

        LocationSyncRequest syncRequest = LocationSyncRequestMapper.map(new SubscriberLocation(district, block, panchayat));

        assertEquals(district, syncRequest.getActualLocation().getDistrict());
        assertEquals(block, syncRequest.getActualLocation().getBlock());
        assertEquals(panchayat, syncRequest.getActualLocation().getPanchayat());
        assertEquals(status, syncRequest.getLocationStatus());
        assertNull(syncRequest.getNewLocation());
        assertNull(syncRequest.getLastModifiedTime());
    }
}
