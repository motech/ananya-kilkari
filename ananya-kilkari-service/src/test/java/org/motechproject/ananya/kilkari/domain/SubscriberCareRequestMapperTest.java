package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubscriberCareRequestMapperTest {
    @Test
    public void shouldMapSubscriberCareRequestToSubscriberCareDoc() {
        String msisdn = "1234567890";
        String reason = SubscriberCareReasons.CHANGE_PACK.name();
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest();
        subscriberCareRequest.setMsisdn(msisdn);
        subscriberCareRequest.setReason(reason);

        SubscriberCareDoc subscriberCareDoc = SubscriberCareRequestMapper.map(subscriberCareRequest);

        assertEquals(msisdn, subscriberCareDoc.getMsisdn());
        assertEquals(reason, subscriberCareDoc.getReason());
    }
}
