package org.motechproject.ananya.kilkari.obd.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.*;

public class AllCampaignMessagesIT extends SpringIntegrationTest {

    @Autowired
    private AllCampaignMessages allCampaignMessages;
    private CampaignMessage newCampaignMessage;
    private CampaignMessage dncCampaignMessage;
    private CampaignMessage newCampaignMessageSent;
    private CampaignMessage dncCampaignMessageSent;
    private CampaignMessage dnpCampaignMessage;
    private CampaignMessage dnpCampaignMessageSent;
    private CampaignMessage dnpCampaignMessageUnsentWithMoreRetryCount;

    @Before
    public void setUp() {
        allCampaignMessages.removeAll();

        newCampaignMessage = new CampaignMessage("subscriptionId1", "messageId1", "1234657980", "operator1");

        dncCampaignMessage = new CampaignMessage("subscriptionId2", "messageId2", "912134567890", "operator2");
        dncCampaignMessage.markSent();
        dncCampaignMessage.markDidNotCall();

        newCampaignMessageSent = new CampaignMessage("subscriptionId3", "messageId3", "3124567890", "operator3");
        newCampaignMessageSent.markSent();

        dncCampaignMessageSent = new CampaignMessage("subscriptionId4", "messageId4", "004123567890", "operator4");
        dncCampaignMessageSent.markSent();
        dncCampaignMessageSent.markDidNotCall();
        dncCampaignMessageSent.markSent();

        dnpCampaignMessage = new CampaignMessage("subscriptionId5", "messageId5", "5123467890", "operator5");
        dnpCampaignMessage.markSent();
        dnpCampaignMessage.setStatusCode(CampaignMessageStatus.DNP);

        dnpCampaignMessageUnsentWithMoreRetryCount = new CampaignMessage("subscriptionId7", "messageId7", "5128467890", "operator7");
        dnpCampaignMessageUnsentWithMoreRetryCount.markSent();
        dnpCampaignMessageUnsentWithMoreRetryCount.setStatusCode(CampaignMessageStatus.DNP);
        dnpCampaignMessageUnsentWithMoreRetryCount.markSent();
        dnpCampaignMessageUnsentWithMoreRetryCount.setStatusCode(CampaignMessageStatus.DNP);

        dnpCampaignMessageSent = new CampaignMessage("subscriptionId6", "messageId6", "6123457890", "operator6");
        dnpCampaignMessageSent.markSent();
        dnpCampaignMessageSent.setStatusCode(CampaignMessageStatus.DNP);
        dnpCampaignMessageSent.markSent();
    }

    @Test
    public void shouldReturnAllTheUnsentNewMessages() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(dncCampaignMessage);
        obdDbConnector.create(dncCampaignMessageSent);
        obdDbConnector.create(dnpCampaignMessage);
        obdDbConnector.create(dnpCampaignMessageSent);

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(dncCampaignMessage);
        markForDeletion(dncCampaignMessageSent);
        markForDeletion(dnpCampaignMessage);
        markForDeletion(dnpCampaignMessageSent);

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
        obdDbConnector.create(dnpCampaignMessageUnsentWithMoreRetryCount);

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(dncCampaignMessage);
        markForDeletion(dncCampaignMessageSent);
        markForDeletion(dnpCampaignMessage);
        markForDeletion(dnpCampaignMessageSent);
        markForDeletion(dnpCampaignMessageUnsentWithMoreRetryCount);

        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentRetryMessages();

        assertEquals(2, allRetryMessages.size());

        CampaignMessage actualCampaignMessage1 = allRetryMessages.get(0);
        assertFalse(actualCampaignMessage1.isSent());
        assertEquals(CampaignMessageStatus.DNP, actualCampaignMessage1.getStatus());
        assertEquals(1, actualCampaignMessage1.getDnpRetryCount());

        CampaignMessage actualCampaignMessage2 = allRetryMessages.get(1);
        assertFalse(actualCampaignMessage2.isSent());
        assertEquals(CampaignMessageStatus.DNP, actualCampaignMessage2.getStatus());
        assertEquals(0, actualCampaignMessage2.getDnpRetryCount());
    }
}
