package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.apache.commons.lang.math.NumberUtils;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberLocation;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionReportRequest;
import org.springframework.util.StringUtils;

public class SubscriptionMapper {

    public static OMSubscriptionRequest createOMSubscriptionRequest(Subscription subscription, Channel channel) {
        return new OMSubscriptionRequest(subscription.getMsisdn(), subscription.getPack(), channel, subscription.getSubscriptionId());
    }

    public static SubscriptionReportRequest createSubscriptionCreationReportRequest(
        Subscription subscription, Channel channel, SubscriptionRequest subscriptionRequest, Integer weekNumber, String operator, boolean requestedFromSM) {
        Location location = subscriptionRequest.getLocation();
        SubscriberLocation subscriberLocation = subscriptionRequest.hasLocation() ? new SubscriberLocation(location.getState(),location.getDistrict(), location.getBlock(), location.getPanchayat()) : null;
        Subscriber subscriber = subscriptionRequest.getSubscriber();
        Integer startWeekNumber = requestedFromSM?weekNumber:subscriber.getWeek();
        Long msisdn = NumberUtils.createLong(subscription.getMsisdn());
        Long referredBy = StringUtils.hasText(subscription.getReferredBy())?NumberUtils.createLong(subscription.getReferredBy()) : null;
        boolean referredByFlag = subscription.isReferredByFLW();
        return new SubscriptionReportRequest(subscription.getSubscriptionId(), channel.name(), msisdn, subscription.getPack().name(),
                subscriber.getBeneficiaryName(), subscriber.getBeneficiaryAge(), subscriptionRequest.getCreationDate(), subscription.getStatus().name(), subscriber.getExpectedDateOfDelivery(),
                subscriber.getDateOfBirth(), subscriberLocation, operator, subscription.getStartDate(), subscriptionRequest.getOldSubscriptionId(), subscriptionRequest.getReason(), startWeekNumber, referredBy, referredByFlag , requestedFromSM);
    }
}
