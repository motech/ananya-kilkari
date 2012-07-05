package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequestMapper;
import org.motechproject.ananya.kilkari.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.validators.SubscriberCareRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberCareService {
    private AllSubscriberCareDocs allSubscriberCareDocs;

    @Autowired
    public SubscriberCareService(AllSubscriberCareDocs allSubscriberCareDocs) {
        this.allSubscriberCareDocs = allSubscriberCareDocs;
    }

    public void createSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
        SubscriberCareRequestValidator.validate(subscriberCareRequest);

        allSubscriberCareDocs.add(SubscriberCareRequestMapper.map(subscriberCareRequest));
    }
}
