package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

public class SubscriptionRequestMapper {

    public SubscriptionRequest createSubscriptionDomainRequest(SubscriptionWebRequest subscriptionWebRequest) {
        Location location = new Location(subscriptionWebRequest.getDistrict(), subscriptionWebRequest.getBlock(), subscriptionWebRequest.getPanchayat());
        Subscriber subscriber = new Subscriber(subscriptionWebRequest.getBeneficiaryName(), subscriptionWebRequest.getBeneficiaryAge(),
                subscriptionWebRequest.getDateOfBirth(), subscriptionWebRequest.getExpectedDateOfDelivery());

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(subscriptionWebRequest.getMsisdn(), subscriptionWebRequest.getCreatedAt(), SubscriptionPack.from(subscriptionWebRequest.getPack()), location, subscriber);
        return subscriptionRequest;
    }
}
