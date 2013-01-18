package org.motechproject.ananya.kilkari.subscription.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AllSubscriberCareDocs extends MotechBaseRepository<SubscriberCareDoc> {

    @Autowired
    protected AllSubscriberCareDocs(@Qualifier("kilkariSubscriptionDbConnector") CouchDbConnector db) {
        super(SubscriberCareDoc.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_msisdn_and_reason", map = "function(doc) {if(doc.type === 'SubscriberCareDoc') emit([doc.msisdn, doc.reason]);}")
    public SubscriberCareDoc find(String msisdn, String reason) {
        List<SubscriberCareDoc> subscriberCareDocs = queryView("find_by_msisdn_and_reason", ComplexKey.of(msisdn, reason));
        return singleResult(subscriberCareDocs);
    }

    @GenerateView
    public List<SubscriberCareDoc> findByMsisdn(String msisdn) {
        ViewQuery viewQuery = createQuery("by_msisdn").key(msisdn).includeDocs(true);
        List<SubscriberCareDoc> subscriberCareDocs = db.queryView(viewQuery, SubscriberCareDoc.class);
        return subscriberCareDocs == null ? Collections.EMPTY_LIST : subscriberCareDocs;
    }

    @GenerateView
    public List<SubscriberCareDoc> findByCreatedAt(DateTime startDate, DateTime endDate) {
        ViewQuery viewQuery = createQuery("by_createdAt").startKey(startDate).endKey(endDate).includeDocs(true);
        List<SubscriberCareDoc> subscriberCareDocs = db.queryView(viewQuery, SubscriberCareDoc.class);
        return subscriberCareDocs == null ? Collections.EMPTY_LIST : subscriberCareDocs;
    }

    public void deleteFor(String msisdn) {
        removeAll("msisdn", msisdn);
    }
}
