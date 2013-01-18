package org.motechproject.ananya.kilkari.obd.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class AllCampaignMessagesIT extends SpringIntegrationTest {

    @Autowired
    private AllCampaignMessages allCampaignMessages;
    private CampaignMessage newCampaignMessage;
    private CampaignMessage dncCampaignMessage;
    private CampaignMessage dncCampaignMessageWithoutRetryCount;
    private CampaignMessage dncCampaignMessageWithGreaterRetry;
    private CampaignMessage newCampaignMessageSent;
    private CampaignMessage dncCampaignMessageSent;
    private CampaignMessage dnpCampaignMessage;
    private CampaignMessage dnpCampaignMessageSent;
    private CampaignMessage dnpCampaignMessageUnsentWithMoreRetryCount;
    private DateTime weekEndingDate1;
    private DateTime weekEndingDate2;

    @Before
    public void setUp() {
        allCampaignMessages.removeAll();

        weekEndingDate1 = DateTime.now().withZone(DateTimeZone.UTC).plusDays(2);
        weekEndingDate2 = DateTime.now().withZone(DateTimeZone.UTC).minusDays(2);
        
        newCampaignMessage = new CampaignMessage("subscriptionId1", "messageId1", "1234657980", "operator1", weekEndingDate1);

        dncCampaignMessage = new CampaignMessage("subscriptionId2", "messageId2", "912134567890", "operator2", weekEndingDate2);
        dncCampaignMessage.setStatusCode(CampaignMessageStatus.ND);
        dncCampaignMessage.markSent();
        dncCampaignMessage.setStatusCode(CampaignMessageStatus.ND);

        dncCampaignMessageWithoutRetryCount = new CampaignMessage("subscriptionId13", "messageId2", "912134567890", "operator2", weekEndingDate1);
        dncCampaignMessageWithoutRetryCount.setStatusCode(CampaignMessageStatus.ND);

        dncCampaignMessageWithGreaterRetry = new CampaignMessage("subscriptionId12", "messageId2", "912134567890", "operator2", weekEndingDate2);
        dncCampaignMessageWithGreaterRetry.setStatusCode(CampaignMessageStatus.ND);
        dncCampaignMessageWithGreaterRetry.markSent();
        dncCampaignMessageWithGreaterRetry.markSent();
        dncCampaignMessageWithGreaterRetry.setStatusCode(CampaignMessageStatus.ND);

        newCampaignMessageSent = new CampaignMessage("subscriptionId3", "messageId3", "3124567890", "operator3", weekEndingDate1);
        newCampaignMessageSent.markSent();

        dncCampaignMessageSent = new CampaignMessage("subscriptionId4", "messageId4", "004123567890", "operator4", weekEndingDate1);
        dncCampaignMessageSent.markSent();
        dncCampaignMessage.setStatusCode(CampaignMessageStatus.ND);

        dncCampaignMessageSent.markSent();

        dnpCampaignMessage = new CampaignMessage("subscriptionId5", "messageId5", "5123467890", "operator5", weekEndingDate1);
        dnpCampaignMessage.markSent();
        dnpCampaignMessage.setStatusCode(CampaignMessageStatus.NA);

        dnpCampaignMessageUnsentWithMoreRetryCount = new CampaignMessage("subscriptionId7", "messageId7", "5128467890", "operator7", weekEndingDate1);
        dnpCampaignMessageUnsentWithMoreRetryCount.markSent();
        dnpCampaignMessageUnsentWithMoreRetryCount.setStatusCode(CampaignMessageStatus.NA);
        dnpCampaignMessageUnsentWithMoreRetryCount.markSent();
        dnpCampaignMessageUnsentWithMoreRetryCount.setStatusCode(CampaignMessageStatus.NA);

        dnpCampaignMessageSent = new CampaignMessage("subscriptionId6", "messageId6", "6123457890", "operator6", weekEndingDate1);
        dnpCampaignMessageSent.markSent();
        dnpCampaignMessageSent.setStatusCode(CampaignMessageStatus.NA);
        dnpCampaignMessageSent.markSent();
    }

    @Test
    public void shouldReturnAllTheUnsentNewMessages() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(dncCampaignMessage);
        obdDbConnector.create(dncCampaignMessageSent);
        obdDbConnector.create(dncCampaignMessageWithGreaterRetry);
        obdDbConnector.create(dncCampaignMessageWithoutRetryCount);
        obdDbConnector.create(dnpCampaignMessage);
        obdDbConnector.create(dnpCampaignMessageSent);

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(dncCampaignMessage);
        markForDeletion(dncCampaignMessageWithGreaterRetry);
        markForDeletion(dncCampaignMessageSent);
        markForDeletion(dncCampaignMessageWithoutRetryCount);
        markForDeletion(dnpCampaignMessage);
        markForDeletion(dnpCampaignMessageSent);

        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();

        assertEquals(4, allNewMessages.size());
        CampaignMessage actualCampaignMessage1 = allNewMessages.get(0);
        CampaignMessage actualCampaignMessage2 = allNewMessages.get(1);
        CampaignMessage actualCampaignMessage3 = allNewMessages.get(2);
        CampaignMessage actualCampaignMessage4 = allNewMessages.get(3);

        assertFalse(actualCampaignMessage1.isSent());
        assertEquals(CampaignMessageStatus.ND, actualCampaignMessage1.getStatus());
        assertEquals(2, actualCampaignMessage1.getNDRetryCount());
        assertEquals(weekEndingDate2, actualCampaignMessage1.getWeekEndingDate());

        assertFalse(actualCampaignMessage2.isSent());
        assertEquals(CampaignMessageStatus.ND, actualCampaignMessage2.getStatus());
        assertEquals(1, actualCampaignMessage2.getNDRetryCount());
        assertEquals(weekEndingDate2, actualCampaignMessage2.getWeekEndingDate());

        assertFalse(actualCampaignMessage3.isSent());
        assertEquals(CampaignMessageStatus.ND, actualCampaignMessage2.getStatus());
        assertEquals(weekEndingDate1, actualCampaignMessage3.getWeekEndingDate());

        assertFalse(actualCampaignMessage4.isSent());
        assertEquals(CampaignMessageStatus.NEW, actualCampaignMessage4.getStatus());
        assertEquals(weekEndingDate1, actualCampaignMessage4.getWeekEndingDate());
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
        assertEquals(CampaignMessageStatus.NA, actualCampaignMessage1.getStatus());
        assertEquals(1, actualCampaignMessage1.getNARetryCount());

        CampaignMessage actualCampaignMessage2 = allRetryMessages.get(1);
        assertFalse(actualCampaignMessage2.isSent());
        assertEquals(CampaignMessageStatus.NA, actualCampaignMessage2.getStatus());
        assertEquals(0, actualCampaignMessage2.getNARetryCount());
    }
}
