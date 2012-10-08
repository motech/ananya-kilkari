package org.motechproject.ananya.kilkari.purge.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class PurgeOrchestrator {
    private KilkariPurgeService kilkariPurgeService;
    private QuartzSchedulerPurgeService quartzSchedulerPurgeService;

    @Autowired
    public PurgeOrchestrator(KilkariPurgeService kilkariPurgeService, QuartzSchedulerPurgeService quartzSchedulerPurgeService) {
        this.kilkariPurgeService = kilkariPurgeService;
        this.quartzSchedulerPurgeService = quartzSchedulerPurgeService;
    }

    public void purgeSubscriptionData(String filePath) throws IOException {
        List<String> msisdnList = readFile(filePath);

        for (String msisdn : msisdnList) {
            purgeSubscriptionDataFor(msisdn);
        }
    }

    private void purgeSubscriptionDataFor(String msisdn) {
        msisdn = msisdn.trim();
        if(StringUtils.isEmpty(msisdn)) {
            return;
        }
        quartzSchedulerPurgeService.deleteFor(msisdn);
        kilkariPurgeService.purge(msisdn);
    }

    private List<String> readFile(String filePath) throws IOException {
        File inputFile = new File(filePath);
        return FileUtils.readLines(inputFile);
    }
}
