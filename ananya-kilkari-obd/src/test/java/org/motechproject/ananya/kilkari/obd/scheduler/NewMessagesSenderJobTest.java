package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.retry.domain.RetryRequest;
import org.motechproject.retry.service.RetryService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewMessagesSenderJobTest {
    @Mock
    private Properties obdProperties;

    @Mock
    private RetryService retryService;

    @Mock
    private CampaignMessageService campaignMessageService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldScheduleCronJobsAtConstruction() {
        String cronJobExpression = "mycronjobexpression";
        when(obdProperties.getProperty("obd.new.messages.job.cron.expression")).thenReturn(cronJobExpression);

        CronSchedulableJob cronSchedulableJob = new NewMessagesSenderJob(campaignMessageService, retryService, obdProperties).getCronJob();

        assertEquals(cronJobExpression, cronSchedulableJob.getCronExpression());
        assertNull(cronSchedulableJob.getEndTime());

        DateTime startDateTime = new DateTime(cronSchedulableJob.getStartTime());
        assertFalse(startDateTime.isAfter(DateTime.now()));

        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertEquals("obd.send.new.messages", motechEvent.getSubject());
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendNewMessagesToOBD() {
        NewMessagesSenderJob newMessagesSenderJob = new NewMessagesSenderJob(campaignMessageService, retryService, obdProperties);
        DateTime before = DateTime.now();

        newMessagesSenderJob.sendMessages(new MotechEvent(""));

        DateTime after = DateTime.now();
        ArgumentCaptor<RetryRequest> captor = ArgumentCaptor.forClass(RetryRequest.class);
        verify(retryService).schedule(captor.capture());
        RetryRequest retryRequest = captor.getValue();
        assertEquals("obd-send-new-messages",retryRequest.getName() );
        assertNotNull(retryRequest.getExternalId());
        DateTime referenceTime = retryRequest.getReferenceTime();
        assertTrue(after.isEqual(referenceTime) || after.isAfter(referenceTime));
        assertTrue(before.isEqual(referenceTime) || before.isBefore(referenceTime));
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendNewMessagesWithRetryAndFulfillTheRetryIfSuccessful() {
        NewMessagesSenderJob newMessagesSenderJob = new NewMessagesSenderJob(campaignMessageService, retryService, obdProperties);
        newMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>(){{
            put("EXTERNAL_ID", "myExternalId");
        }}));

        verify(campaignMessageService).sendNewMessages();

        verify(retryService).fulfill("myExternalId", "obd-send-new-messages-group");
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendNewMessagesWithRetryAndNotFulfillTheRetryIfNotSuccessful() {
        NewMessagesSenderJob newMessagesSenderJob = new NewMessagesSenderJob(campaignMessageService, retryService, obdProperties);
        doThrow(new RuntimeException("some exception")).when(campaignMessageService).sendNewMessages();

        newMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>(){{
            put("EXTERNAL_ID", "myExternalId");
        }}));


        verifyZeroInteractions(retryService);
    }
}
