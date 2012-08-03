package org.motechproject.ananya.kilkari.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.request.SubscriberWebRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class SubscriptionRequestMapper {
    public static SubscriptionRequest mapToSubscriptionRequest(SubscriptionWebRequest subscriptionWebRequest) {
        Location location = new Location(subscriptionWebRequest.getDistrict(), subscriptionWebRequest.getBlock(), subscriptionWebRequest.getPanchayat());
        Integer week = StringUtils.isBlank(subscriptionWebRequest.getWeek()) ? null : new Integer(subscriptionWebRequest.getWeek());
        Subscriber subscriber = new Subscriber(subscriptionWebRequest.getBeneficiaryName(), subscriptionWebRequest.getBeneficiaryAge(),
                subscriptionWebRequest.getDateOfBirth(), subscriptionWebRequest.getExpectedDateOfDelivery(), week);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(
                subscriptionWebRequest.getMsisdn(),
                subscriptionWebRequest.getCreatedAt(),
                SubscriptionPack.from(subscriptionWebRequest.getPack()),
                location,
                subscriber
        );
        return subscriptionRequest;
    }

    public static SubscriberRequest mapToSubscriberRequest(SubscriberWebRequest request, String subscriptionId) {
        Location location = new Location(request.getDistrict(), request.getBlock(), request.getPanchayat());
        return new SubscriberRequest(subscriptionId, request.getChannel(), request.getCreatedAt(),
                request.getBeneficiaryName(), convertToInteger(request.getBeneficiaryAge()), DateUtils.parseDate(request.getExpectedDateOfDelivery()),
                DateUtils.parseDate(request.getDateOfBirth()), location);
    }

    private static Integer convertToInteger(String age) {
        return age == null ? null : Integer.valueOf(age);
    }
}
