package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionResponse;

public class SubscriptionMapper {

    public OMSubscriptionRequest createOMSubscriptionRequest(SubscriptionResponse subscriptionResponse, Channel channel) {
        return new OMSubscriptionRequest(subscriptionResponse.getMsisdn(), subscriptionResponse.getPack(), channel, subscriptionResponse.getSubscriptionId());
    }

    public SubscriptionCreationReportRequest createSubscriptionCreationReportRequest(Subscription subscription, Channel channel, Location location, Subscriber subscriber) {
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails(subscription.getMsisdn(), subscription.getPack().name(),
                subscription.getCreationDate(), subscription.getStatus().name(), subscription.getSubscriptionId());

        SubscriberLocation subscriberLocation = new SubscriberLocation(location.getDistrict(), location.getBlock(), location.getPanchayat());

        return new SubscriptionCreationReportRequest(subscriptionDetails, channel.name(), subscriber.getBeneficiaryAge(),
                subscriber.getBeneficiaryName(), subscriber.getDateOfBirth(), subscriber.getExpectedDateOfDelivery(), subscriberLocation);
    }
}
