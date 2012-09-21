package org.motechproject.ananya.kilkari.purge.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class PurgeOrchestratorTest {
    @Mock
    private KilkariPurgeService kilkariPurgeService;
    @Mock
    private QuartzSchedulerPurgeService quartzSchedulerPurgeService;

    private PurgeOrchestrator purgeOrchestrator;
    private String filePath;
    private File tempFile;

    @Before
    public void setUp() throws IOException {
        purgeOrchestrator = new PurgeOrchestrator(kilkariPurgeService, quartzSchedulerPurgeService);
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

        InOrder inOrder = Mockito.inOrder(quartzSchedulerPurgeService, kilkariPurgeService);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn);
        inOrder.verify(kilkariPurgeService).purge(msisdn);
    }
}
