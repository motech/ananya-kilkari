package org.motechproject.ananya.kilkari.subscription.service.mapper;


import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberCareReportRequest;

import static junit.framework.Assert.assertEquals;

public class SubscriberCareReportRequestMapperTest {

    @Test
    public void shouldmapFromSubscriberCareRequestToReportRequest() {
        String msisdn = "1234567890";
        SubscriberCareReasons reason = SubscriberCareReasons.HELP;
        String channel = "ivr";
        DateTime createdAt = DateTime.now().minusSeconds(42);
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason.name(), channel, createdAt);

        SubscriberCareReportRequest subscriberCareReportRequest = SubscriberCareReportRequestMapper.map(subscriberCareRequest);

        assertEquals(msisdn, subscriberCareReportRequest.getMsisdn());
        assertEquals(reason.name(), subscriberCareReportRequest.getReason());
        assertEquals(channel, subscriberCareReportRequest.getChannel());
        assertEquals(createdAt, subscriberCareReportRequest.getCreatedAt());
    }
}
