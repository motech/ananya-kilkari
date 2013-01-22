package org.motechproject.ananya.kilkari.obd.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.scheduler.SubSlot;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class CampaignMessageServiceIT extends SpringIntegrationTest {

    @Autowired
    private CampaignMessageService campaignMessageService;

    @Autowired
    private AllCampaignMessages allCampaignMessages;

    @Autowired
    private StubOnMobileOBDGateway onMobileOBDGateway;

    @After
    @Before
    public void setUp() {
        allCampaignMessages.removeAll();
    }

    @Test
    public void shouldFindTheCampaignMessage() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        String msisdn = "1234567890";
        String operator = "airtel";
        allCampaignMessages.add(new CampaignMessage("subscriptionId2", "messageId2", "9876543210", "operator2", DateTime.now().plusDays(3)));
        DateTime weekEndingDate = DateTime.now().plusDays(2);
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, msisdn, operator, weekEndingDate));

        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);

        assertNotNull(campaignMessage);
        assertEquals(subscriptionId, campaignMessage.getSubscriptionId());
        assertEquals(messageId, campaignMessage.getMessageId());
        assertEquals(msisdn, campaignMessage.getMsisdn());
        assertEquals(operator, campaignMessage.getOperator());
        assertEquals(weekEndingDate.withZone(DateTimeZone.UTC), campaignMessage.getWeekEndingDate());
    }

    @Test
    public void shouldDeleteTheCampaignMessage() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, messageId, "1234567890", null, DateTime.now().plusDays(2));
        allCampaignMessages.add(campaignMessage);

        campaignMessageService.deleteCampaignMessage(campaignMessage);

        assertTrue(allCampaignMessages.getAll().isEmpty());
    }

    @Test
    public void shouldDeleteTheCampaignMessageIfItExists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, "1234567890", null, DateTime.now().plusDays(2)));

        campaignMessageService.deleteCampaignMessageIfExists(subscriptionId, messageId);

        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);
        assertNull(campaignMessage);
    }

    @Test
    public void shouldNotDeleteTheCampaignMessageIfItDoesNotExists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, "1234567890", null, DateTime.now().plusDays(2)));

        campaignMessageService.deleteCampaignMessageIfExists("subscriptionId2", messageId);

        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);
        assertNotNull(campaignMessage);
    }

    @Test
    public void shouldUpdateTheCampaignMessage() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, messageId, "1234567890", null, DateTime.now().plusDays(2));
        allCampaignMessages.add(campaignMessage);
        assertEquals(CampaignMessageStatus.NEW, campaignMessage.getStatus());
        campaignMessage.setStatusCode(CampaignMessageStatus.NA);

        campaignMessageService.update(campaignMessage);

        CampaignMessage updatedCampaignMessage = allCampaignMessages.find("subscriptionId", "messageId");
        assertEquals(CampaignMessageStatus.NA, updatedCampaignMessage.getStatus());
    }

    @Test
    public void shouldSaveTheCampaignMessageToDB() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        String operator = "airtel";
        String msisdn = "1234567890";

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2));

        List<CampaignMessage> all = allCampaignMessages.getAll();
        for (CampaignMessage campaignMessage : all) {
            if (equals(new CampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2)), campaignMessage)) {
                markForDeletion(campaignMessage);
                return;
            }
        }
        fail("Should have found created campaign message");
    }

    private boolean equals(CampaignMessage expected, CampaignMessage actual) {
        if (actual == null) {
            return false;
        }
        return new EqualsBuilder()
                .append(expected.getMessageId(), actual.getMessageId())
                .append(expected.getSubscriptionId(), actual.getSubscriptionId())
                .append(expected.getMsisdn(), actual.getMsisdn())
                .append(expected.getOperator(), expected.getOperator())
                .isEquals();
    }

    @Test
    public void shouldSendNewCampaignMessagesToOBD() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        String operator = "airtel";
        String msisdn = "1234567890";

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2));
        markForDeletion(allCampaignMessages.find(subscriptionId, messageId));

        OnMobileOBDGateway mockOnMobileOBDGateway = Mockito.mock(OnMobileOBDGateway.class);
        onMobileOBDGateway.setBehavior(mockOnMobileOBDGateway);

        campaignMessageService.sendNewMessages(SubSlot.ONE);

        verify(mockOnMobileOBDGateway).sendNewMessages("1234567890,messageId,subscriptionId,airtel\n", SubSlot.ONE);
    }

    @Test
    public void shouldSendRetryCampaignMessagesToOBD() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        String operator = "airtel";
        String msisdn = "1234567890";

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2));
        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);
        campaignMessage.setStatusCode(CampaignMessageStatus.NA);
        allCampaignMessages.update(campaignMessage);

        markForDeletion(campaignMessage);

        OnMobileOBDGateway mockOnMobileOBDGateway = Mockito.mock(OnMobileOBDGateway.class);
        onMobileOBDGateway.setBehavior(mockOnMobileOBDGateway);

        campaignMessageService.sendRetryMessages(SubSlot.THREE);

        verify(mockOnMobileOBDGateway).sendRetryMessages("1234567890,messageId,subscriptionId,airtel\n", SubSlot.THREE);
    }
}
