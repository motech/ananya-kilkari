package org.motechproject.ananya.kilkari.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.request.ChangeSubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.SubscriberWebRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.*;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class SubscriptionRequestMapper {
    public static SubscriptionRequest mapToSubscriptionRequest(SubscriptionWebRequest subscriptionWebRequest) {
        Location location = subscriptionWebRequest.getLocation();
        Integer week = StringUtils.isBlank(subscriptionWebRequest.getWeek()) ? null : new Integer(subscriptionWebRequest.getWeek());
        Subscriber subscriber = new Subscriber(subscriptionWebRequest.getBeneficiaryName(), subscriptionWebRequest.getBeneficiaryAge(),
                subscriptionWebRequest.getDateOfBirth(), subscriptionWebRequest.getExpectedDateOfDelivery(), week);


        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(
                subscriptionWebRequest.getMsisdn(),
                subscriptionWebRequest.getCreatedAt(),
                SubscriptionPack.from(subscriptionWebRequest.getPack()),
                location,
                subscriber,
                null,
                subscriptionWebRequest.getReferredBy(),
                subscriptionWebRequest.isReferredByFLW());
        return subscriptionRequest;
    }

    public static SubscriberRequest mapToSubscriberRequest(SubscriberWebRequest request, String subscriptionId) {
        Location location = request.getLocation();
        return new SubscriberRequest(subscriptionId, request.getChannel(), request.getCreatedAt(),
                request.getBeneficiaryName(), convertToInteger(request.getBeneficiaryAge()),location);
    }

    public static ChangeSubscriptionRequest mapToChangeSubscriptionRequest(ChangeSubscriptionWebRequest webRequest, String subscriptionId) {
        return new ChangeSubscriptionRequest(ChangeSubscriptionType.from(webRequest.getChangeType()), null, subscriptionId, SubscriptionPack.from(webRequest.getPack()), Channel.from(webRequest.getChannel()),
                webRequest.getCreatedAt(), DateUtils.parseDate(webRequest.getExpectedDateOfDelivery()), DateUtils.parseDate(webRequest.getDateOfBirth()), webRequest.getReason(), webRequest.getReferredBy(), webRequest.isReferredByFLW());
    }

    private static Integer convertToInteger(String age) {
        return age == null ? null : Integer.valueOf(age);
    }
}
