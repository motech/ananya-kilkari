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

import static org.mockito.Mockito.verifyNoMoreInteractions;

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
        tempFile.deleteOnExit();
        filePath = tempFile.getAbsolutePath();
    }

    @Test
    public void shouldPurgeSubscription() throws IOException {
        String msisdn1 = "1234567890";
        String msisdn2 = "1234567891";
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write(msisdn1);
        fileWriter.write("\n" + msisdn2);
        fileWriter.close();

        purgeOrchestrator.purgeSubscriptionData(filePath);

        InOrder inOrder = Mockito.inOrder(quartzSchedulerPurgeService, kilkariPurgeService);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn1);
        inOrder.verify(kilkariPurgeService).purge(msisdn1);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn2);
        inOrder.verify(kilkariPurgeService).purge(msisdn2);

        verifyNoMoreInteractions(quartzSchedulerPurgeService);
        verifyNoMoreInteractions(kilkariPurgeService);
    }


    @Test
    public void shouldIgnoreBlankLinesWhileReadingFromTheFile() throws IOException {
        String msisdn1 = "1234567890";
        String msisdn2 = "1234567891";
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write("");
        fileWriter.write("\n" + msisdn1);
        fileWriter.write("\n    ");
        fileWriter.write("\n" + msisdn2);
        fileWriter.write("\n");
        fileWriter.close();

        purgeOrchestrator.purgeSubscriptionData(filePath);

        InOrder inOrder = Mockito.inOrder(quartzSchedulerPurgeService, kilkariPurgeService);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn1);
        inOrder.verify(kilkariPurgeService).purge(msisdn1);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn2);
        inOrder.verify(kilkariPurgeService).purge(msisdn2);

        verifyNoMoreInteractions(quartzSchedulerPurgeService);
        verifyNoMoreInteractions(kilkariPurgeService);
    }

    @Test
    public void shouldTrimMsidnWhileReadingFromTheFile() throws IOException {
        String msisdn1 = "1234567890";
        String msisdn2 = "1234567891";
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write("    " + msisdn1 + "    ");
        fileWriter.write("\n    " + msisdn2);
        fileWriter.close();

        purgeOrchestrator.purgeSubscriptionData(filePath);

        InOrder inOrder = Mockito.inOrder(quartzSchedulerPurgeService, kilkariPurgeService);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn1);
        inOrder.verify(kilkariPurgeService).purge(msisdn1);
        inOrder.verify(quartzSchedulerPurgeService).deleteFor(msisdn2);
        inOrder.verify(kilkariPurgeService).purge(msisdn2);

        verifyNoMoreInteractions(quartzSchedulerPurgeService);
        verifyNoMoreInteractions(kilkariPurgeService);
    }
}
