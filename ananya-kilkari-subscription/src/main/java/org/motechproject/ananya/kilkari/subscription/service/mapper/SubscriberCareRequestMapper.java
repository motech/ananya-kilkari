package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;

public class SubscriberCareRequestMapper {
    public static SubscriberCareDoc map(SubscriberCareRequest subscriberCareRequest) {
        return new SubscriberCareDoc(subscriberCareRequest.getMsisdn(), SubscriberCareReasons.getFor(subscriberCareRequest.getReason()),
                subscriberCareRequest.getCreatedAt(), Channel.from(subscriberCareRequest.getChannel()));
    }
}
