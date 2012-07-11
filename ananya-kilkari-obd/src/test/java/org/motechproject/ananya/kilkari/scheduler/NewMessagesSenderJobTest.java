package org.motechproject.ananya.kilkari.scheduler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.service.CampaignMessageService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewMessagesSenderJobTest {
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
        when(obdProperties.getProperty("obd.new.messages.job.cron.expression")).thenReturn(cronJobExpression);

        CronSchedulableJob cronSchedulableJob = new NewMessagesSenderJob(campaignMessageService, obdProperties).getCronJob();

        assertEquals(cronJobExpression, cronSchedulableJob.getCronExpression());
        assertNull(cronSchedulableJob.getEndTime());

        DateTime startDateTime = new DateTime(cronSchedulableJob.getStartTime());
        assertFalse(startDateTime.isAfter(DateTime.now()));

        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertEquals(NewMessagesSenderJob.SEND_NEW_MESSAGES, motechEvent.getSubject());
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendNewMessagesToOBD() {
        NewMessagesSenderJob newMessagesSenderJob = new NewMessagesSenderJob(campaignMessageService, obdProperties);
        newMessagesSenderJob.sendMessages(new MotechEvent(""));
        verify(campaignMessageService).sendNewMessages();
    }
}
