package org.motechproject.ananya.kilkari.subscription.service;

import org.apache.commons.lang.NumberUtils;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeScheduleRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionChangePackRequest;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangePackService {
    private SubscriptionService subscriptionService;
    private SubscriptionValidator subscriptionValidator;
    private ReportingService reportingService;

    @Autowired
    public ChangePackService(SubscriptionService subscriptionService, SubscriptionValidator subscriptionValidator, ReportingService reportingService) {
        this.subscriptionService = subscriptionService;
        this.subscriptionValidator = subscriptionValidator;
        this.reportingService = reportingService;
    }

    public void process(ChangeScheduleRequest changeScheduleRequest) {
        String subscriptionId = changeScheduleRequest.getSubscriptionId();
        subscriptionValidator.validateSubscriptionExists(subscriptionId);
        Subscription existingSubscription = subscriptionService.findBySubscriptionId(subscriptionId);
        ChangePackValidator.validate(existingSubscription, changeScheduleRequest);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, changeScheduleRequest.getChannel(), changeScheduleRequest.getCreatedAt()));
        updateEddOrDob(changeScheduleRequest);
        Subscription newSubscription = createSubscriptionWithNewPack(changeScheduleRequest);

        reportingService.reportChangePack(new SubscriptionChangePackRequest(NumberUtils.createLong(newSubscription.getMsisdn()), newSubscription.getSubscriptionId(), subscriptionId, newSubscription.getPack().name(),
                changeScheduleRequest.getChannel().name(), newSubscription.getStatus().name(), changeScheduleRequest.getCreatedAt(), changeScheduleRequest.getExpectedDateOfDelivery(), changeScheduleRequest.getDateOfBirth(), newSubscription.getStartDate(), changeScheduleRequest.getReason()));
    }

    private void updateEddOrDob(ChangeScheduleRequest changeScheduleRequest) {
        if(changeScheduleRequest.getDateOfBirth() == null && changeScheduleRequest.getExpectedDateOfDelivery() == null) {
            SubscriberResponse subscriberResponse = reportingService.getSubscriber(changeScheduleRequest.getSubscriptionId());
            changeScheduleRequest.setDateOfBirth(subscriberResponse.getDateOfBirth());
            changeScheduleRequest.setExpectedDateOfDelivery(subscriberResponse.getExpectedDateOfDelivery());
        }
    }

    private Subscription createSubscriptionWithNewPack(ChangeScheduleRequest changeScheduleRequest) {
        Subscriber subscriber = new Subscriber(null, null, changeScheduleRequest.getDateOfBirth(), changeScheduleRequest.getExpectedDateOfDelivery(), null);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(changeScheduleRequest.getMsisdn(), changeScheduleRequest.getCreatedAt(), changeScheduleRequest.getPack(), null, subscriber);
        return subscriptionService.createSubscription(subscriptionRequest, changeScheduleRequest.getChannel());
    }
}