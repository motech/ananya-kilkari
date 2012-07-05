package org.motechproject.ananya.kilkari.domain;

public class SubscriberCareRequestMapper {
    public static SubscriberCareDoc map(SubscriberCareRequest subscriberCareRequest) {
        return new SubscriberCareDoc(subscriberCareRequest.getMsisdn(), subscriberCareRequest.getReason());
    }
}
