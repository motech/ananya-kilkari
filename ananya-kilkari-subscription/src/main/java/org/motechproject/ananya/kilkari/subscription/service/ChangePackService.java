package org.motechproject.ananya.kilkari.subscription.service;

import org.apache.commons.lang.NumberUtils;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;
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

    public void process(ChangeSubscriptionRequest changeSubscriptionRequest) {
        String subscriptionId = changeSubscriptionRequest.getSubscriptionId();
        subscriptionValidator.validateSubscriptionExists(subscriptionId);
        Subscription existingSubscription = subscriptionService.findBySubscriptionId(subscriptionId);
        ChangePackValidator.validate(existingSubscription, changeSubscriptionRequest);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, changeSubscriptionRequest.getChannel(), changeSubscriptionRequest.getCreatedAt()));
        updateEddOrDob(changeSubscriptionRequest);
        Subscription newSubscription = createSubscriptionWithNewPack(changeSubscriptionRequest);

        reportingService.reportChangePack(new SubscriptionChangePackRequest(NumberUtils.createLong(newSubscription.getMsisdn()), newSubscription.getSubscriptionId(), subscriptionId, newSubscription.getPack().name(),
                changeSubscriptionRequest.getChannel().name(), newSubscription.getStatus().name(), changeSubscriptionRequest.getCreatedAt(), changeSubscriptionRequest.getExpectedDateOfDelivery(), changeSubscriptionRequest.getDateOfBirth(), newSubscription.getStartDate(), changeSubscriptionRequest.getReason()));
    }

    private void updateEddOrDob(ChangeSubscriptionRequest changeSubscriptionRequest) {
        if(changeSubscriptionRequest.getDateOfBirth() == null && changeSubscriptionRequest.getExpectedDateOfDelivery() == null) {
            SubscriberResponse subscriberResponse = reportingService.getSubscriber(changeSubscriptionRequest.getSubscriptionId());
            changeSubscriptionRequest.setDateOfBirth(subscriberResponse.getDateOfBirth());
            changeSubscriptionRequest.setExpectedDateOfDelivery(subscriberResponse.getExpectedDateOfDelivery());
        }
    }

    private Subscription createSubscriptionWithNewPack(ChangeSubscriptionRequest changeSubscriptionRequest) {
        Subscriber subscriber = new Subscriber(null, null, changeSubscriptionRequest.getDateOfBirth(), changeSubscriptionRequest.getExpectedDateOfDelivery(), null);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(changeSubscriptionRequest.getMsisdn(), changeSubscriptionRequest.getCreatedAt(), changeSubscriptionRequest.getPack(), null, subscriber);
        return subscriptionService.createSubscription(subscriptionRequest, changeSubscriptionRequest.getChannel());
    }
}