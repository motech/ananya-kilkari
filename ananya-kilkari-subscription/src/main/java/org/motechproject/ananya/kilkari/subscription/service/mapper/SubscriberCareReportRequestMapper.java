package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberCareReportRequest;

public class SubscriberCareReportRequestMapper {

    public static SubscriberCareReportRequest map(SubscriberCareRequest subscriberCareRequest) {
        return new SubscriberCareReportRequest(subscriberCareRequest.getMsisdn(), subscriberCareRequest.getReason(), subscriberCareRequest.getChannel(), subscriberCareRequest.getCreatedAt());
    }
}
