package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriptionResponse;
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

    public List<SubscriptionDetailsResponse> map(List<Subscription> subscriptionList, List<SubscriptionResponse> subscriberDetailsList) {
        List<SubscriptionDetailsResponse> responseList = new ArrayList<>();
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();
            String messageId = inboxService.getMessageFor(subscriptionId);
            SubscriptionDetailsResponse response = new SubscriptionDetailsResponse(subscriptionId, subscription.getPack(), subscription.getStatus(), messageId);
            for (SubscriptionResponse reportDetails : subscriberDetailsList) {
                if (subscriptionId.equals(reportDetails.getSubscriptionId())) {
                    response.updateSubscriberDetails(reportDetails.getBeneficiaryName(), reportDetails.getBeneficiaryAge(),
                            reportDetails.getStartWeekNumber(), reportDetails.getDateOfBirth(), reportDetails.getExpectedDateOfDelivery(), reportDetails.getLocation());
                }
            }
            responseList.add(response);
        }
        return responseList;
    }
}
