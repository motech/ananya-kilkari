package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.request.HelpWebRequest;
import org.motechproject.ananya.kilkari.service.validator.SubscriberCareRequestValidator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KilkariSubscriberCareService {
    private static final Logger logger = LoggerFactory.getLogger(KilkariSubscriberCareService.class);
    private SubscriberCareRequestValidator careRequestValidator;
    private SubscriptionPublisher subscriptionPublisher;
    private SubscriberCareService subscriberCareService;

    public KilkariSubscriberCareService(SubscriberCareService subscriberCareService, SubscriberCareRequestValidator careRequestValidator,
                                        SubscriptionPublisher subscriptionPublisher) {
        this.subscriberCareService = subscriberCareService;
        this.careRequestValidator = careRequestValidator;
        this.subscriptionPublisher = subscriptionPublisher;
    }

    @Autowired
    public KilkariSubscriberCareService(SubscriberCareService subscriberCareService,
                                        SubscriptionPublisher subscriptionPublisher) {
        this.subscriberCareService = subscriberCareService;
        this.subscriptionPublisher = subscriptionPublisher;
        this.careRequestValidator = new SubscriberCareRequestValidator();
    }

    public void createSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
        logger.info(String.format("Create subscriber care request event for msisdn: %s, reason: %s, channel:%s, createdAt: %s",
                subscriberCareRequest.getMsisdn(), subscriberCareRequest.getReason(),
                subscriberCareRequest.getChannel(), subscriberCareRequest.getCreatedAt()));
        careRequestValidator.validate(subscriberCareRequest);
        subscriberCareService.create(subscriberCareRequest);
    }

    public List<SubscriberCareDoc> fetchSubscriberCareDocs(HelpWebRequest helpWebRequest) {
        return subscriberCareService.getAllSortedByDate(helpWebRequest.getStartTime(), helpWebRequest.getEndTime());
    }
}
