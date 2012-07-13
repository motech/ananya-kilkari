package org.motechproject.ananya.kilkari.obd.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllInvalidCallRecords extends MotechBaseRepository<InvalidCallRecord> {

    @Autowired
    public AllInvalidCallRecords(@Qualifier("obdDbConnector") CouchDbConnector db) {
        super(InvalidCallRecord.class, db);
        initStandardDesignDocument();
    }

}
