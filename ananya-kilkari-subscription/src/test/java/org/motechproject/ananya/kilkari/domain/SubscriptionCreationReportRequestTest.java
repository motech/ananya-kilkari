package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SubscriptionCreationReportRequestTest {

    @Test
    public void shouldCreateASubscriptionReportRequest() {
        Subscription subscription = new Subscription("msisdn", SubscriptionPack.FIFTEEN_MONTHS);
        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);

        DateTime edd = DateTime.now().plusMonths(9);
        DateTime dob = DateTime.now().minusMonths(10);
        String name = "name1";

        SubscriptionCreationReportRequest subscriptionCreationReportRequest = new SubscriptionCreationReportRequest(subscription, Channel.CALL_CENTER, 23, name, dob, edd, new SubscriberLocation("district", "block", "panchayat"));
        SubscriberLocation location = subscriptionCreationReportRequest.getLocation();

        assertEquals("msisdn",subscriptionCreationReportRequest.getMsisdn());
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS,subscriptionCreationReportRequest.getPack());
        assertEquals(Channel.CALL_CENTER,subscriptionCreationReportRequest.getChannel());
        assertEquals(23,subscriptionCreationReportRequest.getAgeOfBeneficiary());
        assertEquals(dob,subscriptionCreationReportRequest.getDob());
        assertEquals(edd,subscriptionCreationReportRequest.getEdd());
        assertEquals("district", location.getDistrict());
        assertEquals("block", location.getBlock());
        assertEquals("panchayat", location.getPanchayat());
        assertEquals(subscription.getCreationDate(),subscriptionCreationReportRequest.getCreatedAt());
        assertEquals(name,subscriptionCreationReportRequest.getName());
        assertEquals(SubscriptionStatus.PENDING_ACTIVATION, subscriptionCreationReportRequest.getSubscriptionStatus());
     }
}
