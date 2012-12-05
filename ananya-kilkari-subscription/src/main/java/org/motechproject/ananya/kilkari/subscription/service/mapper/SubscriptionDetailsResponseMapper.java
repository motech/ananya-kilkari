package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubscriptionDetailsResponseMapper {

    private InboxService inboxService;

    @Autowired
    public SubscriptionDetailsResponseMapper(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    public List<SubscriptionDetailsResponse> map(List<Subscription> subscriptionsList, List<SubscriberResponse> subscriberDetailsFromReports) {
        List<SubscriptionDetailsResponse> responseList = new ArrayList<>();
        for (Subscription subscription : subscriptionsList) {
            responseList.add(constructResponse(subscription, subscriberDetailsFromReports));
        }
        return responseList;
    }

    private SubscriptionDetailsResponse constructResponse(Subscription subscription, List<SubscriberResponse> subscriberDetailsFromReports) {
        String subscriptionId = subscription.getSubscriptionId();
        String messageId = inboxService.getMessageFor(subscriptionId);
        SubscriptionDetailsResponse response = new SubscriptionDetailsResponse(subscriptionId, subscription.getPack(), subscription.getStatus(), messageId);
        SubscriberResponse subscriberDetails = findSubscriberDetailsFor(subscriptionId, subscriberDetailsFromReports);
        if (subscriberDetails == null)
            return response;
        LocationResponse subscriberLocationResponse = subscriberDetails.getLocationResponse();
        Location subscriberLocation =
                subscriberLocationResponse == null
                ? null
                : new Location(subscriberLocationResponse.getDistrict(), subscriberLocationResponse.getBlock(), subscriberLocationResponse.getPanchayat());
        response.updateSubscriberDetails(subscriberDetails.getBeneficiaryName(), subscriberDetails.getBeneficiaryAge(), subscriberDetails.getDateOfBirth(), subscriberDetails.getExpectedDateOfDelivery(), subscription.getStartWeekNumber(), subscriberLocation);
        return response;
    }

    private SubscriberResponse findSubscriberDetailsFor(String subscriptionId, List<SubscriberResponse> subscriberDetailsFromReports) {
        for (SubscriberResponse subscriberDetails : subscriberDetailsFromReports) {
            if (subscriptionId.equals(subscriberDetails.getSubscriptionId())) {
                return subscriberDetails;
            }
        }
        return null;
    }
}
