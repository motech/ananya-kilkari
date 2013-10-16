package org.motechproject.ananya.kilkari.obd.repository;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;


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

    @Test
    public void shouldDeleteAllInvalidCallRecordsForASubscriptionId() {
        String subscriptionId = "abcd1234";
        allInvalidCallRecords.add(new InvalidCallRecord("1234", subscriptionId, "", "", ""));

        allInvalidCallRecords.deleteFor(subscriptionId);

        assertTrue(allInvalidCallRecords.findBySubscriptionId(subscriptionId).isEmpty());
    }

    @Test
    public void shouldFindBySubscriptionId() {
        String subscriptionId = "abcd1234";
        String msisdn = "1234";
        allInvalidCallRecords.add(new InvalidCallRecord(msisdn, subscriptionId, "", "", ""));

        List<InvalidCallRecord> subscriptions = allInvalidCallRecords.findBySubscriptionId(subscriptionId);

        assertEquals(msisdn, subscriptions.get(0).getMsisdn());
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
