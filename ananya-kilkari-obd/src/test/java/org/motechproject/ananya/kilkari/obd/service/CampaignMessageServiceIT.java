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
import org.motechproject.ananya.kilkari.obd.domain.MainSubSlot;
import org.motechproject.ananya.kilkari.obd.domain.RetrySubSlot;
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
        allCampaignMessages.add(new CampaignMessage("subscriptionId2", "messageId2", DateTime.now(), "9876543210", "operator2", DateTime.now().plusDays(3)));
        DateTime weekEndingDate = DateTime.now().plusDays(2);
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, DateTime.now(), msisdn, operator, weekEndingDate));

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
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, messageId, DateTime.now(), "1234567890", null, DateTime.now().plusDays(2));
        allCampaignMessages.add(campaignMessage);

        campaignMessageService.deleteCampaignMessage(campaignMessage);

        assertTrue(allCampaignMessages.getAll().isEmpty());
    }

    @Test
    public void shouldDeleteTheCampaignMessageIfItExists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, DateTime.now(), "1234567890", null, DateTime.now().plusDays(2)));

        campaignMessageService.deleteCampaignMessageIfExists(subscriptionId, messageId);

        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);
        assertNull(campaignMessage);
    }

    @Test
    public void shouldNotDeleteTheCampaignMessageIfItDoesNotExists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, DateTime.now(), "1234567890", null, DateTime.now().plusDays(2)));

        campaignMessageService.deleteCampaignMessageIfExists("subscriptionId2", messageId);

        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);
        assertNotNull(campaignMessage);
    }

    @Test
    public void shouldUpdateTheCampaignMessage() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, messageId, DateTime.now(), "1234567890", null, DateTime.now().plusDays(2));
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

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2), DateTime.now());

        List<CampaignMessage> all = allCampaignMessages.getAll();
        for (CampaignMessage campaignMessage : all) {
            if (equals(new CampaignMessage(subscriptionId, messageId, DateTime.now(), msisdn, operator, DateTime.now().plusDays(2)), campaignMessage)) {
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
    public void shouldSendFirstMainSubSlotCampaignMessagesToOBD() {
        String subscriptionId1 = "subscriptionId1";
        String messageId1 = "messageId1";
        String msisdn1 = "1234567890";
        String subscriptionId2 = "subscriptionId2";
        String messageId2 = "messageId2";
        String msisdn2 = "1234567891";
        String operator = "airtel";

        campaignMessageService.scheduleCampaignMessage(subscriptionId1, messageId1, msisdn1, operator, DateTime.now().plusDays(2), DateTime.now());
        campaignMessageService.scheduleCampaignMessage(subscriptionId2, messageId2, msisdn2, operator, DateTime.now(), DateTime.now().minusMinutes(5));

        OnMobileOBDGateway mockOnMobileOBDGateway = Mockito.mock(OnMobileOBDGateway.class);
        onMobileOBDGateway.setBehavior(mockOnMobileOBDGateway);

        campaignMessageService.sendFirstMainSubSlotMessages(MainSubSlot.ONE);

        verify(mockOnMobileOBDGateway).sendMessages("1234567891,messageId2,subscriptionId2,airtel\n", MainSubSlot.ONE);
    }

    @Test
    public void shouldSendSecondMainSubSlotCampaignMessagesToOBD() {
        CampaignMessage campaignMessage1 = new CampaignMessage("subscriptionId1", "messageId1", DateTime.now(), "1234567891", "airtel", DateTime.now());
        CampaignMessage campaignMessage2 = new CampaignMessage("subscriptionId2", "messageId2", DateTime.now(), "1234567892", "airtel", DateTime.now().minusDays(2));
        CampaignMessage campaignMessage3 = new CampaignMessage("subscriptionId3", "messageId3", DateTime.now(), "1234567893", "airtel", DateTime.now().plusDays(3));
        CampaignMessage campaignMessage4 = new CampaignMessage("subscriptionId4", "messageId4", DateTime.now(), "1234567894", "airtel", DateTime.now().plusDays(3));
        CampaignMessage campaignMessage5 = new CampaignMessage("subscriptionId5", "messageId5", DateTime.now(), "1234567895", "airtel", DateTime.now());
        CampaignMessage campaignMessage6 = new CampaignMessage("subscriptionId6", "messageId6", DateTime.now(), "1234567896", "airtel", DateTime.now());
        CampaignMessage campaignMessage7 = new CampaignMessage("subscriptionId7", "messageId7", DateTime.now(), "1234567897", "airtel", DateTime.now());
        CampaignMessage campaignMessage8 = new CampaignMessage("subscriptionId8", "messageId8", DateTime.now(), "1234567898", "airtel", DateTime.now().plusDays(6));
        campaignMessage8.setStatusCode(CampaignMessageStatus.ND);
        allCampaignMessages.add(campaignMessage1);
        allCampaignMessages.add(campaignMessage2);
        allCampaignMessages.add(campaignMessage3);
        allCampaignMessages.add(campaignMessage4);
        allCampaignMessages.add(campaignMessage5);
        allCampaignMessages.add(campaignMessage6);
        allCampaignMessages.add(campaignMessage7);
        allCampaignMessages.add(campaignMessage8);

        OnMobileOBDGateway mockOnMobileOBDGateway = Mockito.mock(OnMobileOBDGateway.class);
        onMobileOBDGateway.setBehavior(mockOnMobileOBDGateway);

        campaignMessageService.sendSecondMainSubSlotMessages(MainSubSlot.TWO);

        verify(mockOnMobileOBDGateway).sendMessages("1234567898,messageId8,subscriptionId8,airtel\n1234567892,messageId2,subscriptionId2,airtel\n" +
                "1234567891,messageId1,subscriptionId1,airtel\n1234567895,messageId5,subscriptionId5,airtel\n" +
                "1234567896,messageId6,subscriptionId6,airtel\n1234567897,messageId7,subscriptionId7,airtel\n" +
                "1234567893,messageId3,subscriptionId3,airtel\n", MainSubSlot.TWO);
    }

    @Test
    public void shouldSendThirdMainSubSlotCampaignMessagesToOBD() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        String operator = "airtel";
        String msisdn = "1234567890";

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2), DateTime.now());
        markForDeletion(allCampaignMessages.find(subscriptionId, messageId));

        OnMobileOBDGateway mockOnMobileOBDGateway = Mockito.mock(OnMobileOBDGateway.class);
        onMobileOBDGateway.setBehavior(mockOnMobileOBDGateway);

        campaignMessageService.sendThirdMainSubSlotMessages(MainSubSlot.THREE);

        verify(mockOnMobileOBDGateway).sendMessages("1234567890,messageId,subscriptionId,airtel\n", MainSubSlot.THREE);
    }

    @Test
    public void shouldSendRetrySlotCampaignMessagesToOBD() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        String operator = "airtel";
        String msisdn = "1234567890";

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator, DateTime.now().plusDays(2), DateTime.now());
        CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionId, messageId);
        campaignMessage.setStatusCode(CampaignMessageStatus.NA);
        allCampaignMessages.update(campaignMessage);

        markForDeletion(campaignMessage);

        OnMobileOBDGateway mockOnMobileOBDGateway = Mockito.mock(OnMobileOBDGateway.class);
        onMobileOBDGateway.setBehavior(mockOnMobileOBDGateway);

        campaignMessageService.sendRetrySlotMessages(RetrySubSlot.THREE);

        verify(mockOnMobileOBDGateway).sendMessages("1234567890,messageId,subscriptionId,airtel\n", RetrySubSlot.THREE);
    }
}
