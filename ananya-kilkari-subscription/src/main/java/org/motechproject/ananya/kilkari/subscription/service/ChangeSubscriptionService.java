package org.motechproject.ananya.kilkari.subscription.service;


import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangeSubscriptionService {
    private SubscriptionService subscriptionService;
    private ChangeSubscriptionValidator changeSubscriptionValidator;
    private ReportingService reportingService;


    @Autowired
    public ChangeSubscriptionService(SubscriptionService subscriptionService, ChangeSubscriptionValidator changeSubscriptionValidator, ReportingService reportingService) {
        this.subscriptionService = subscriptionService;
        this.changeSubscriptionValidator = changeSubscriptionValidator;
        this.reportingService = reportingService;
    }

    public void process(ChangeSubscriptionRequest changeSubscriptionRequest) {
        changeSubscriptionValidator.validate(changeSubscriptionRequest);
        String subscriptionId = changeSubscriptionRequest.getSubscriptionId();
        Subscription existingSubscription = subscriptionService.findBySubscriptionId(subscriptionId);
        changeSubscriptionRequest.setMsisdn(existingSubscription.getMsisdn());
        changeSubscriptionRequest.prefixReasonWithChangeType();
		if(ChangeSubscriptionType.isChangeReferredBy(changeSubscriptionRequest.getChangeType()))
			subscriptionService.updateReferredByMsisdn(existingSubscription, changeSubscriptionRequest);   
		else{
        subscriptionService.requestDeactivation(new DeactivationRequest(changeSubscriptionRequest.getSubscriptionId(), changeSubscriptionRequest.getChannel(),
                changeSubscriptionRequest.getCreatedAt(), changeSubscriptionRequest.getReason()));
        updateEddOrDob(changeSubscriptionRequest);
        createSubscriptionWithNewPack(changeSubscriptionRequest);
		}
    }

    private void updateEddOrDob(ChangeSubscriptionRequest changeSubscriptionRequest) {
        if (changeSubscriptionRequest.getDateOfBirth() == null && changeSubscriptionRequest.getExpectedDateOfDelivery() == null) {
            SubscriberResponse subscriberResponse = reportingService.getSubscriber(changeSubscriptionRequest.getSubscriptionId());
            changeSubscriptionRequest.setDateOfBirth(subscriberResponse.getDateOfBirth());
            changeSubscriptionRequest.setExpectedDateOfDelivery(subscriberResponse.getExpectedDateOfDelivery());
        }
    }

    private Subscription createSubscriptionWithNewPack(ChangeSubscriptionRequest changeSubscriptionRequest) {
        Subscriber subscriber = new Subscriber(null, null, changeSubscriptionRequest.getDateOfBirth(), changeSubscriptionRequest.getExpectedDateOfDelivery(), null);
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest(changeSubscriptionRequest.getMsisdn(), changeSubscriptionRequest.getCreatedAt(), changeSubscriptionRequest.getPack(), null, subscriber, changeSubscriptionRequest.getReason(), changeSubscriptionRequest.getReferredBy());
        subscriptionRequest.setOldSubscriptionId(changeSubscriptionRequest.getSubscriptionId());
        return subscriptionService.createSubscription(subscriptionRequest, changeSubscriptionRequest.getChannel());
    }
}
