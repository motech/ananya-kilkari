package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.subscription.domain.*;

public class SubscriptionRequestMapper {

    public Subscription createSubscription(SubscriptionRequest subscriptionRequest) {
        Subscription subscription = new Subscription(subscriptionRequest.getMsisdn(), SubscriptionPack.from(subscriptionRequest.getPack()),
                subscriptionRequest.getCreatedAt());

        Subscriber subscriber = new Subscriber(subscriptionRequest.getBeneficiaryName(), subscriptionRequest.getBeneficiaryAge(),
                subscriptionRequest.getDateOfBirth(), subscriptionRequest.getExpectedDateOfDelivery());
        subscription.setSubscriber(subscriber);

        Location location = new Location(subscriptionRequest.getPanchayat(), subscriptionRequest.getBlock(), subscriptionRequest.getDistrict());
        subscription.setLocation(location);

        return subscription;
    }
}
