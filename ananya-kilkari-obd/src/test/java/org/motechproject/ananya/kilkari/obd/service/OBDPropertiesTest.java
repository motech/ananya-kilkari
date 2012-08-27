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
    public void shouldMapDNPStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.dnp.status.codes")).thenReturn(",,  iu_dnp1,iu_dnp2,  iu_dnp3,  ");
        when(properties.getProperty("campaign.message.dnc.status.codes")).thenReturn("");
        OBDProperties obdProperties = new OBDProperties(properties);
        assertEquals(CampaignMessageStatus.DNP, obdProperties.getCampaignMessageStatusFor("iu_dnp1"));
        assertEquals(CampaignMessageStatus.DNP, obdProperties.getCampaignMessageStatusFor("iu_dnp2"));
        assertEquals(CampaignMessageStatus.DNP, obdProperties.getCampaignMessageStatusFor("iu_dnp3"));
    }

    @Test
    public void shouldMapDNCStatusCodeToCampaignMessageStatus() {
        when(properties.getProperty("campaign.message.dnp.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.dnc.status.codes")).thenReturn(",,  iu_dnc1,iu_dnc2,  iu_dnc3,  ");
        OBDProperties obdProperties = new OBDProperties(properties);
        assertEquals(CampaignMessageStatus.DNC, obdProperties.getCampaignMessageStatusFor("iu_dnc1"));
        assertEquals(CampaignMessageStatus.DNC, obdProperties.getCampaignMessageStatusFor("iu_dnc2"));
        assertEquals(CampaignMessageStatus.DNC, obdProperties.getCampaignMessageStatusFor("iu_dnc3"));
    }

    @Test
    public void shouldThrowExceptionIfDNPPropertyIsNotDefined() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("campaign.message.dnp.status.codes property should be available");
        when(properties.getProperty("campaign.message.dnc.status.codes")).thenReturn("");
        new OBDProperties(properties);
    }

    @Test
    public void shouldThrowExceptionIfDNCPropertyIsNotDefined() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("campaign.message.dnc.status.codes property should be available");
        when(properties.getProperty("campaign.message.dnp.status.codes")).thenReturn("");
        new OBDProperties(properties);
    }

    @Test
    public void shouldGetNewMessagesJobCronExpression() {
        when(properties.getProperty("campaign.message.dnp.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.dnc.status.codes")).thenReturn("");
        when(properties.getProperty("obd.new.messages.job.cron.expression")).thenReturn("****");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals("****", obdProperties.getNewMessageJobCronExpression());
    }

    @Test
    public void shouldGetRetryMessagesJobCronExpression() {
        when(properties.getProperty("campaign.message.dnp.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.dnc.status.codes")).thenReturn("");
        when(properties.getProperty("obd.retry.messages.job.cron.expression")).thenReturn("****");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals("****", obdProperties.getRetryMessageJobCronExpression());
    }

    @Test
    public void shouldGetSlotStartAndEndTimes() {
        when(properties.getProperty("campaign.message.dnp.status.codes")).thenReturn("");
        when(properties.getProperty("campaign.message.dnc.status.codes")).thenReturn("");
        when(properties.getProperty("obd.new.message.start.time.limit")).thenReturn("13:45");
        when(properties.getProperty("obd.retry.message.start.time.limit")).thenReturn("16:30");

        OBDProperties obdProperties = new OBDProperties(properties);

        assertEquals(13, obdProperties.getNewMessageStartTimeLimitHours());
        assertEquals(45, obdProperties.getNewMessageStartTimeLimitMinute());
        assertEquals(16, obdProperties.getRetryMessageStartTimeLimitHours());
        assertEquals(30, obdProperties.getRetryMessageStartTimeLimitMinute());
    }

}