package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.reporting.domain.*;

public interface ReportingGateway {
    String GET_LOCATION_PATH = "location";
    SubscriberLocation getLocation(String district, String block, String panchayat);
}
