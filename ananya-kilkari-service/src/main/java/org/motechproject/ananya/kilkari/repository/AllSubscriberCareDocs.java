package org.motechproject.ananya.kilkari.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.kilkari.domain.SubscriberCareDoc;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSubscriberCareDocs extends MotechBaseRepository<SubscriberCareDoc> {

    @Autowired
    protected AllSubscriberCareDocs(@Qualifier("kilkariDbConnector") CouchDbConnector db) {
        super(SubscriberCareDoc.class, db);
        initStandardDesignDocument();
    }

    public void add(SubscriberCareDoc subscriberCareDoc) {
        super.add(subscriberCareDoc);
    }
}
