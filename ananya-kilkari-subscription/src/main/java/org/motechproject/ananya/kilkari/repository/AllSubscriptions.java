package org.motechproject.ananya.kilkari.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubscriptions extends MotechBaseRepository<Subscription> {
    @Autowired
    protected AllSubscriptions(@Qualifier("kilkariDbConnector") CouchDbConnector db) {
        super(Subscription.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<Subscription> findByMsisdn(String msisdn) {
        ViewQuery viewQuery = createQuery("by_msisdn").key(msisdn).includeDocs(true);
        List<Subscription> subscriptions = db.queryView(viewQuery, Subscription.class);
        if (subscriptions == null || subscriptions.isEmpty()) return null;
        return subscriptions;
    }

    @GenerateView
    public Subscription findBySubscriptionId(String subscriptionId) {
        List<Subscription> subscriptions = queryView("find_by_subscription_id", subscriptionId);
        return subscriptions.isEmpty() ? null : subscriptions.get(0);
    }

    @View(name = "find_by_msisdn_and_pack", map = "function(doc) {if(doc.type === 'Subscription') emit([doc.msisdn, doc.pack]);}")
    public Subscription findByMsisdnAndPack(String msisdn, SubscriptionPack pack) {
        List<Subscription> subscriptions = queryView("find_by_msisdn_and_pack", ComplexKey.of(msisdn, pack));
        return subscriptions.isEmpty() ? null : subscriptions.get(0);
    }

    public void add(Subscription subscription) {
        Subscription existingSubscription = findByMsisdnAndPack(subscription.getMsisdn(), subscription.getPack());
        if (existingSubscription == null)
            super.add(subscription);
    }
}
