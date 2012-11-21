package org.motechproject.ananya.kilkari.sync.mapper;

import org.motechproject.ananya.reports.kilkari.contract.LocationStatus;
import org.motechproject.ananya.reports.kilkari.contract.request.LocationRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.LocationSyncRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberLocation;

public class LocationSyncRequestMapper {
    public static LocationSyncRequest map(SubscriberLocation location) {
        LocationRequest actualLocation = new LocationRequest(location.getDistrict(), location.getBlock(), location.getPanchayat());
        return new LocationSyncRequest(actualLocation, null, LocationStatus.NOT_VERIFIED.name(), null);
    }
}
