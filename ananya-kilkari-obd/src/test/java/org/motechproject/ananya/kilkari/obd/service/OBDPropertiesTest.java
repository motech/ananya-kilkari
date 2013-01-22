package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.scheduler.SubSlot;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDPropertiesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Properties properties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldMapNAStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn(",,  iu_na1,iu_na2,  iu_na3,  ");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("iu_nd1, ");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals(CampaignMessageStatus.NA, obdProperties.getCampaignMessageStatusFor("iu_na1"));
        assertEquals(CampaignMessageStatus.NA, obdProperties.getCampaignMessageStatusFor("iu_na2"));
        assertEquals(CampaignMessageStatus.NA, obdProperties.getCampaignMessageStatusFor("iu_na3"));
    }

    @Test
    public void shouldMapNDStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn(",,  iu_nd1,iu_nd2,  iu_nd3,  ");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("iu_so1,");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals(CampaignMessageStatus.ND, obdProperties.getCampaignMessageStatusFor("iu_nd1"));
        assertEquals(CampaignMessageStatus.ND, obdProperties.getCampaignMessageStatusFor("iu_nd2"));
        assertEquals(CampaignMessageStatus.ND, obdProperties.getCampaignMessageStatusFor("iu_nd3"));
    }

    @Test
    public void shouldMapSOStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn(",,  iu_nd1,iu_nd2,  iu_nd3,  ");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("iu_so1, iu_so2");

        OBDProperties obdProperties = new OBDProperties(properties);

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
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        when(properties.getProperty("obd.main.sub.slot.one.cron.job.expression")).thenReturn("0 0 12 * * ?");
        when(properties.getProperty("obd.retry.sub.slot.three.cron.job.expression")).thenReturn("0 30 14 * * ?");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals("0 0 12 * * ?", obdProperties.getMainSlotCronJobExpressionFor(SubSlot.ONE.name()));
        assertEquals("0 30 14 * * ?", obdProperties.getRetrySlotCronJobExpressionFor(SubSlot.THREE.name()));
    }

    @Test
    public void shouldGetSlotStartTimeLimits() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        when(properties.getProperty("obd.main.sub.slot.two.start.time.limit")).thenReturn("13:30");
        when(properties.getProperty("obd.retry.sub.slot.three.start.time.limit")).thenReturn("16:45");
        OBDProperties obdProperties = new OBDProperties(properties);

        DateTime mainSlotStartTimeLimit = obdProperties.getMainSlotStartTimeLimitFor(SubSlot.TWO.name());
        assertEquals(13, mainSlotStartTimeLimit.getHourOfDay());
        assertEquals(30, mainSlotStartTimeLimit.getMinuteOfHour());
        DateTime retrySlotStartTime = obdProperties.getRetrySlotStartTimeLimitFor(SubSlot.THREE.name());
        assertEquals(16, retrySlotStartTime.getHourOfDay());
        assertEquals(45, retrySlotStartTime.getMinuteOfHour());
    }

    @Test
    public void shouldGetTimeSlots(){
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        when(properties.getProperty("obd.main.sub.slot.one.start.time")).thenReturn("String1");
        when(properties.getProperty("obd.main.sub.slot.three.end.time")).thenReturn("String2");
        when(properties.getProperty("obd.retry.sub.slot.one.start.time")).thenReturn("String3");
        when(properties.getProperty("obd.retry.sub.slot.three.end.time")).thenReturn("String4");
        OBDProperties obdProperties = new OBDProperties(properties);

        String mainSlotStartTime = obdProperties.getMainSlotStartTimeFor(SubSlot.ONE.name());
        String mainSlotEndTime = obdProperties.getMainSlotEndTimeFor(SubSlot.THREE.name());
        String retrySlotStartTime = obdProperties.getRetrySlotStartTimeFor(SubSlot.ONE.name());
        String retrySlotEndTime = obdProperties.getRetrySlotEndTimeFor(SubSlot.THREE.name());

        assertEquals("String1", mainSlotStartTime);
        assertEquals("String2", mainSlotEndTime);
        assertEquals("String3", retrySlotStartTime);
        assertEquals("String4", retrySlotEndTime);
    }
}