package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetryMessagesSenderJobTest {
    @Mock
    private Properties obdProperties;

    @Mock
    private CampaignMessageService campaignMessageService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldScheduleCronJobsAtConstruction() {
        String cronJobExpression = "mycronjobexpression";
        when(obdProperties.getProperty("obd.retry.messages.job.cron.expression")).thenReturn(cronJobExpression);

        CronSchedulableJob cronSchedulableJob = new RetryMessagesSenderJob(campaignMessageService, obdProperties).getCronJob();

        assertEquals(cronJobExpression, cronSchedulableJob.getCronExpression());
        assertNull(cronSchedulableJob.getEndTime());

        DateTime startDateTime = new DateTime(cronSchedulableJob.getStartTime());
        assertFalse(startDateTime.isAfter(DateTime.now()));

        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertEquals(RetryMessagesSenderJob.SEND_RETRY_MESSAGES, motechEvent.getSubject());
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendRetryMessagesToOBD() {
        RetryMessagesSenderJob retryMessagesSenderJob = new RetryMessagesSenderJob(campaignMessageService, obdProperties);
        retryMessagesSenderJob.sendMessages(new MotechEvent(""));
        verify(campaignMessageService).sendRetryMessages();
    }
}
