package org.motechproject.ananya.kilkari.obd.repository;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.fail;

public class AllInvalidCallRecordsIT extends SpringIntegrationTest {

    @Autowired
    private AllInvalidCallRecords allInvalidCallRecords;

    @Test
    public void shouldSaveInvalidCallRecordInDB() {
        InvalidCallRecord invalidCallRecord = new InvalidCallRecord("mymsisdn", "mysubscriptionid", "mycampaignid", "myoperator", "mydescription");
        allInvalidCallRecords.add(invalidCallRecord);
        markForDeletion(invalidCallRecord);

        List<InvalidCallRecord> invalidCallRecordsFromDB = allInvalidCallRecords.getAll();
        for(InvalidCallRecord fromDB: invalidCallRecordsFromDB) {
            if(equals(invalidCallRecord, fromDB)) {
                return;
            }
        }

        fail("Should have found invalid call record in db");
    }

    private boolean equals(InvalidCallRecord invalidCallRecord, InvalidCallRecord fromDB) {
        return new EqualsBuilder()
                .append(invalidCallRecord.getCampaignId(), fromDB.getCampaignId())
                .append(invalidCallRecord.getDescription(), fromDB.getDescription())
                .append(invalidCallRecord.getMsisdn(), fromDB.getMsisdn())
                .append(invalidCallRecord.getOperator(), fromDB.getOperator())
                .append(invalidCallRecord.getSubscriptionId(), fromDB.getSubscriptionId())
                .isEquals();
    }
}
