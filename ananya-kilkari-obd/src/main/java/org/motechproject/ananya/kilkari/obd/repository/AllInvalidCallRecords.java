package org.motechproject.ananya.kilkari.obd.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AllInvalidCallRecords extends MotechBaseRepository<InvalidCallRecord> {

    @Autowired
    public AllInvalidCallRecords(@Qualifier("obdDbConnector") CouchDbConnector db) {
        super(InvalidCallRecord.class, db);
        initStandardDesignDocument();
    }

    public void deleteFor(String subscriptionId) {
        removeAll("subscriptionId", subscriptionId);
    }

    @GenerateView
    public List<InvalidCallRecord> findBySubscriptionId(String subscriptionId) {
        ViewQuery viewQuery = createQuery("by_subscriptionId").key(subscriptionId).includeDocs(true);
        List<InvalidCallRecord> invalidCallRecords = db.queryView(viewQuery, InvalidCallRecord.class);
        return invalidCallRecords == null ? Collections.EMPTY_LIST : invalidCallRecords;
    }
}
