package org.motechproject.ananya.kilkari.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.kilkari.domain.Subscription;
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
}
