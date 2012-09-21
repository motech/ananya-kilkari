package org.motechproject.ananya.kilkari.purge.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class PurgeOrchestrator {
    private KilkariPurgeService kilkariPurgeService;

    @Autowired
    public PurgeOrchestrator(KilkariPurgeService kilkariPurgeService) {
        this.kilkariPurgeService = kilkariPurgeService;
    }

    public void purgeSubscriptionData(String filePath) throws IOException {
        List<String> msisdnList = readFile(filePath);

        for (String msisdn : msisdnList) {
            kilkariPurgeService.purge(msisdn);
        }
    }

    private List<String> readFile(String filePath) throws IOException {
        File inputFile = new File(filePath);
        return FileUtils.readLines(inputFile);
    }
}
