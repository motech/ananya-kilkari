package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.service.validator.SubscriberCareRequestValidator;
import org.motechproject.ananya.kilkari.subscription.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KilkariSubscriberCareService {
    private SubscriberCareRequestValidator careRequestValidator;
    private SubscriptionPublisher subscriptionPublisher;
    private SubscriberCareService subscriberCareService;

    @Autowired
    public KilkariSubscriberCareService(SubscriberCareService subscriberCareService, SubscriberCareRequestValidator careRequestValidator,
                                        SubscriptionPublisher subscriptionPublisher) {
        this.subscriberCareService = subscriberCareService;
        this.careRequestValidator = careRequestValidator;
        this.subscriptionPublisher = subscriptionPublisher;
    }

    public void processSubscriberCareRequest(String msisdn, String reason, String channel, DateTime createdAt) {
        subscriptionPublisher.processSubscriberCareRequest(
                new SubscriberCareRequest(msisdn, reason, channel, createdAt));
    }

    public void createSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
        careRequestValidator.validate(subscriberCareRequest);
        subscriberCareService.create(subscriberCareRequest);
    }
}
