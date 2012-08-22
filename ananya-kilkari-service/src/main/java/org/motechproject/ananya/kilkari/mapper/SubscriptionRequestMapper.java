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
                request.getBeneficiaryName(), convertToInteger(request.getBeneficiaryAge()),location);
    }

    public static ChangeSubscriptionRequest mapToChangeSubscriptionRequest(ChangeSubscriptionWebRequest webRequest, String subscriptionId) {
        return new ChangeSubscriptionRequest(ChangeSubscriptionType.from(webRequest.getChangeType()), webRequest.getMsisdn(), subscriptionId, SubscriptionPack.from(webRequest.getPack()), Channel.from(webRequest.getChannel()),
                webRequest.getCreatedAt(), DateUtils.parseDate(webRequest.getExpectedDateOfDelivery()), DateUtils.parseDate(webRequest.getDateOfBirth()), webRequest.getReason());
    }

    private static Integer convertToInteger(String age) {
        return age == null ? null : Integer.valueOf(age);
    }
}
