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

public class FreshMessagesSenderJobTest {
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
        when(obdProperties.getProperty("obd.fresh.messages.job.cron.expression")).thenReturn(cronJobExpression);

        CronSchedulableJob cronSchedulableJob = new FreshMessagesSenderJob(campaignMessageService, obdProperties).getCronJob();

        assertEquals(cronJobExpression, cronSchedulableJob.getCronExpression());
        assertNull(cronSchedulableJob.getEndTime());

        DateTime startDateTime = new DateTime(cronSchedulableJob.getStartTime());
        assertFalse(startDateTime.isAfter(DateTime.now()));

        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertEquals(FreshMessagesSenderJob.SEND_FRESH_MESSAGES, motechEvent.getSubject());
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendFreshMessagesToOBD() {
        FreshMessagesSenderJob freshMessagesSenderJob = new FreshMessagesSenderJob(campaignMessageService, obdProperties);
        freshMessagesSenderJob.sendMessages(new MotechEvent(""));
        verify(campaignMessageService).sendFreshMessages();
    }
}
