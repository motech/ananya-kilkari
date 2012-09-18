package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.repository.AllInvalidCallRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvalidOBDEntriesService {
    private AllInvalidCallRecords allInvalidCallRecords;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidOBDEntriesService.class);

    @Autowired
    InvalidOBDEntriesService(AllInvalidCallRecords allInvalidCallRecords) {
        this.allInvalidCallRecords = allInvalidCallRecords;
    }

    public void processInvalidCallRecords(List<InvalidCallRecord> invalidCallRecords) {
        if (invalidCallRecords.isEmpty()) {
            return;
        }
        LOGGER.error(String.format("Received obd callback for %s invalid call records.", invalidCallRecords.size()));
        for (InvalidCallRecord record : invalidCallRecords) {
            allInvalidCallRecords.add(record);
        }
    }

    public void deleteInvalidCallRecordsFor(String subscriptionId) {
        allInvalidCallRecords.deleteFor(subscriptionId);
    }
}