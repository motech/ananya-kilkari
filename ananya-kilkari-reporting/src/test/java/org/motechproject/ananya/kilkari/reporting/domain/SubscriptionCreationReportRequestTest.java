package org.motechproject.ananya.kilkari.reporting.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SubscriptionCreationReportRequestTest {

    @Test
    public void shouldCreateASubscriptionReportRequest() {
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails("msisdn", "FIFTEEN_MONTHS", DateTime.now(), "PENDING_ACTIVATION", "subscriptionID");

        DateTime edd = DateTime.now().plusMonths(9);
        DateTime dob = DateTime.now().minusMonths(10);
        String name = "name1";

        SubscriptionCreationReportRequest subscriptionCreationReportRequest = new SubscriptionCreationReportRequest(subscriptionDetails, "CALL_CENTER", 23, name, dob, edd, new SubscriberLocation("district", "block", "panchayat"));
        SubscriberLocation location = subscriptionCreationReportRequest.getLocation();

        assertEquals("msisdn",subscriptionCreationReportRequest.getMsisdn());
        assertEquals("FIFTEEN_MONTHS",subscriptionCreationReportRequest.getPack());
        assertEquals("CALL_CENTER",subscriptionCreationReportRequest.getChannel());
        assertEquals(23,subscriptionCreationReportRequest.getAgeOfBeneficiary());
        assertEquals(dob,subscriptionCreationReportRequest.getDob());
        assertEquals(edd,subscriptionCreationReportRequest.getEdd());
        assertEquals("district", location.getDistrict());
        assertEquals("block", location.getBlock());
        assertEquals("panchayat", location.getPanchayat());
        assertEquals(subscriptionDetails.getCreationDate(),subscriptionCreationReportRequest.getCreatedAt());
        assertEquals(name,subscriptionCreationReportRequest.getName());
        assertEquals("PENDING_ACTIVATION", subscriptionCreationReportRequest.getSubscriptionStatus());
     }
}
