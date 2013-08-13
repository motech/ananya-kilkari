package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.MainSubSlot;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.OBDProperties;
import org.motechproject.event.MotechEvent;
import org.motechproject.retry.domain.RetryRequest;
import org.motechproject.retry.service.RetryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SecondMainSubSlotMessagesSenderJobTest {
    @Mock
    private OBDProperties obdProperties;
    @Mock
    private RetryService retryService;
    @Mock
    private CampaignMessageService campaignMessageService;

    private SecondMainSubSlotMessagesSenderJob secondMainSubSlotMessagesSenderJob;
    private String cronJobExpression = "mycronjobexpression";
    private static final String SUB_SLOT_KEY = "sub_slot";

    @Before
    public void setUp() {
        initMocks(this);
        when(obdProperties.getCronJobExpressionFor(MainSubSlot.TWO)).thenReturn(cronJobExpression);
        secondMainSubSlotMessagesSenderJob = new SecondMainSubSlotMessagesSenderJobStub(campaignMessageService, retryService, obdProperties);
    }

    @Test
    public void shouldScheduleCronJobsAtConstruction() {
        List<CronSchedulableJob> cronJobs = secondMainSubSlotMessagesSenderJob.getCronJobs();

        assertEquals(1, cronJobs.size());
        CronSchedulableJob cronJob = cronJobs.get(0);
        assertEquals(cronJobExpression, cronJob.getCronExpression());
        assertNull(cronJob.getEndTime());

        DateTime startDateTime = new DateTime(cronJob.getStartTime());
        assertFalse(startDateTime.isAfter(DateTime.now()));

        MotechEvent motechEvent = cronJob.getMotechEvent();
        assertEquals("obd.send.main.sub.slot.two.messages", motechEvent.getSubject());
        assertEquals(MainSubSlot.TWO, motechEvent.getParameters().get(SUB_SLOT_KEY));
        assertEquals(MainSubSlot.TWO.getSlotName(), motechEvent.getParameters().get(MotechSchedulerService.JOB_ID_KEY));
    }

    @Test
    public void shouldScheduleRetryJobToSendNewMessagesToOBD() {
        DateTime before = DateTime.now();
        Map<String, Object> expectedParameters = new HashMap<>();
        expectedParameters.put(SUB_SLOT_KEY, MainSubSlot.TWO);

        secondMainSubSlotMessagesSenderJob.handleMessages(new MotechEvent("", expectedParameters));

        DateTime after = DateTime.now();
        ArgumentCaptor<RetryRequest> captor = ArgumentCaptor.forClass(RetryRequest.class);

        verify(retryService).schedule(captor.capture(), eq(expectedParameters));
        RetryRequest retryRequest = captor.getValue();
        assertEquals("obd-send-main-sub-slot-two-messages", retryRequest.getName());
        assertNotNull(retryRequest.getExternalId());
        DateTime referenceTime = retryRequest.getReferenceTime();
        assertTrue(after.isEqual(referenceTime) || after.isAfter(referenceTime));
        assertTrue(before.isEqual(referenceTime) || before.isBefore(referenceTime));
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendSecondMainSubSlotMessagesWithRetryAndFulfillTheRetryIfSuccessful() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(16).withMinuteOfHour(55).getMillis());

        final MainSubSlot subSlot = MainSubSlot.TWO;
        when(obdProperties.getSlotStartTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(16).withMinuteOfHour(00));
        when(obdProperties.getSlotEndTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(17).withMinuteOfHour(00));

        secondMainSubSlotMessagesSenderJob.handleMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
            put(SUB_SLOT_KEY, subSlot);
        }}));

        verify(obdProperties).getSlotStartTimeLimitFor(subSlot);
        verify(campaignMessageService).sendSecondMainSubSlotMessages(subSlot);

        verify(retryService).fulfill("myExternalId", "obd-send-main-sub-slot-two-messages-group");

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendNewMessagesWithRetryAndNotFulfillTheRetryIfNotSuccessful() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(18).withMinuteOfHour(0).getMillis());

        final MainSubSlot subSlot = MainSubSlot.TWO;
        when(obdProperties.getSlotStartTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(17).withMinuteOfHour(45));
        when(obdProperties.getSlotEndTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(18).withMinuteOfHour(45));

        doThrow(new RuntimeException("some exception")).when(campaignMessageService).sendSecondMainSubSlotMessages(subSlot);

        secondMainSubSlotMessagesSenderJob.handleMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
            put(SUB_SLOT_KEY, subSlot);
        }}));

        verifyZeroInteractions(retryService);

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldNotSendNewMessagesIfTimeIsAfterTheNewMessagesSlot() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(13).withMinuteOfHour(0).getMillis());

        final MainSubSlot subSlot = MainSubSlot.TWO;
        when(obdProperties.getSlotStartTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(11));
        when(obdProperties.getSlotEndTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(12));

        secondMainSubSlotMessagesSenderJob.handleMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
            put(SUB_SLOT_KEY, subSlot);
        }}));

        verify(campaignMessageService, never()).sendSecondMainSubSlotMessages(subSlot);
        verify(retryService).fulfill("myExternalId", "obd-send-main-sub-slot-two-messages-group");

        DateTimeUtils.setCurrentMillisSystem();
    }

    class SecondMainSubSlotMessagesSenderJobStub extends SecondMainSubSlotMessagesSenderJob {
        public SecondMainSubSlotMessagesSenderJobStub(CampaignMessageService campaignMessageService, RetryService retryService, OBDProperties obdProperties) {
            super();
            this.campaignMessageService = campaignMessageService;
            this.retryService = retryService;
            this.obdProperties = obdProperties;
        }
    }
}
