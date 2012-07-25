package org.motechproject.ananya.kilkari.subscription.mappers;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Location;
import org.motechproject.ananya.kilkari.subscription.domain.Subscriber;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

public class SubscriptionMapper {

    public OMSubscriptionRequest createOMSubscriptionRequest(Subscription subscription, Channel channel) {
        return new OMSubscriptionRequest(subscription.getMsisdn(), subscription.getPack(), channel, subscription.getSubscriptionId());
    }

    public SubscriptionCreationReportRequest createSubscriptionCreationReportRequest(Subscription subscription, Channel channel) {
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails(subscription.getMsisdn(), subscription.getPack().name(),
                subscription.getCreationDate(), subscription.getStatus().name(), subscription.getSubscriptionId());

        Location location = subscription.getLocation();
        SubscriberLocation subscriberLocation = new SubscriberLocation(location.getDistrict(), location.getBlock(), location.getPanchayat());

        Subscriber subscriber = subscription.getSubscriber();
        return new SubscriptionCreationReportRequest(subscriptionDetails, channel.name(), subscriber.getBeneficiaryAge(),
                subscriber.getBeneficiaryName(), subscriber.getDateOfBirth(), subscriber.getExpectedDateOfDelivery(), subscriberLocation);
    }
}
