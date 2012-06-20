package org.motechproject.ananya.kilkari.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSubscriptions  extends MotechBaseRepository<Subscription> {
    @Autowired
    protected AllSubscriptions(@Qualifier("kilkariDbConnector") CouchDbConnector db) {
        super(Subscription.class, db);
        initStandardDesignDocument();
    }
}
