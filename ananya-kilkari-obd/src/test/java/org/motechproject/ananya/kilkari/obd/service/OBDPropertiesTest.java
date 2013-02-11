package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.scheduler.MainSubSlot;
import org.motechproject.ananya.kilkari.obd.scheduler.RetrySubSlot;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDPropertiesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Properties properties;
    private OBDProperties obdProperties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldMapNAStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn(",,  iu_na1,iu_na2,  iu_na3,  ");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("iu_nd1, ");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");

        obdProperties = new OBDProperties(properties);

        assertEquals(CampaignMessageStatus.NA, obdProperties.getCampaignMessageStatusFor("iu_na1"));
        assertEquals(CampaignMessageStatus.NA, obdProperties.getCampaignMessageStatusFor("iu_na2"));
        assertEquals(CampaignMessageStatus.NA, obdProperties.getCampaignMessageStatusFor("iu_na3"));
    }

    @Test
    public void shouldMapNDStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn(",,  iu_nd1,iu_nd2,  iu_nd3,  ");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("iu_so1,");

        obdProperties = new OBDProperties(properties);

        assertEquals(CampaignMessageStatus.ND, obdProperties.getCampaignMessageStatusFor("iu_nd1"));
        assertEquals(CampaignMessageStatus.ND, obdProperties.getCampaignMessageStatusFor("iu_nd2"));
        assertEquals(CampaignMessageStatus.ND, obdProperties.getCampaignMessageStatusFor("iu_nd3"));
    }

    @Test
    public void shouldMapSOStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn(",,  iu_nd1,iu_nd2,  iu_nd3,  ");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("iu_so1, iu_so2");

        obdProperties = new OBDProperties(properties);

        assertEquals(CampaignMessageStatus.SO, obdProperties.getCampaignMessageStatusFor("iu_so1"));
        assertEquals(CampaignMessageStatus.SO, obdProperties.getCampaignMessageStatusFor("iu_so2"));
    }

    @Test
    public void shouldThrowExceptionIfNAPropertyIsNotDefined() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("campaign.message.na.status.codes property should be available");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        new OBDProperties(properties);
    }

    @Test
    public void shouldThrowExceptionIfNDPropertyIsNotDefined() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("campaign.message.nd.status.codes property should be available");
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        new OBDProperties(properties);
    }

    @Test
    public void shouldThrowExceptionIfSOPropertyIsNotDefined() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("campaign.message.so.status.codes property should be available");
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        new OBDProperties(properties);
    }

    @Test
    public void shouldGetCronJobExpression() {
        defaultSetup();
        when(properties.getProperty("obd.main.one.sub.slot.cron.job.expression")).thenReturn("0 0 12 * * ?");
        when(properties.getProperty("obd.retry.three.sub.slot.cron.job.expression")).thenReturn("0 30 14 * * ?");

        assertEquals("0 0 12 * * ?", obdProperties.getCronJobExpressionFor(MainSubSlot.ONE));
        assertEquals("0 30 14 * * ?", obdProperties.getCronJobExpressionFor(RetrySubSlot.THREE));
    }

    @Test
    public void shouldGetSlotStartTimeLimits() {
        defaultSetup();
        when(properties.getProperty("obd.main.two.sub.slot.start.time.limit")).thenReturn("13:30");
        when(properties.getProperty("obd.retry.three.sub.slot.start.time.limit")).thenReturn("16:45");

        DateTime mainSlotStartTimeLimit = obdProperties.getSlotStartTimeLimitFor(MainSubSlot.TWO);
        assertEquals(13, mainSlotStartTimeLimit.getHourOfDay());
        assertEquals(30, mainSlotStartTimeLimit.getMinuteOfHour());
        DateTime retrySlotStartTime = obdProperties.getSlotStartTimeLimitFor(RetrySubSlot.THREE);
        assertEquals(16, retrySlotStartTime.getHourOfDay());
        assertEquals(45, retrySlotStartTime.getMinuteOfHour());
    }

    @Test
    public void shouldGetSlotEndTimeLimits() {
        defaultSetup();
        when(properties.getProperty("obd.main.two.sub.slot.end.time.limit")).thenReturn("13:30");
        when(properties.getProperty("obd.retry.three.sub.slot.end.time.limit")).thenReturn("16:45");

        DateTime mainSlotStartTimeLimit = obdProperties.getSlotEndTimeLimitFor(MainSubSlot.TWO);
        assertEquals(13, mainSlotStartTimeLimit.getHourOfDay());
        assertEquals(30, mainSlotStartTimeLimit.getMinuteOfHour());
        DateTime retrySlotStartTime = obdProperties.getSlotEndTimeLimitFor(RetrySubSlot.THREE);
        assertEquals(16, retrySlotStartTime.getHourOfDay());
        assertEquals(45, retrySlotStartTime.getMinuteOfHour());
    }

    @Test
    public void shouldGetTimeSlots() {
        defaultSetup();
        when(properties.getProperty("obd.main.one.sub.slot.start.time")).thenReturn("String1");
        when(properties.getProperty("obd.main.three.sub.slot.end.time")).thenReturn("String2");
        when(properties.getProperty("obd.retry.one.sub.slot.start.time")).thenReturn("String3");
        when(properties.getProperty("obd.retry.three.sub.slot.end.time")).thenReturn("String4");

        String mainSlotStartTime = obdProperties.getSlotStartTimeFor(MainSubSlot.ONE);
        String mainSlotEndTime = obdProperties.getSlotEndTimeFor(MainSubSlot.THREE);
        String retrySlotStartTime = obdProperties.getSlotStartTimeFor(RetrySubSlot.ONE);
        String retrySlotEndTime = obdProperties.getSlotEndTimeFor(RetrySubSlot.THREE);

        assertEquals("String1", mainSlotStartTime);
        assertEquals("String2", mainSlotEndTime);
        assertEquals("String3", retrySlotStartTime);
        assertEquals("String4", retrySlotEndTime);
    }

    @Test
    public void shouldGetMainSubSlotsMessagePercentageToSend() {
        defaultSetup();
        when(properties.getProperty("obd.main.one.sub.slot.message.percentage.to.send")).thenReturn("30");
        when(properties.getProperty("obd.main.two.sub.slot.message.percentage.to.send")).thenReturn("60");

        assertEquals(30, (int) obdProperties.getSlotMessagePercentageFor(MainSubSlot.ONE));
        assertEquals(60, (int) obdProperties.getSlotMessagePercentageFor(MainSubSlot.TWO));
        assertNull(obdProperties.getSlotMessagePercentageFor(MainSubSlot.THREE));
        assertNull(obdProperties.getSlotMessagePercentageFor(RetrySubSlot.ONE));
        assertNull(obdProperties.getSlotMessagePercentageFor(RetrySubSlot.TWO));
        assertNull(obdProperties.getSlotMessagePercentageFor(RetrySubSlot.THREE));
    }

    @Test
    public void shouldGetRetryPropertiesUpdatingMessages(){
        defaultSetup();
        when(properties.getProperty("obd.retry.sent.messages.update.initial.wait")).thenReturn("2");
        when(properties.getProperty("obd.retry.sent.messages.update.max.retry.count")).thenReturn("2");
        when(properties.getProperty("obd.retry.sent.messages.update.retry.interval")).thenReturn("2");

        assertEquals(2, (int) obdProperties.getRetryIntervalForMessageUpdate());
        assertEquals(2, (int) obdProperties.getRetryCountForMessageUpdate());
        assertEquals(2, (int) obdProperties.getInitialWaitForMessageUpdate());
    }

    private void defaultSetup() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");

        obdProperties = new OBDProperties(properties);
    }
}