package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KilkariSubscriptionService {
    private SubscriptionPublisher subscriptionPublisher;
    private SubscriptionService subscriptionService;

    @Autowired
    public KilkariSubscriptionService(SubscriptionPublisher subscriptionPublisher, SubscriptionService subscriptionService) {
        this.subscriptionPublisher = subscriptionPublisher;
        this.subscriptionService = subscriptionService;
    }

    public void createSubscription(SubscriptionRequest subscriptionRequest) {
        subscriptionPublisher.createSubscription(subscriptionRequest);
    }

    public void processCallbackRequest(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionPublisher.processCallbackRequest(callbackRequestWrapper);
    }

    public List<Subscription> getSubscriptionsFor(String msisdn) {
        return subscriptionService.findByMsisdn(msisdn);
    }
}
