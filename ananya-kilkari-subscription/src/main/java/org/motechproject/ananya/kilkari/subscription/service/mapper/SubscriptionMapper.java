package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;

public class SubscriptionMapper {

    public OMSubscriptionRequest createOMSubscriptionRequest(Subscription subscription, Channel channel) {
        return new OMSubscriptionRequest(subscription.getMsisdn(), subscription.getPack(), channel, subscription.getSubscriptionId());
    }

    public SubscriptionCreationReportRequest createSubscriptionCreationReportRequest(org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription, Channel channel, Location location, Subscriber subscriber) {
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails(subscription.getMsisdn(), subscription.getPack().name(),
                subscription.getCreationDate(), subscription.getStatus().name(), subscription.getSubscriptionId(), subscription.getStartDate());

        SubscriberLocation subscriberLocation = new SubscriberLocation(location.getDistrict(), location.getBlock(), location.getPanchayat());

        return new SubscriptionCreationReportRequest(subscriptionDetails, channel.name(), subscriber.getBeneficiaryAge(),
                subscriber.getBeneficiaryName(), subscriber.getDateOfBirth(), subscriber.getExpectedDateOfDelivery(), subscriberLocation);
    }
}
