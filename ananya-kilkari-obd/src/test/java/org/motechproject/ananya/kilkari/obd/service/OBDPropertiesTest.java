package org.motechproject.ananya.kilkari.obd.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;

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
    public void shouldGetNewMessagesJobCronExpression() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        when(properties.getProperty("obd.new.messages.job.cron.expression")).thenReturn("****");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals("****", obdProperties.getNewMessageJobCronExpression());
    }

    @Test
    public void shouldGetRetryMessagesJobCronExpression() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        when(properties.getProperty("obd.retry.messages.job.cron.expression")).thenReturn("****");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals("****", obdProperties.getRetryMessageJobCronExpression());
    }

    @Test
    public void shouldGetSlotStartAndEndTimes() {
        when(properties.getProperty("campaign.message.na.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.nd.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.so.status.codes")).thenReturn("");
        when(properties.getProperty("obd.new.message.start.time.limit")).thenReturn("13:45");
        when(properties.getProperty("obd.retry.message.start.time.limit")).thenReturn("16:30");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals(13, obdProperties.getNewMessageStartTimeLimitHours());
        assertEquals(45, obdProperties.getNewMessageStartTimeLimitMinute());
        assertEquals(16, obdProperties.getRetryMessageStartTimeLimitHours());
        assertEquals(30, obdProperties.getRetryMessageStartTimeLimitMinute());
    }

}