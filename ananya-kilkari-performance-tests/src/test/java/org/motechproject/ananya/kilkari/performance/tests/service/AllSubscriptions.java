package org.motechproject.ananya.kilkari.performance.tests.service;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.kilkari.performance.tests.domain.Subscription;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AllSubscriptions extends MotechBaseRepository<Subscription> {

    @Autowired
    protected AllSubscriptions(@Qualifier("kilkariSubscriptionDbConnector") CouchDbConnector db) {
        super(Subscription.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<Subscription> findByMsisdn(String msisdn) {
        ViewQuery viewQuery = createQuery("by_msisdn").key(msisdn).includeDocs(true);
        List<Subscription> subscriptions = db.queryView(viewQuery, Subscription.class);
        return subscriptions == null ? Collections.EMPTY_LIST : subscriptions;
    }

    @GenerateView
    public Subscription findBySubscriptionId(String subscriptionId) {
        ViewQuery viewQuery = createQuery("by_subscriptionId").key(subscriptionId).includeDocs(true);
        List<Subscription> subscriptions = db.queryView(viewQuery, Subscription.class);
        return singleResult(subscriptions);
    }

}
