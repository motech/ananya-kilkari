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
    private CampaignMessage NDCampaignMessage;
    private CampaignMessage NDCampaignMessageWithoutRetryCount;
    private CampaignMessage NDCampaignMessageWithGreaterRetry;
    private CampaignMessage newCampaignMessageSent;
    private CampaignMessage NDCampaignMessageSent;
    private CampaignMessage NACampaignMessage;
    private CampaignMessage NACampaignMessageSent;
    private CampaignMessage NACampaignMessageUnsentWithMoreRetryCount;
    private CampaignMessage SOCampaignMessage;
    private CampaignMessage SOCampaignMessageSent;
    private DateTime weekEndingDate1;
    private DateTime weekEndingDate2;

    @Before
    public void setUp() {
        allCampaignMessages.removeAll();

        weekEndingDate1 = DateTime.now().withZone(DateTimeZone.UTC).plusDays(2);
        weekEndingDate2 = DateTime.now().withZone(DateTimeZone.UTC).minusDays(2);

        newCampaignMessage = new CampaignMessage("subscriptionId1", "messageId1", "1234657980", "operator1", weekEndingDate1);

        NDCampaignMessage = new CampaignMessage("subscriptionId2", "messageId2", "912134567890", "operator2", weekEndingDate2);
        NDCampaignMessage.setStatusCode(CampaignMessageStatus.ND);
        NDCampaignMessage.markSent();

        NDCampaignMessageWithoutRetryCount = new CampaignMessage("subscriptionId13", "messageId2", "912134567890", "operator2", weekEndingDate1);
        NDCampaignMessageWithoutRetryCount.setStatusCode(CampaignMessageStatus.ND);

        NDCampaignMessageWithGreaterRetry = new CampaignMessage("subscriptionId12", "messageId2", "912134567890", "operator2", weekEndingDate2);
        NDCampaignMessageWithGreaterRetry.setStatusCode(CampaignMessageStatus.ND);
        NDCampaignMessageWithGreaterRetry.markSent();
        NDCampaignMessageWithGreaterRetry.markSent();
        NDCampaignMessageWithGreaterRetry.setStatusCode(CampaignMessageStatus.ND);

        newCampaignMessageSent = new CampaignMessage("subscriptionId3", "messageId3", "3124567890", "operator3", weekEndingDate1);
        newCampaignMessageSent.markSent();

        NDCampaignMessageSent = new CampaignMessage("subscriptionId4", "messageId4", "004123567890", "operator4", weekEndingDate1);
        NDCampaignMessageSent.markSent();
        NDCampaignMessage.setStatusCode(CampaignMessageStatus.ND);

        NDCampaignMessageSent.markSent();

        NACampaignMessage = new CampaignMessage("subscriptionId5", "messageId5", "5123467890", "operator5", weekEndingDate1);
        NACampaignMessage.markSent();
        NACampaignMessage.setStatusCode(CampaignMessageStatus.NA);

        NACampaignMessageUnsentWithMoreRetryCount = new CampaignMessage("subscriptionId7", "messageId7", "5128467890", "operator7", weekEndingDate1);
        NACampaignMessageUnsentWithMoreRetryCount.markSent();
        NACampaignMessageUnsentWithMoreRetryCount.setStatusCode(CampaignMessageStatus.NA);
        NACampaignMessageUnsentWithMoreRetryCount.markSent();
        NACampaignMessageUnsentWithMoreRetryCount.setStatusCode(CampaignMessageStatus.NA);

        NACampaignMessageSent = new CampaignMessage("subscriptionId6", "messageId6", "6123457890", "operator6", weekEndingDate1);
        NACampaignMessageSent.markSent();
        NACampaignMessageSent.setStatusCode(CampaignMessageStatus.NA);
        NACampaignMessageSent.markSent();

        SOCampaignMessage = new CampaignMessage("subscriptionId7", "messageId7", "6123457890", "operator6", weekEndingDate1);
        SOCampaignMessage.setStatusCode(CampaignMessageStatus.SO);

        SOCampaignMessageSent = new CampaignMessage("subscriptionId7", "messageId7", "6123457890", "operator6", weekEndingDate1);
        SOCampaignMessage.setStatusCode(CampaignMessageStatus.SO);
        SOCampaignMessageSent.markSent();
    }

    @Test
    public void shouldReturnAllTheUnsentNewMessages() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(NDCampaignMessage);
        obdDbConnector.create(NDCampaignMessageSent);
        obdDbConnector.create(NACampaignMessage);
        obdDbConnector.create(NACampaignMessageSent);
        obdDbConnector.create(SOCampaignMessage);
        obdDbConnector.create(SOCampaignMessageSent);

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(NDCampaignMessage);
        markForDeletion(NDCampaignMessageSent);
        markForDeletion(NACampaignMessage);
        markForDeletion(NACampaignMessageSent);
        markForDeletion(SOCampaignMessage);
        markForDeletion(SOCampaignMessageSent);

        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();

        assertEquals(1, allNewMessages.size());
        CampaignMessage actualCampaignMessage1 = allNewMessages.get(0);

        assertFalse(actualCampaignMessage1.isSent());
        assertEquals(CampaignMessageStatus.NEW, actualCampaignMessage1.getStatus());
        assertEquals(0, actualCampaignMessage1.getNDRetryCount());
        assertEquals(0, actualCampaignMessage1.getNARetryCount());
        assertEquals(0, actualCampaignMessage1.getSORetryCount());
        assertEquals(weekEndingDate1, actualCampaignMessage1.getWeekEndingDate());
    }

    @Test
    public void shouldReturnAllTheUnsentRetryMessages() {

        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(NDCampaignMessage);
        obdDbConnector.create(NDCampaignMessageSent);
        obdDbConnector.create(NACampaignMessage);
        obdDbConnector.create(NACampaignMessageSent);
        obdDbConnector.create(NACampaignMessageUnsentWithMoreRetryCount);

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(NDCampaignMessage);
        markForDeletion(NDCampaignMessageSent);
        markForDeletion(NACampaignMessage);
        markForDeletion(NACampaignMessageSent);
        markForDeletion(NACampaignMessageUnsentWithMoreRetryCount);

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
