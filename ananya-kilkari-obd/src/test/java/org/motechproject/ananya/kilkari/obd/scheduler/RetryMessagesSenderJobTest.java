package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.OBDProperties;
import org.motechproject.event.MotechEvent;
import org.motechproject.retry.domain.RetryRequest;
import org.motechproject.retry.service.RetryService;
import org.motechproject.scheduler.domain.CronSchedulableJob;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetryMessagesSenderJobTest {
    @Mock
    private OBDProperties obdProperties;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private RetryService retryService;

    private RetryMessagesSenderJob retryMessagesSenderJob;
    private String cronJobExpression = "mycronjobexpression";

    @Before
    public void setUp() {
        initMocks(this);
        when(obdProperties.getRetryMessageJobCronExpression()).thenReturn(cronJobExpression);
        retryMessagesSenderJob = new RetryMessagesSenderJob(campaignMessageService, retryService, obdProperties);
    }

    @Test
    public void shouldScheduleCronJobsAtConstruction() {
        CronSchedulableJob cronSchedulableJob = retryMessagesSenderJob.getCronJob();

        assertEquals(cronJobExpression, cronSchedulableJob.getCronExpression());
        assertNull(cronSchedulableJob.getEndTime());

        DateTime startDateTime = new DateTime(cronSchedulableJob.getStartTime());
        assertFalse(startDateTime.isAfter(DateTime.now()));

        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertEquals("obd.send.retry.messages", motechEvent.getSubject());
    }

    @Test
    public void shouldScheduleToSendRetryMessagesToOBD() {
        DateTime before = DateTime.now();

        retryMessagesSenderJob.sendMessages(new MotechEvent(""));

        DateTime after = DateTime.now();

        ArgumentCaptor<RetryRequest> captor = ArgumentCaptor.forClass(RetryRequest.class);
        verify(retryService).schedule(captor.capture());
        RetryRequest retryRequest = captor.getValue();
        assertEquals("obd-send-retry-messages", retryRequest.getName());
        assertNotNull(retryRequest.getExternalId());
        DateTime referenceTime = retryRequest.getReferenceTime();
        assertTrue(after.isEqual(referenceTime) || after.isAfter(referenceTime));
        assertTrue(before.isEqual(referenceTime) || before.isBefore(referenceTime));
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendRetryMessagesWithRetryAndFulfillTheRetryIfSuccessful() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(16).withMinuteOfHour(0).getMillis());

        when(obdProperties.getRetryMessageStartTimeLimitHours()).thenReturn(16);
        when(obdProperties.getRetryMessageStartTimeLimitMinute()).thenReturn(45);

        retryMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
        }}));

        verify(campaignMessageService).sendRetryMessages();

        verify(retryService).fulfill("myExternalId", "obd-send-retry-messages-group");

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendRetryMessagesWithRetryAndNotFulfillTheRetryIfNotSuccessful() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(16).withMinuteOfHour(0).getMillis());

        when(obdProperties.getRetryMessageStartTimeLimitHours()).thenReturn(16);
        when(obdProperties.getRetryMessageStartTimeLimitMinute()).thenReturn(45);

        doThrow(new RuntimeException("some exception")).when(campaignMessageService).sendRetryMessages();

        retryMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
        }}));

        verifyZeroInteractions(retryService);

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldNotSendRetryMessagesIfTimeIsAfterTheRetryMessagesSlot() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(18).withMinuteOfHour(0).getMillis());

        when(obdProperties.getNewMessageStartTimeLimitHours()).thenReturn(16);
        when(obdProperties.getNewMessageStartTimeLimitMinute()).thenReturn(45);

        retryMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
        }}));

        verify(campaignMessageService,never()).sendNewMessages();

        DateTimeUtils.setCurrentMillisSystem();
    }
}