package org.motechproject.ananya.kilkari.subscription.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubscriberCareDocs extends MotechBaseRepository<SubscriberCareDoc> {

    @Autowired
    protected AllSubscriberCareDocs(@Qualifier("kilkariSubscriptionDbConnector") CouchDbConnector db) {
        super(SubscriberCareDoc.class, db);
        initStandardDesignDocument();
    }

    public void addOrUpdate(SubscriberCareDoc subscriberCareDoc) {
        SubscriberCareDoc existingSubscriberCareDoc = find(subscriberCareDoc.getMsisdn(), subscriberCareDoc.getReason().name());
        if(existingSubscriberCareDoc == null)
            super.add(subscriberCareDoc);
        else {
            existingSubscriberCareDoc.setCreatedAt(subscriberCareDoc.getCreatedAt());
            super.update(existingSubscriberCareDoc);
        }
    }

    @View(name = "find_by_msisdn_and_reason", map = "function(doc) {if(doc.type === 'SubscriberCareDoc') emit([doc.msisdn, doc.reason]);}")
    public SubscriberCareDoc find(String msisdn, String reason) {
        List<SubscriberCareDoc> subscriberCareDocs = queryView("find_by_msisdn_and_reason", ComplexKey.of(msisdn, reason));
        return singleResult(subscriberCareDocs);
    }
}
