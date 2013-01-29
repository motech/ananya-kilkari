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
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetrySlotMessagesSenderJobTest {
    @Mock
    private OBDProperties obdProperties;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private RetryService retryService;

    private RetrySlotMessagesSenderJob retrySlotMessagesSenderJob;
    private String cronJobExpression1 = "myfirstcronjobexpression";
    private String cronJobExpression2 = "mysecondcronjobexpression";
    private static final String SUB_SLOT_KEY = "sub_slot";

    @Before
    public void setUp() {
        initMocks(this);
        when(obdProperties.getRetrySlotCronJobExpressionFor(SubSlot.ONE)).thenReturn(cronJobExpression1);
        when(obdProperties.getRetrySlotCronJobExpressionFor(SubSlot.THREE)).thenReturn(cronJobExpression2);
        retrySlotMessagesSenderJob = new RetrySlotMessagesSenderJob(campaignMessageService, retryService, obdProperties);
    }

    @Test
    public void shouldScheduleCronJobsAtConstruction() {
        List<CronSchedulableJob> cronJobs = retrySlotMessagesSenderJob.getCronJobs();
        MotechEvent expectedEvent1 = new MotechEvent("obd.send.retry.messages", new HashMap<String, Object>() {{
            put(SUB_SLOT_KEY, SubSlot.ONE);
        }});
        MotechEvent expectedEvent2 = new MotechEvent("obd.send.retry.messages", new HashMap<String, Object>() {{
            put(SUB_SLOT_KEY, SubSlot.ONE);
        }});

        assertEquals(2, cronJobs.size());
        cronJobs.contains(new CronSchedulableJob(expectedEvent1, cronJobExpression1));
        cronJobs.contains(new CronSchedulableJob(expectedEvent2, cronJobExpression2));
    }

    @Test
    public void shouldScheduleToSendRetryMessagesToOBD() {
        DateTime before = DateTime.now();
        Map<String, Object> expectedParameters = new HashMap<>();
        expectedParameters.put(SUB_SLOT_KEY, SubSlot.ONE);

        retrySlotMessagesSenderJob.handleMessages(new MotechEvent("", expectedParameters));

        DateTime after = DateTime.now();

        ArgumentCaptor<RetryRequest> captor = ArgumentCaptor.forClass(RetryRequest.class);
        verify(retryService).schedule(captor.capture(), eq(expectedParameters));
        RetryRequest retryRequest = captor.getValue();
        assertEquals("obd-send-retry-slot-messages", retryRequest.getName());
        assertNotNull(retryRequest.getExternalId());
        DateTime referenceTime = retryRequest.getReferenceTime();
        assertTrue(after.isEqual(referenceTime) || after.isAfter(referenceTime));
        assertTrue(before.isEqual(referenceTime) || before.isBefore(referenceTime));
    }


    @Test
    public void shouldInvokeCampaignMessageServiceToSendRetryMessagesWithRetryAndFulfillTheRetryIfSuccessful() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(16).withMinuteOfHour(0).getMillis());
        final SubSlot subSlot = SubSlot.ONE;
        when(obdProperties.getRetrySlotStartTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(15).withMinuteOfHour(45));
        when(obdProperties.getRetrySlotEndTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(16).withMinuteOfHour(45));

        retrySlotMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
            put(SUB_SLOT_KEY, subSlot);
        }}));

        verify(obdProperties).getRetrySlotStartTimeLimitFor(subSlot);
        verify(campaignMessageService).sendRetrySlotMessages(subSlot);

        verify(retryService).fulfill("myExternalId", "obd-send-retry-slot-messages-group");

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldInvokeCampaignMessageServiceToSendRetryMessagesWithRetryAndNotFulfillTheRetryIfNotSuccessful() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(16).withMinuteOfHour(0).getMillis());
        final SubSlot subSlot = SubSlot.THREE;
        when(obdProperties.getRetrySlotStartTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(15).withMinuteOfHour(45));
        when(obdProperties.getRetrySlotEndTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(16).withMinuteOfHour(45));

        doThrow(new RuntimeException("some exception")).when(campaignMessageService).sendRetrySlotMessages(subSlot);

        retrySlotMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
            put(SUB_SLOT_KEY, subSlot);
        }}));

        verify(obdProperties).getRetrySlotStartTimeLimitFor(subSlot);
        verifyZeroInteractions(retryService);

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldNotSendRetryMessagesIfTimeIsAfterTheRetryMessagesSlot() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().withHourOfDay(18).withMinuteOfHour(0).getMillis());

        final SubSlot subSlot = SubSlot.ONE;
        when(obdProperties.getRetrySlotStartTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(16).withMinuteOfHour(45));
        when(obdProperties.getRetrySlotEndTimeLimitFor(subSlot)).thenReturn(DateTime.now().withHourOfDay(17).withMinuteOfHour(45));

        retrySlotMessagesSenderJob.sendMessagesWithRetry(new MotechEvent("some subject", new HashMap<String, Object>() {{
            put("EXTERNAL_ID", "myExternalId");
            put(SUB_SLOT_KEY, subSlot);
        }}));

        verify(campaignMessageService, never()).sendRetrySlotMessages(subSlot);
        verify(retryService).fulfill("myExternalId", "obd-send-retry-slot-messages-group");

        DateTimeUtils.setCurrentMillisSystem();
    }
}