package org.motechproject.ananya.kilkari.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AllCampaignMessagesIT extends SpringIntegrationTest{

    @Autowired
    private AllCampaignMessages allCampaignMessages;
    private CampaignMessage newCampaignMessage;
    private CampaignMessage dncCampaignMessage;
    private CampaignMessage newCampaignMessageSent;
    private CampaignMessage dncCampaignMessageSent;
    private CampaignMessage dnpCampaignMessage;
    private CampaignMessage dnpCampaignMessageSent;

    @Before
    public void setUp() {
        newCampaignMessage = new CampaignMessage("subscriptionId1", "messageId1");

        dncCampaignMessage = new CampaignMessage("subscriptionId2", "messageId2");
        dncCampaignMessage.markSent();
        dncCampaignMessage.markDidNotCall();

        newCampaignMessageSent = new CampaignMessage("subscriptionId3", "messageId3");
        newCampaignMessageSent.markSent();

        dncCampaignMessageSent = new CampaignMessage("subscriptionId4", "messageId4");
        dncCampaignMessageSent.markSent();
        dncCampaignMessageSent.markDidNotCall();
        dncCampaignMessageSent.markSent();

        dnpCampaignMessage = new CampaignMessage("subscriptionId5", "messageId5");
        dnpCampaignMessage.markSent();
        dnpCampaignMessage.markDidNotPickup();

        dnpCampaignMessageSent = new CampaignMessage("subscriptionId6", "messageId6");
        dnpCampaignMessageSent.markSent();
        dnpCampaignMessageSent.markDidNotPickup();
        dnpCampaignMessageSent.markSent();
    }

    @After
    @Override
    public void after() {
        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(dncCampaignMessage);
        markForDeletion(dncCampaignMessageSent);
        markForDeletion(dnpCampaignMessage);
        markForDeletion(dnpCampaignMessageSent);
        super.after();
    }

    @Test
    public void shouldReturnAllTheUnsentNewMessages() {

        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(dncCampaignMessage);
        obdDbConnector.create(dncCampaignMessageSent);
        obdDbConnector.create(dnpCampaignMessage);
        obdDbConnector.create(dnpCampaignMessageSent);

        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();

        assertEquals(2, allNewMessages.size());
        CampaignMessage actualCampaignMessage1 = allNewMessages.get(0);
        CampaignMessage actualCampaignMessage2 = allNewMessages.get(1);

        assertFalse(actualCampaignMessage1.isSent());
        assertTrue(CampaignMessageStatus.DNP != actualCampaignMessage1.getStatus());

        assertFalse(actualCampaignMessage2.isSent());
        assertTrue(CampaignMessageStatus.DNP != actualCampaignMessage2.getStatus());
    }

    @Test
    public void shouldReturnAllTheUnsentRetryMessages() {

        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(dncCampaignMessage);
        obdDbConnector.create(dncCampaignMessageSent);
        obdDbConnector.create(dnpCampaignMessage);
        obdDbConnector.create(dnpCampaignMessageSent);

        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentRetryMessages();

        assertEquals(1, allRetryMessages.size());
        CampaignMessage actualCampaignMessage = allRetryMessages.get(0);

        assertFalse(actualCampaignMessage.isSent());
        assertEquals(CampaignMessageStatus.DNP, actualCampaignMessage.getStatus());
    }
}
