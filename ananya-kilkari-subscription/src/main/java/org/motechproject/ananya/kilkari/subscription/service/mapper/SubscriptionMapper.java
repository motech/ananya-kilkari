package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.apache.commons.lang.math.NumberUtils;
import org.motechproject.ananya.kilkari.contract.request.SubscriberLocation;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

public class SubscriptionMapper {

    public static OMSubscriptionRequest createOMSubscriptionRequest(Subscription subscription, Channel channel) {
        return new OMSubscriptionRequest(subscription.getMsisdn(), subscription.getPack(), channel, subscription.getSubscriptionId());
    }

    public static SubscriptionReportRequest createSubscriptionCreationReportRequest(
            Subscription subscription, Channel channel, SubscriptionRequest subscriptionRequest) {
        Location location = subscriptionRequest.getLocation();
        SubscriberLocation subscriberLocation = new SubscriberLocation(location.getDistrict(), location.getBlock(), location.getPanchayat());
        Subscriber subscriber = subscriptionRequest.getSubscriber();

        SubscriptionReportRequest subscriptionReportRequest = new SubscriptionReportRequest();
        subscriptionReportRequest.setAgeOfBeneficiary(subscriber.getBeneficiaryAge());
        subscriptionReportRequest.setChannel(channel.name());
        subscriptionReportRequest.setName(subscriber.getBeneficiaryName());
        subscriptionReportRequest.setSubscriptionId(subscription.getSubscriptionId());
        subscriptionReportRequest.setSubscriptionStatus(subscription.getStatus().name());
        subscriptionReportRequest.setCreatedAt(subscription.getCreationDate());
        subscriptionReportRequest.setStartDate(subscription.getStartDate());
        subscriptionReportRequest.setDateOfBirth(subscriber.getDateOfBirth());
        subscriptionReportRequest.setEstimatedDateOfDelivery(subscriber.getExpectedDateOfDelivery());
        subscriptionReportRequest.setLocation(subscriberLocation);
        subscriptionReportRequest.setMsisdn(NumberUtils.createLong(subscription.getMsisdn()));
        subscriptionReportRequest.setPack(subscription.getPack().name());
        subscriptionReportRequest.setOldSubscriptionId(subscriptionRequest.getOldSubscriptionId());

        return subscriptionReportRequest;
    }
}
