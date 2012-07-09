package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubscriberCareRequestMapperTest {
    @Test
    public void shouldMapSubscriberCareRequestToSubscriberCareDoc() {
        String msisdn = "1234567890";
        String reason = SubscriberCareReasons.HELP.name();
        String channel = "ivr";
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason, channel);

        SubscriberCareDoc subscriberCareDoc = SubscriberCareRequestMapper.map(subscriberCareRequest);

        assertEquals(msisdn, subscriberCareDoc.getMsisdn());
        assertEquals(reason, subscriberCareDoc.getReason());
        assertEquals(Channel.IVR, subscriberCareDoc.getChannel());
        assertEquals(subscriberCareRequest.getCreatedAt(), subscriberCareDoc.getCreatedAt());
    }
}
