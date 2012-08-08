package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequestMapper;
import org.motechproject.ananya.kilkari.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.service.validator.SubscriberCareRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberCareService {
    private AllSubscriberCareDocs allSubscriberCareDocs;
    private SubscriberCareRequestValidator careRequestValidator;
    private SubscriptionPublisher subscriptionPublisher;

    @Autowired
    public SubscriberCareService(AllSubscriberCareDocs allSubscriberCareDocs, SubscriberCareRequestValidator careRequestValidator,
                                 SubscriptionPublisher subscriptionPublisher) {
        this.allSubscriberCareDocs = allSubscriberCareDocs;
        this.careRequestValidator = careRequestValidator;
        this.subscriptionPublisher = subscriptionPublisher;
    }

    public void processSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
        subscriptionPublisher.processSubscriberCareRequest(subscriberCareRequest);
    }

    public void createSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
        careRequestValidator.validate(subscriberCareRequest);
        allSubscriberCareDocs.addOrUpdate(SubscriberCareRequestMapper.map(subscriberCareRequest));
    }
}
