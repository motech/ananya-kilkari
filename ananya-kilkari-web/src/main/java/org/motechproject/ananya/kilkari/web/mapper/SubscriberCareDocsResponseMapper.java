package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.kilkari.web.response.SubscriberCareDocResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberCareDocResponseList;

import java.util.ArrayList;
import java.util.List;

public class SubscriberCareDocsResponseMapper {

    public static SubscriberCareDocResponseList mapToSubscriberDocsResponseList(List<SubscriberCareDoc> subscriberCareDocList) {
        ArrayList<SubscriberCareDocResponse> subscriberCareDocResponses = new ArrayList<>();
        for (SubscriberCareDoc subscriberCareDoc : subscriberCareDocList) {
            DateTime createdAt = subscriberCareDoc.getCreatedAt();
            subscriberCareDocResponses.add(new SubscriberCareDocResponse(subscriberCareDoc.getMsisdn(),
                    subscriberCareDoc.getReason().name(), DateUtils.formatDate(createdAt, DateUtils.ISTTimeZone), DateUtils.formatTime(createdAt, DateUtils.ISTTimeZone)));
        }
        return new SubscriberCareDocResponseList(subscriberCareDocResponses);
    }
}
