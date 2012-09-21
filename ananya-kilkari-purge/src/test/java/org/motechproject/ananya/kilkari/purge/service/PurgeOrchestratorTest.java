package org.motechproject.ananya.kilkari.purge.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.purge.service.KilkariPurgeService;
import org.motechproject.ananya.kilkari.purge.service.PurgeOrchestrator;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PurgeOrchestratorTest {
    @Mock
    private KilkariPurgeService kilkariPurgeService;
    @Mock
    private ReportingService reportingService;

    private PurgeOrchestrator purgeOrchestrator;
    private String filePath;
    private File tempFile;

    @Before
    public void setUp() throws IOException {
        purgeOrchestrator = new PurgeOrchestrator(kilkariPurgeService, reportingService);
        tempFile = File.createTempFile("tmp", "txt");
        filePath = tempFile.getAbsolutePath();
    }

    @Test
    public void shouldPurgeSubscription() throws IOException {
        String msisdn = "1234567890";
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write(msisdn);
        fileWriter.close();

        purgeOrchestrator.purgeSubscriptionData(filePath);

        verify(kilkariPurgeService).purge(msisdn);
        verify(reportingService).purge(msisdn);
    }
}
