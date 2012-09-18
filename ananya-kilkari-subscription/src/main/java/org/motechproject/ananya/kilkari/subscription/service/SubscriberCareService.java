package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriberCareRequestMapper;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberCareService {

    private AllSubscriberCareDocs allSubscriberCareDocs;

    @Autowired
    public SubscriberCareService(AllSubscriberCareDocs allSubscriberCareDocs) {
        this.allSubscriberCareDocs = allSubscriberCareDocs;
    }

    public void create(SubscriberCareRequest subscriberCareRequest) {
        allSubscriberCareDocs.addOrUpdate(SubscriberCareRequestMapper.map(subscriberCareRequest));
    }


    public void deleteCareDocsFor(String msisdn) {
        allSubscriberCareDocs.deleteFor(msisdn);
    }
}
