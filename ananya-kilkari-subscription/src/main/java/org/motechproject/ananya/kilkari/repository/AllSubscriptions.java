package org.motechproject.ananya.kilkari.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.dao.MotechBaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubscriptions extends MotechBaseRepository<Subscription> {
    private final static Logger logger = LoggerFactory.getLogger(AllSubscriptions.class);

    @Autowired
    protected AllSubscriptions(@Qualifier("kilkariSubscriptionDbConnector") CouchDbConnector db) {
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
        ViewQuery viewQuery = createQuery("by_subscriptionId").key(subscriptionId).includeDocs(true);
        List<Subscription> subscriptions = db.queryView(viewQuery, Subscription.class);
        return singleResult(subscriptions);
    }

    @View(name = "find_by_msisdn_and_pack", map = "function(doc) {if(doc.type === 'Subscription') emit([doc.msisdn, doc.pack]);}")
    public Subscription findByMsisdnAndPack(String msisdn, SubscriptionPack pack) {
        List<Subscription> subscriptions = queryView("find_by_msisdn_and_pack", ComplexKey.of(msisdn, pack));
        return singleResult(subscriptions);
    }

    public void add(Subscription subscription) {
        Subscription existingSubscription = findByMsisdnAndPack(subscription.getMsisdn(), subscription.getPack());
        if (existingSubscription == null)
            super.add(subscription);
        else
            logger.info(String.format("Ignored Create subscription for msisdn: %s, pack: %s as an active subscription already exists for the given msisdn and pack.",
                    subscription.getMsisdn(), subscription.getPack()));
    }
}
